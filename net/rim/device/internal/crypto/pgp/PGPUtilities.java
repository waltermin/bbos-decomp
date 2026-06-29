package net.rim.device.internal.crypto.pgp;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import net.rim.device.api.crypto.CryptoUnsupportedOperationException;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.SymmetricKeyFactory;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateKeyStoreIndex;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.pgp.PGPEncodingException;
import net.rim.device.api.crypto.pgp.PGPPseudoRandomSource;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.io.SharedInputStream;
import net.rim.vm.Array;

public final class PGPUtilities {
   public static String ENCODING_ALGORITHM = "KeyStore";
   public static String PGP = "PGP";
   private static final ResourceBundle _rb = ResourceBundle.getBundle(1012160749078055414L, "net.rim.device.internal.resource.crypto.PGP");

   public static final ResourceBundle getResourceBundle() {
      return _rb;
   }

   public static final int writeMPI(OutputStream out, byte[] buffer) {
      return writeMPI(out, buffer, 0, buffer.length);
   }

   public static final int writeMPI(OutputStream out, byte[] buffer, int offset, int length) {
      if (buffer != null && offset >= 0 && length >= 0 && buffer.length - length >= offset && length <= 65535) {
         while (offset < buffer.length && buffer[offset] == 0) {
            offset++;
         }

         int bitlength = (buffer.length - offset) * 8;

         for (int i = 7; i > -1 && ((byte)(buffer[offset] >> i) & 1) == 0; i--) {
            bitlength--;
         }

         int bytelength;
         if ((bitlength & 7) != 0) {
            bytelength = (bitlength >> 3) + 1;
         } else {
            bytelength = bitlength >> 3;
         }

         out.write((byte)(bitlength >> 8 & 0xFF));
         out.write((byte)(bitlength & 0xFF));
         out.write(buffer, offset, bytelength);
         return bytelength + 2;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static final byte[] readMPI(InputStream input) {
      return readMPI(input, null);
   }

   public static final byte[] readMPI(InputStream input, byte[] encodedLength) throws IOException {
      int length;
      if (encodedLength == null) {
         length = input.read();
         length = length << 8 | input.read();
      } else {
         encodedLength[0] = (byte)input.read();
         encodedLength[1] = (byte)input.read();
         length = (encodedLength[0] & 255) << 8 | encodedLength[1] & 255;
      }

      if ((length & 7) != 0) {
         length = (length >> 3) + 1;
      } else {
         length >>= 3;
      }

      if (length < 0) {
         throw new IOException();
      } else {
         byte[] result = new byte[length];
         length = input.read(result);
         if (length != result.length) {
            throw new IOException();
         } else {
            return result;
         }
      }
   }

   public static final void skipPacket(InputStream input) {
      int[] parameters = new int[2];
      byte[] encoding = readTagAndLength(input, parameters);
      if (encoding == null) {
         input.skip(parameters[1]);
      }
   }

   public static final void writeTagAndLength(OutputStream out, int tag, int length, int tagVersion) {
      if (tagVersion != 3) {
         if (tagVersion == 4) {
            if (tag > 63) {
               throw new IllegalArgumentException();
            }

            out.write(192 | tag);
            if (length >> 16 != 0 || length - 8383 > 0) {
               out.write(255);
               out.write(length >> 24);
               out.write(length >> 16);
               out.write(length >> 8);
               out.write(length);
            } else if (length - 192 >= 0) {
               out.write((length - 192 >> 8) + 192);
               out.write(length - 192);
            } else {
               out.write(length);
            }
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         if (tag > 15) {
            throw new IllegalArgumentException();
         }

         if (length >> 16 != 0) {
            out.write(128 | tag << 2 | 2);
            out.write(length >> 24);
            out.write(length >> 16);
            out.write(length >> 8);
            out.write(length);
         } else if (length >> 8 != 0) {
            out.write(128 | tag << 2 | 1);
            out.write(length >> 8);
            out.write(length);
         } else {
            out.write(128 | tag << 2);
            out.write(length);
         }
      }
   }

   public static final int peekTag(SharedInputStream stream) throws PGPEncodingException {
      int tag = stream.peek();
      if ((tag & 128) == 0) {
         throw new PGPEncodingException();
      } else {
         return (tag & 64) == 0 ? tag >> 2 & 15 : tag & 63;
      }
   }

   public static final byte[] readTagAndLength(InputStream input, int[] info) throws PGPEncodingException {
      info[0] = 0;
      info[1] = 0;
      int tag = nextInt(input);
      if ((tag & 128) == 0) {
         throw new PGPEncodingException();
      }

      if ((tag & 64) != 0) {
         info[0] = tag & 63;
         int next = nextInt(input);
         if ((next & 0xFF) < 192) {
            info[1] = next & 0xFF;
            return null;
         }

         if ((next & 0xFF) >= 192 && (next & 0xFF) <= 223) {
            info[1] = ((next & 0xFF) - 192 << 8) + nextInt(input) + 192;
            return null;
         }

         if (next == 255) {
            info[1] = nextInt(input);
            info[1] = info[1] << 8 | nextInt(input);
            info[1] = info[1] << 8 | nextInt(input);
            info[1] = info[1] << 8 | nextInt(input);
            return null;
         }

         byte[] buffer = new byte[0];
         info[1] = -1;
         int templength = 0;
         int length = 0;
         boolean done = false;

         try {
            while (true) {
               if ((next & 0xFF) < 192) {
                  templength = next & 0xFF;
                  done = true;
               } else if ((next & 0xFF) >= 192 && (next & 0xFF) <= 223) {
                  templength = ((next & 0xFF) - 192 << 8) + nextInt(input) + 192;
                  done = true;
               } else if (next == 255) {
                  templength = nextInt(input);
                  templength = templength << 8 | nextInt(input);
                  templength = templength << 8 | nextInt(input);
                  templength = templength << 8 | nextInt(input);
                  done = true;
               } else {
                  templength = 1 << (next & 31);
               }

               if (templength < 0) {
                  throw new IOException();
               }

               Array.resize(buffer, length + templength);
               int temp = input.read(buffer, length, templength);
               length += temp;
               if (done || temp < templength) {
                  if (temp < templength) {
                     Array.resize(buffer, length);
                  }

                  return buffer;
               }

               next = nextInt(input);
            }
         } finally {
            Array.resize(buffer, length);
            return buffer;
         }
      } else {
         info[0] = tag >> 2 & 15;
         if ((tag & 3) == 0) {
            info[1] = nextInt(input) & 0xFF;
            return null;
         }

         if ((tag & 3) == 1) {
            info[1] = nextInt(input);
            info[1] = info[1] << 8 | nextInt(input);
            return null;
         }

         if ((tag & 3) == 2) {
            info[1] = nextInt(input);
            info[1] = info[1] << 8 | nextInt(input);
            info[1] = info[1] << 8 | nextInt(input);
            info[1] = info[1] << 8 | nextInt(input);
            return null;
         }

         info[1] = -1;
         byte[] buffer = new byte[100];

         try {
            int offset = 0;

            while (true) {
               int len = input.read(buffer, offset, 100);
               if (len == -1) {
                  Array.resize(buffer, offset);
                  return buffer;
               }

               if (len < 100) {
                  Array.resize(buffer, offset + len);
                  return buffer;
               }

               Array.resize(buffer, buffer.length + 100);
               offset += 100;
            }
         } finally {
            ;
         }
      }
   }

   public static final int digestStringToConstant(String digest) throws CryptoUnsupportedOperationException {
      if (digest.equals("SHA1")) {
         return 2;
      } else if (digest.equals("RIPEMD160")) {
         return 3;
      } else if (digest.equals("MD5")) {
         return 1;
      } else if (digest.equals("MD2")) {
         return 5;
      } else if (digest.equals("SHA256")) {
         return 8;
      } else if (digest.equals("SHA384")) {
         return 9;
      } else if (digest.equals("SHA512")) {
         return 10;
      } else {
         throw new CryptoUnsupportedOperationException("Hash:" + digest);
      }
   }

   public static final String digestConstantToString(int hashAlgorithm) throws CryptoUnsupportedOperationException {
      switch (hashAlgorithm) {
         case 0:
         case 4:
         case 6:
         case 7:
            throw new CryptoUnsupportedOperationException("Hash:" + hashAlgorithm);
         case 1:
            return "MD5";
         case 2:
         default:
            return "SHA1";
         case 3:
            return "RIPEMD160";
         case 5:
            return "MD2";
         case 8:
            return "SHA256";
         case 9:
            return "SHA384";
         case 10:
            return "SHA512";
      }
   }

   public static final String encryptionConstantToString(int symmetricAlgorithm) throws CryptoUnsupportedOperationException {
      switch (symmetricAlgorithm) {
         case 1:
         case 4:
         case 5:
         case 6:
            throw new CryptoUnsupportedOperationException("Sym:" + symmetricAlgorithm);
         case 2:
         default:
            return "TripleDES";
         case 3:
            return "CAST128";
         case 7:
            return "AES_128";
         case 8:
            return "AES_192";
         case 9:
            return "AES_256";
      }
   }

   public static final String publicKeyAlgorithmConstantToString(int publicKeyAlgorithm) throws CryptoUnsupportedOperationException {
      switch (publicKeyAlgorithm) {
         case 1:
            return "RSA";
         case 2:
            return "RSA Encrypt-Only";
         case 3:
            return "RSA Sign-Only";
         case 16:
            return "Elgamal Encrypt Only";
         case 17:
            return "DSA";
         case 18:
            return "ECAES";
         case 19:
            return "ECDSA";
         case 20:
            return "Elgamal";
         case 21:
            return "DH";
         default:
            throw new CryptoUnsupportedOperationException("Pub:" + publicKeyAlgorithm);
      }
   }

   public static final SymmetricKey getSessionKey(int algorithm, PGPPseudoRandomSource randomSource) throws CryptoUnsupportedOperationException {
      String algorithmString = encryptionConstantToString(algorithm);
      byte[] sessionKey;
      switch (algorithm) {
         case 1:
         case 4:
         case 5:
         case 6:
            throw new CryptoUnsupportedOperationException("Sym:" + algorithm);
         case 2:
         case 8:
            sessionKey = new byte[24];
            break;
         case 3:
         case 7:
         default:
            sessionKey = new byte[16];
            break;
         case 9:
            sessionKey = new byte[32];
      }

      randomSource.xorBytes(sessionKey, 0, sessionKey.length);
      return SymmetricKeyFactory.getInstance(algorithmString, sessionKey, 0);
   }

   public static final long convertTime(byte[] data, int offset) {
      if (data != null && data.length >= 4 && data.length - offset >= 4) {
         long time = 0;
         time += (data[offset++] & 255) << 24;
         time += (data[offset++] & 255) << 16;
         time += (data[offset++] & 255) << 8;
         time += data[offset] & 0xFF;
         return time * 1000;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static final boolean isRevokedSignature(int signatureType) {
      switch (signatureType) {
         case 32:
         case 40:
         case 48:
            return true;
         default:
            return false;
      }
   }

   public static final boolean isUserIDSignature(int signatureType) {
      switch (signatureType) {
         case 16:
         case 17:
         case 18:
         case 19:
         case 48:
            return true;
         default:
            return false;
      }
   }

   public static final boolean isParentKeySignature(int signatureType) {
      switch (signatureType) {
         case 30:
            return false;
         case 31:
         case 32:
         default:
            return true;
      }
   }

   public static final boolean isSubKeySignature(int signatureType) {
      switch (signatureType) {
         case 24:
         case 40:
            return true;
         default:
            return false;
      }
   }

   public static final boolean startsWith(byte[] line, byte[] header) {
      if (line.length < header.length) {
         return false;
      }

      for (int i = 0; i < header.length; i++) {
         if (line[i] != header[i]) {
            return false;
         }
      }

      return true;
   }

   public static final boolean hasPrivateKey(Certificate certificate, KeyStore keyStore) {
      if (keyStore == null) {
         return false;
      }

      if (!keyStore.existsIndex(-2038609988711824737L)) {
         keyStore.addIndex(new CertificateKeyStoreIndex());
      }

      Enumeration enumeration = keyStore.elements(-2038609988711824737L, certificate);

      while (enumeration.hasMoreElements()) {
         KeyStoreData data = (KeyStoreData)enumeration.nextElement();
         if (data.isPrivateKeySet()) {
            return true;
         }
      }

      return false;
   }

   public static final String binaryToHexASCIIString(byte[] byteArray) {
      StringBuffer hexASCIIStringBuffer = new StringBuffer();
      hexASCIIStringBuffer.append('0').append('x');
      int byteArrayLength = byteArray.length;

      for (int i = 0; i < byteArrayLength; i++) {
         hexASCIIStringBuffer.append(binaryByteToHexASCII((byte)(byteArray[i] >> 4 & 15)));
         hexASCIIStringBuffer.append(binaryByteToHexASCII((byte)(byteArray[i] & 15)));
      }

      return hexASCIIStringBuffer.toString();
   }

   private static final char binaryByteToHexASCII(byte binaryByte) {
      switch (binaryByte) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         default:
            return '0';
         case 1:
            return '1';
         case 2:
            return '2';
         case 3:
            return '3';
         case 4:
            return '4';
         case 5:
            return '5';
         case 6:
            return '6';
         case 7:
            return '7';
         case 8:
            return '8';
         case 9:
            return '9';
         case 10:
            return 'A';
         case 11:
            return 'B';
         case 12:
            return 'C';
         case 13:
            return 'D';
         case 14:
            return 'E';
         case 15:
            return 'F';
      }
   }

   public static final byte[] hexASCIIStringToBinary(String hexASCIIString) {
      if (hexASCIIString == null) {
         return null;
      }

      byte[] allBytes = hexASCIIString.getBytes();
      int allBytesLength = allBytes.length;
      int hexASCIIStartPosition = 0;
      if (allBytesLength >= 2 && allBytes[0] == 48 && allBytes[1] == 120) {
         hexASCIIStartPosition = 2;
      }

      int hexASCIIBytesLength = allBytesLength - hexASCIIStartPosition;
      if ((hexASCIIBytesLength & 1) != 0) {
         return null;
      }

      try {
         byte[] binaryBytes = new byte[hexASCIIBytesLength >> 1];
         int binaryPosition = 0;
         int hexASCIIPosition = hexASCIIStartPosition;

         while (hexASCIIPosition < allBytesLength) {
            binaryBytes[binaryPosition++] = (byte)(hexASCIIByteToBinary(allBytes[hexASCIIPosition++]) << 4 | hexASCIIByteToBinary(allBytes[hexASCIIPosition++]));
         }

         return binaryBytes;
      } finally {
         ;
      }
   }

   private static final byte hexASCIIByteToBinary(byte hexASCIIByte) {
      switch (hexASCIIByte) {
         case 48:
            return 0;
         case 49:
            return 1;
         case 50:
            return 2;
         case 51:
            return 3;
         case 52:
            return 4;
         case 53:
            return 5;
         case 54:
            return 6;
         case 55:
            return 7;
         case 56:
            return 8;
         case 57:
            return 9;
         case 65:
            return 10;
         case 66:
            return 11;
         case 67:
            return 12;
         case 68:
            return 13;
         case 69:
            return 14;
         case 70:
            return 15;
         case 97:
            return 10;
         case 98:
            return 11;
         case 99:
            return 12;
         case 100:
            return 13;
         case 101:
            return 14;
         case 102:
            return 15;
         default:
            throw new IllegalArgumentException();
      }
   }

   private static final int nextInt(InputStream input) throws EOFException {
      int next = input.read();
      if (next == -1) {
         throw new EOFException();
      } else {
         return next;
      }
   }
}
