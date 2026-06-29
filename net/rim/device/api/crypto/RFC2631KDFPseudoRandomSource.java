package net.rim.device.api.crypto;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public final class RFC2631KDFPseudoRandomSource extends AbstractPseudoRandomSource implements PseudoRandomSource {
   private SHA1Digest _digest;
   private int _counter;
   private int _counterOffset;
   private byte[] _sharedSecret;
   private byte[] _outputBuffer;
   private byte[] _previousOutputBuffer;
   private byte[] _otherInfo;
   private int _outputOffset;
   private int _outputLength;
   private static final int SEQUENCE_TAG = 48;
   private static final int OID_TAG = 6;
   private static final int OCTET_STRING_TAG = 4;

   public RFC2631KDFPseudoRandomSource(byte[] sharedSecret, OID algorithm, byte[] partyAInfo, int derivedKeyLength) {
      this(sharedSecret, 0, sharedSecret == null ? 0 : sharedSecret.length, algorithm, partyAInfo, derivedKeyLength);
   }

   public RFC2631KDFPseudoRandomSource(byte[] sharedSecret, int offset, int length, OID algorithm, byte[] partyAInfo, int derivedKeyLength) {
      if (sharedSecret != null && length >= 0 && offset >= 0 && sharedSecret.length - length >= offset && algorithm != null && derivedKeyLength >= 0) {
         this._digest = (SHA1Digest)(new Object());
         this._outputLength = this._digest.getDigestLength();
         this._sharedSecret = new byte[length];
         System.arraycopy(sharedSecret, offset, this._sharedSecret, 0, length);
         if (partyAInfo != null) {
            if (partyAInfo.length != 64) {
               throw new Object();
            }
         } else {
            partyAInfo = new byte[0];
         }

         byte[] oidBytes = algorithm.toByteArray();
         byte[] suppPublicInfo = this.intToBigEndian(derivedKeyLength, new byte[4], 0);
         byte[] counter = new byte[4];
         int keySpecificInfoLength = this.predictFieldLength(oidBytes.length, false) + this.predictFieldLength(counter.length, false);
         int otherInfoLength = this.predictFieldLength(keySpecificInfoLength, false)
            + this.predictFieldLength(partyAInfo.length, true)
            + this.predictFieldLength(suppPublicInfo.length, true);

         label44:
         try {
            ByteArrayOutputStream out = (ByteArrayOutputStream)(new Object());
            this._counterOffset = 0;
            this._counterOffset = this._counterOffset + this.encodeTag(out, 48, otherInfoLength, false, 0);
            this._counterOffset = this._counterOffset + this.encodeTag(out, 48, keySpecificInfoLength, false, 0);
            this._counterOffset = this._counterOffset + this.encodeTag(out, 6, oidBytes.length, false, 0);
            this._counterOffset = this._counterOffset + this.appendByteArray(out, oidBytes);
            this._counterOffset = this._counterOffset + this.encodeTag(out, 4, counter.length, false, 0);
            this.appendByteArray(out, counter);
            this.encodeTag(out, 4, partyAInfo.length, true, 160);
            this.appendByteArray(out, partyAInfo);
            this.encodeTag(out, 4, suppPublicInfo.length, true, 162);
            this.appendByteArray(out, suppPublicInfo);
            this._otherInfo = out.toByteArray();
         } finally {
            break label44;
         }

         this._outputOffset = this._outputLength;
         this._counter = 1;
         this._outputBuffer = new byte[this._outputLength];
         this._previousOutputBuffer = new byte[this._outputLength];
      } else {
         throw new Object();
      }
   }

   @Override
   public final String getAlgorithm() {
      return "RFC2631KDF";
   }

   private final int appendByteArray(OutputStream out, byte[] buffer) {
      out.write(buffer);
      return buffer.length;
   }

   private final int predictEncodedLength(int length) {
      if (length >= 0 && length <= 127) {
         return 1;
      }

      int octet = 4;

      for (int shift = 24; (byte)(length >> shift) == 0 && octet > 1; shift -= 8) {
         octet--;
      }

      return octet + 1;
   }

   private final int encodeLength(OutputStream out, int length) {
      int outputLength = 0;
      if (length >= 0 && length <= 127) {
         out.write((byte)length);
         return outputLength + 1;
      }

      int octet = 4;

      int shift;
      for (shift = 24; (byte)(length >> shift) == 0 && octet > 1; shift -= 8) {
         octet--;
      }

      out.write((byte)(128 | octet));

      for (int offset = 1; offset <= octet; offset++) {
         out.write((byte)(length >> shift));
         shift -= 8;
      }

      return outputLength + octet + 1;
   }

   private final int predictFieldLength(int length, boolean explicitTag) {
      if (length == 0) {
         return 0;
      }

      int encodedLength = this.predictEncodedLength(length) + length + 1;
      if (explicitTag) {
         encodedLength += this.predictEncodedLength(encodedLength) + 1;
      }

      return encodedLength;
   }

   private final int encodeTag(OutputStream out, int type, int length, boolean explicit, int explicitTag) {
      int outputLength = 0;
      if (length != 0) {
         int innerTagLength = this.predictEncodedLength(length) + 1;
         if (explicit) {
            out.write((byte)explicitTag);
            outputLength += this.encodeLength(out, innerTagLength + length) + 1;
         }

         out.write((byte)type);
         outputLength += this.encodeLength(out, length) + 1;
      }

      return outputLength;
   }

   @Override
   public final void xorBytes(byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         while (length > 0) {
            if (this._outputOffset == this._outputLength) {
               do {
                  this.intToBigEndian(this._counter++, this._otherInfo, this._counterOffset);
                  this._digest.update(this._sharedSecret);
                  this._digest.update(this._otherInfo);
                  this._digest.getDigest(this._outputBuffer, 0);
                  this._outputOffset = 0;
               } while (Arrays.equals(this._outputBuffer, this._previousOutputBuffer));

               System.arraycopy(this._outputBuffer, 0, this._previousOutputBuffer, 0, this._outputBuffer.length);
            }

            int xorLength = Math.min(length, this._outputLength - this._outputOffset);
            length -= xorLength;

            while (xorLength-- > 0) {
               buffer[offset++] ^= this._outputBuffer[this._outputOffset++];
            }
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public final int getAvailable() {
      return Integer.MAX_VALUE;
   }

   @Override
   public final int getMaxAvailable() {
      return Integer.MAX_VALUE;
   }

   private final byte[] intToBigEndian(int value, byte[] buffer, int offset) {
      buffer[offset++] = (byte)(value >> 24);
      buffer[offset++] = (byte)(value >> 16);
      buffer[offset++] = (byte)(value >> 8);
      buffer[offset] = (byte)value;
      return buffer;
   }

   public static final void selfTest() {
      byte[] result = new byte[]{
         -39, 30, 13, -103, -53, 46, 88, -39, 114, -30, 44, -21, -41, -69, 22, -15, 95, 62, -42, -36, -43, -120, 79, -2, -112, 126, -86, 80, 123, 70, -119, -12
      };

      try {
         RFC2631KDFPseudoRandomSource source = new RFC2631KDFPseudoRandomSource(SelfTestData.RANDOM_DATA, (OID)(new Object(new byte[]{0})), null, 0);
         byte[] data = source.getBytes(32);
         if (Arrays.equals(data, result)) {
            return;
         }
      } finally {
         throw new Object();
      }

      throw new Object();
   }

   static {
      long ID_TEST_PRS_RFC2631KDF = -4431687079420144372L;
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      Object instance = appRegistry.getOrWaitFor(ID_TEST_PRS_RFC2631KDF);
      if (instance == null) {
         selfTest();
         appRegistry.put(ID_TEST_PRS_RFC2631KDF, appRegistry);
      }
   }
}
