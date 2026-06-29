package net.rim.device.api.crypto;

import java.io.IOException;
import net.rim.device.api.util.Arrays;

public final class RIMSignature {
   private static final byte[] DEFAULT_RSA_E = new byte[]{3};
   private static final byte[] RSA_PKCS1_SHA1_DIGEST_PREFIX = new byte[]{48, 33, 48, 9, 6, 5, 43, 14, 3, 2, 26, 5, 0, 4, 20};
   private static final byte[] RSA_PKCS1_MD5_DIGEST_PREFIX = new byte[]{48, 32, 48, 12, 6, 8, 42, -122, 72, -122, -9, 13, 2, 5, 5, 0, 4, 16};

   private RIMSignature() {
   }

   public static final long verify(byte[] dataBytes, int dataOffset, int dataLength, byte[] signatureBytes, int signatureOffset, byte[] publicKeyBytes) {
      if (dataBytes != null
         && dataOffset >= 0
         && dataBytes.length - dataLength >= dataOffset
         && signatureBytes != null
         && signatureOffset >= 0
         && signatureOffset <= signatureBytes.length
         && publicKeyBytes != null) {
         try {
            return verifyRIMFileSignerSignature(dataBytes, dataOffset, dataLength, signatureBytes, signatureOffset, publicKeyBytes);
         } catch (IOException var9) {
         } catch (ArrayIndexOutOfBoundsException var10) {
         }

         try {
            return verifyPGPSignature(dataBytes, dataOffset, dataLength, signatureBytes, signatureOffset, publicKeyBytes);
         } catch (IOException var7) {
         } catch (ArrayIndexOutOfBoundsException var8) {
         }

         return -1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static final long verifyRIMFileSignerSignature(
      byte[] dataBytes, int dataOffset, int dataLength, byte[] signatureBytes, int signatureOffset, byte[] publicKeyBytes
   ) {
      assertByte(signatureBytes, signatureOffset++, 31);
      assertByte(signatureBytes, signatureOffset++, 45);
      assertByte(signatureBytes, signatureOffset++, 200);
      assertByte(signatureBytes, signatureOffset++, 215);
      signatureOffset += 4;
      int timeOffset = signatureOffset;
      long signingTime = signatureBytes[signatureOffset++] & 0xFF;
      signingTime |= (signatureBytes[signatureOffset++] & 255) << 8;
      signingTime |= (signatureBytes[signatureOffset++] & 255) << 16;
      signingTime |= (signatureBytes[signatureOffset++] & 255) << 24;
      int encodedSignatureLength = signatureBytes[signatureOffset++] & 255;
      encodedSignatureLength |= (signatureBytes[signatureOffset++] & 255) << 8;
      encodedSignatureLength |= (signatureBytes[signatureOffset++] & 255) << 16;
      encodedSignatureLength |= (signatureBytes[signatureOffset++] & 255) << 24;
      byte[] e = DEFAULT_RSA_E;
      byte[] n = publicKeyBytes;
      byte[] signature = getBytes(signatureBytes, signatureOffset, encodedSignatureLength, n.length);
      Digest digest = new SHA1Digest();
      digest.update(dataBytes, dataOffset, dataLength);
      digest.update(signatureBytes, timeOffset, 4);
      return !verifyRSASignature(e, n, signature, digest, RSA_PKCS1_SHA1_DIGEST_PREFIX) ? 0 : signingTime * 1000;
   }

   private static final int checkPGPPacketHeader(byte[] data, int offset, int expectedPacketType) {
      int headerByte = data[offset] & 255;
      int expectedHeaderByte = 128 | expectedPacketType << 2;
      int lengthType = headerByte ^ expectedHeaderByte;
      if (lengthType >= 3) {
         throw new IOException();
      } else {
         return 1 + (1 << lengthType);
      }
   }

   private static final long verifyPGPSignature(
      byte[] dataBytes, int dataOffset, int dataLength, byte[] signatureBytes, int signatureOffset, byte[] publicKeyBytes
   ) {
      signatureOffset += checkPGPPacketHeader(signatureBytes, signatureOffset, 2);
      assertByte(signatureBytes, signatureOffset++, 3);
      assertByte(signatureBytes, signatureOffset++, 5);
      int additionalHashDataOffset = signatureOffset;
      assertByte(signatureBytes, signatureOffset++, 0);
      long signingTimeInSeconds = getInteger(signatureBytes, signatureOffset, 4) & -1;
      signatureOffset += 12;
      byte signingAlgorithm = signatureBytes[signatureOffset++];
      byte hashAlgorithm = signatureBytes[signatureOffset++];
      Digest digest;
      byte[] digestPrefix;
      switch (hashAlgorithm) {
         case 0:
            throw new IOException();
         case 1:
         default:
            digest = new MD5Digest();
            digestPrefix = RSA_PKCS1_MD5_DIGEST_PREFIX;
            break;
         case 2:
            digest = new SHA1Digest();
            digestPrefix = RSA_PKCS1_SHA1_DIGEST_PREFIX;
      }

      digest.update(dataBytes, dataOffset, dataLength);
      digest.update(signatureBytes, additionalHashDataOffset, 5);
      signatureOffset += 2;
      int publicKeyOffset = checkPGPPacketHeader(publicKeyBytes, 0, 6);
      byte publicKeyVersion = publicKeyBytes[publicKeyOffset++];
      switch (publicKeyVersion) {
         case 2:
            throw new IOException();
         case 3:
         default:
            publicKeyOffset += 6;
            break;
         case 4:
            publicKeyOffset += 4;
      }

      boolean verified;
      switch (signingAlgorithm) {
         case 1:
         case 3:
            verified = verifyPGPRSASignature(digest, digestPrefix, signatureBytes, signatureOffset, publicKeyBytes, publicKeyOffset);
            break;
         case 17:
            verified = verifyPGPDSASignature(digest.getDigest(), signatureBytes, signatureOffset, publicKeyBytes, publicKeyOffset);
            break;
         default:
            throw new IOException();
      }

      return !verified ? 0 : signingTimeInSeconds * 1000;
   }

   private static final boolean verifyPGPRSASignature(
      Digest digest, byte[] digestPrefix, byte[] signatureBytes, int signatureOffset, byte[] publicKeyBytes, int publicKeyOffset
   ) {
      assertByte(publicKeyBytes, publicKeyOffset++, 1);
      byte[] n = getMPI(publicKeyBytes, publicKeyOffset);
      publicKeyOffset += 2 + n.length;
      byte[] e = getMPI(publicKeyBytes, publicKeyOffset);
      byte[] signature = new byte[n.length];
      signatureOffset += getMPI(signatureBytes, signatureOffset, signature);
      return verifyRSASignature(e, n, signature, digest, digestPrefix);
   }

   private static final boolean verifyRSASignature(byte[] e, byte[] n, byte[] signature, Digest digest, byte[] digestPrefix) {
      int modulusLength = n.length;
      byte[] remoteBuffer = new byte[modulusLength];
      NativeRSA.publicKeyOperation(n.length << 3, e, n, signature, 0, remoteBuffer, 0);
      int digestLength = digest.getDigestLength();
      int digestPrefixLength = digestPrefix.length;
      byte[] localBuffer = new byte[modulusLength];
      localBuffer[1] = 1;
      Arrays.fill(localBuffer, (byte)-1, 2, modulusLength - digestLength - digestPrefixLength - 1 - 2);
      System.arraycopy(digestPrefix, 0, localBuffer, modulusLength - digestLength - digestPrefixLength, digestPrefixLength);
      digest.getDigest(localBuffer, modulusLength - digestLength);
      return Arrays.equals(localBuffer, remoteBuffer);
   }

   private static final boolean verifyPGPDSASignature(byte[] digest, byte[] signatureBytes, int signatureOffset, byte[] publicKeyBytes, int publicKeyOffset) {
      assertByte(publicKeyBytes, publicKeyOffset++, 17);
      byte[] p = getMPI(publicKeyBytes, publicKeyOffset);
      publicKeyOffset += 2 + p.length;
      byte[] q = new byte[20];
      byte[] g = new byte[p.length];
      byte[] y = new byte[p.length];
      publicKeyOffset += getMPI(publicKeyBytes, publicKeyOffset, q);
      publicKeyOffset += getMPI(publicKeyBytes, publicKeyOffset, g);
      publicKeyOffset += getMPI(publicKeyBytes, publicKeyOffset, y);
      byte[] r = new byte[q.length];
      byte[] s = new byte[q.length];
      signatureOffset += getMPI(signatureBytes, signatureOffset, r);
      signatureOffset += getMPI(signatureBytes, signatureOffset, s);
      return NativeDL.verifyDSA(p, q, g, y, digest, r, s);
   }

   private static final void assertByte(byte[] bytes, int offset, int value) {
      if (value != (bytes[offset++] & 255)) {
         throw new IOException();
      }
   }

   private static final int getInteger(byte[] bytes, int offset, int length) {
      int i = bytes[offset++] & 255;

      while (--length > 0) {
         i <<= 8;
         i |= bytes[offset++] & 255;
      }

      return i;
   }

   private static final byte[] getBytes(byte[] data, int offset, int length, int paddedLength) {
      byte[] buffer = new byte[paddedLength];
      System.arraycopy(data, offset, buffer, paddedLength - length, length);
      return buffer;
   }

   private static final int getMPILength(byte[] data, int offset) {
      return getInteger(data, offset, 2) + 7 >> 3;
   }

   private static final byte[] getMPI(byte[] data, int offset) {
      int length = getMPILength(data, offset);
      byte[] mpi = new byte[length];
      getMPI(data, offset, mpi);
      return mpi;
   }

   private static final int getMPI(byte[] data, int offset, byte[] mpi) {
      int length = getMPILength(data, offset);
      System.arraycopy(data, offset + 2, mpi, mpi.length - length, length);
      return 2 + length;
   }
}
