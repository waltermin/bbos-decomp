package net.rim.device.internal.browser.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.api.util.Arrays;

public final class IdEncryptor {
   private static final int VERSION = 1;
   private static final int BLOCK_LEN = 100;
   private static final int MODULUS_BIT_LENGTH = 1024;
   private static final int MODULUS_LENGTH = 128;
   private static final byte[] RSA_E = new byte[]{1, 0, 1};
   private static final byte[] RSA_N_0 = new byte[]{
      -95,
      -50,
      1,
      -98,
      -13,
      -36,
      -49,
      -108,
      39,
      -87,
      73,
      -13,
      104,
      97,
      15,
      -88,
      -80,
      116,
      -35,
      101,
      84,
      -44,
      37,
      -44,
      119,
      105,
      59,
      -5,
      -126,
      -39,
      -80,
      -54,
      -107,
      -35,
      -43,
      88,
      -47,
      99,
      -65,
      37,
      45,
      119,
      -85,
      -38,
      71,
      -3,
      12,
      -115,
      20,
      13,
      78,
      -92,
      -88,
      50,
      -98,
      110,
      99,
      39,
      93,
      56,
      -21,
      -125,
      96,
      -122,
      -92,
      7,
      -87,
      4,
      38,
      126,
      91,
      85,
      -12,
      83,
      -30,
      51,
      -94,
      105,
      -69,
      49,
      75,
      26,
      70,
      -63,
      -15,
      25,
      -2,
      54,
      -109,
      13,
      -5,
      76,
      97,
      48,
      52,
      110,
      -58,
      90,
      -17,
      127,
      -21,
      20,
      -121,
      5,
      -23,
      -75,
      74,
      43,
      112,
      10,
      -124,
      -64,
      64,
      66,
      108,
      88,
      -45,
      9,
      24,
      -73,
      -36,
      -11,
      -105,
      -76,
      -28,
      -114,
      -87,
      -111
   };
   private static final byte[] RSA_N_1 = new byte[]{
      76,
      67,
      -107,
      73,
      -41,
      -128,
      36,
      122,
      102,
      -110,
      -13,
      105,
      91,
      35,
      -108,
      127,
      -28,
      29,
      101,
      77,
      109,
      -19,
      -17,
      -32,
      -53,
      -27,
      -117,
      84,
      -87,
      13,
      -71,
      85,
      38,
      57,
      -90,
      66,
      11,
      54,
      -120,
      116,
      6,
      -52,
      -21,
      -99,
      46,
      123,
      -121,
      -107,
      95,
      67,
      74,
      45,
      88,
      27,
      -24,
      110,
      -19,
      -115,
      28,
      -119,
      95,
      115,
      -118,
      -97,
      -85,
      53,
      15,
      78,
      1,
      14,
      -128,
      71,
      -33,
      5,
      55,
      -28,
      -92,
      -79,
      -69,
      -2,
      106,
      41,
      116,
      27,
      -120,
      -113,
      63,
      104,
      63,
      35,
      101,
      -89,
      -14,
      -93,
      -113,
      -11,
      -16,
      89,
      64,
      115,
      -128,
      -1,
      -25,
      -48,
      96,
      -5,
      102,
      -123,
      2,
      96,
      -100,
      -29,
      -28,
      102,
      117,
      -58,
      -127,
      39,
      -128,
      73,
      -17,
      -1,
      68,
      16,
      29,
      98,
      -106,
      109
   };

   private IdEncryptor() {
   }

   public static final String encrypt(String value, int key) {
      if (value != null && key >= 0 && key <= 1) {
         byte[] tempMemory = new byte[128];
         byte[] input = value.getBytes();
         int count = (input.length + 100 - 1) / 100;
         byte[] output = new byte[1 + count * 128];
         output[0] = 1;
         byte[] n = key == 0 ? RSA_N_0 : RSA_N_1;
         int blockLen = 0;
         int inOffset = 0;
         int outOffset = 1;

         for (int i = 0; i < count; i++) {
            Arrays.fill(tempMemory, (byte)0);
            blockLen = Math.min(100, input.length - inOffset);
            formatAndEncrypt(n, tempMemory, input, inOffset, blockLen, output, outOffset);
            inOffset += 100;
            outOffset += 128;
         }

         try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            Base64OutputStream b64Out = new Base64OutputStream(outStream);
            b64Out.write(output);
            b64Out.close();
            return outStream.toString();
         } catch (IOException var12) {
            return null;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static final void formatAndEncrypt(byte[] n, byte[] temp, byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) {
      if (input != null
         && inputOffset >= 0
         && inputLength >= 0
         && inputOffset + inputLength <= input.length
         && output != null
         && outputOffset >= 0
         && outputOffset + 128 <= output.length
         && inputLength <= 100) {
         int encodedMessageOffset = 0;
         temp[encodedMessageOffset++] = 0;
         temp[encodedMessageOffset++] = 2;
         int randomDataLength = temp.length - (3 + inputLength);
         RandomSource.getBytes(temp, encodedMessageOffset, randomDataLength);

         for (int i = randomDataLength; i > 0; i--) {
            if (temp[encodedMessageOffset++] == 0) {
               byte randomByte;
               do {
                  randomByte = (byte)RandomSource.getInt();
               } while (randomByte == 0);

               temp[encodedMessageOffset - 1] = randomByte;
            }
         }

         temp[encodedMessageOffset++] = 0;
         System.arraycopy(input, inputOffset, temp, encodedMessageOffset, inputLength);
         nativeRSAPublicKeyOperation(1024, RSA_E, n, temp, 0, output, outputOffset);
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static final native void nativeRSAPublicKeyOperation(int var0, byte[] var1, byte[] var2, byte[] var3, int var4, byte[] var5, int var6);

   public static final byte[] getPGPPublicKey() {
      return getPGPPublicKey(RSA_E, RSA_N_0);
   }

   private static final byte[] getPGPPublicKey(byte[] e, byte[] n) {
      try {
         ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
         bytesOut.write(3);
         long time = System.currentTimeMillis() / 1000;
         bytesOut.write((int)(time >> 24) & 0xFF);
         bytesOut.write((int)(time >> 16) & 0xFF);
         bytesOut.write((int)(time >> 8) & 0xFF);
         bytesOut.write((int)time & 0xFF);
         bytesOut.write(1);
         int length = n.length << 3;
         bytesOut.write(length >> 8);
         bytesOut.write(length);
         bytesOut.write(n);
         length = e.length << 3;
         bytesOut.write(length >> 8);
         bytesOut.write(length);
         bytesOut.write(e);
         return bytesOut.toByteArray();
      } catch (IOException var6) {
         return null;
      }
   }
}
