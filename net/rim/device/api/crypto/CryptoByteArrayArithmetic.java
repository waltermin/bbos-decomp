package net.rim.device.api.crypto;

import net.rim.device.api.util.Arrays;

public class CryptoByteArrayArithmetic {
   private static final int MAX_LENGTH = 256;
   private static final byte[] ONE = new byte[]{1};

   private CryptoByteArrayArithmetic() {
   }

   public static boolean isZero(byte[] a, int offset, int length) {
      if (a != null && a.length - length >= offset && offset >= 0 && length >= 0) {
         int endLength = offset + length;

         while (offset < endLength) {
            if (a[offset++] != 0) {
               return false;
            }
         }

         return true;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static boolean isZero(byte[] a) {
      if (a == null) {
         throw new IllegalArgumentException();
      } else {
         return isZero(a, 0, a.length);
      }
   }

   public static boolean isOne(byte[] a, int offset, int length) {
      if (a != null && offset >= 0 && length >= 0 && a.length - length >= offset) {
         int endIndex = offset + length - 1;
         if (a[endIndex] != 1) {
            return false;
         }

         while (offset < endIndex) {
            if (a[offset++] != 0) {
               return false;
            }
         }

         return true;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static boolean isOne(byte[] a) {
      if (a == null) {
         throw new IllegalArgumentException();
      } else {
         return isOne(a, 0, a.length);
      }
   }

   public static int findFirstNonZeroByte(byte[] a, int offset, int length) {
      if (a == null || length < 0 || offset < 0 || a.length - length < offset) {
         throw new IllegalArgumentException();
      }

      if (length != 0 && a.length != 0 && offset <= a.length) {
         int endIndex = offset + length - 1;
         int msb = offset;

         while (msb < endIndex && a[msb] == 0) {
            msb++;
         }

         return msb;
      } else {
         return -1;
      }
   }

   public static native int compare(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5);

   public static int compare(byte[] a, byte[] b) {
      try {
         return compare(a, 0, a.length, b, 0, b.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static native void multiplyByTwo(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, byte[] var6, int var7, int var8);

   public static void multiplyByTwo(byte[] a, byte[] modulus, byte[] result) {
      try {
         multiplyByTwo(a, 0, a.length, modulus, 0, modulus.length, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static native void multiplyByTwo(byte[] var0, int var1, int var2, int var3, byte[] var4, int var5, int var6);

   public static void multiplyByTwo(byte[] a, int log2modulus, byte[] result) {
      try {
         multiplyByTwo(a, 0, a.length, log2modulus, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static native void divideByTwo(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, byte[] var6, int var7, int var8);

   public static void divideByTwo(byte[] a, byte[] modulus, byte[] result) {
      try {
         divideByTwo(a, 0, a.length, modulus, 0, modulus.length, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static native void divideByTwo(byte[] var0, int var1, int var2, int var3, byte[] var4, int var5, int var6);

   public static void divideByTwo(byte[] a, int log2modulus, byte[] result) {
      try {
         divideByTwo(a, 0, a.length, log2modulus, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static native void add(
      byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, byte[] var6, int var7, int var8, byte[] var9, int var10, int var11
   );

   public static void add(byte[] a, byte[] b, byte[] modulus, byte[] result) {
      try {
         add(a, 0, a.length, b, 0, b.length, modulus, 0, modulus.length, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static native void add(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, int var6, byte[] var7, int var8, int var9);

   public static void add(byte[] a, byte[] b, int log2modulus, byte[] result) {
      try {
         add(a, 0, a.length, b, 0, b.length, log2modulus, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static native void subtract(
      byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, byte[] var6, int var7, int var8, byte[] var9, int var10, int var11
   );

   public static void subtract(byte[] a, byte[] b, byte[] modulus, byte[] result) {
      try {
         subtract(a, 0, a.length, b, 0, b.length, modulus, 0, modulus.length, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static native void subtract(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, int var6, byte[] var7, int var8, int var9);

   public static void subtract(byte[] a, byte[] b, int log2modulus, byte[] result) {
      try {
         subtract(a, 0, a.length, b, 0, b.length, log2modulus, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static native void multiply(
      byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, byte[] var6, int var7, int var8, byte[] var9, int var10, int var11
   );

   public static void multiply(byte[] a, byte[] b, byte[] modulus, byte[] result) {
      try {
         multiply(a, 0, a.length, b, 0, b.length, modulus, 0, modulus.length, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static native void multiply(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, int var6, byte[] var7, int var8, int var9);

   public static void multiply(byte[] a, byte[] b, int log2modulus, byte[] result) {
      try {
         multiply(a, 0, a.length, b, 0, b.length, log2modulus, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static native void square(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, byte[] var6, int var7, int var8);

   public static void square(byte[] a, byte[] modulus, byte[] result) {
      try {
         square(a, 0, a.length, modulus, 0, modulus.length, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static native void square(byte[] var0, int var1, int var2, int var3, byte[] var4, int var5, int var6);

   public static void square(byte[] a, int log2modulus, byte[] result) {
      try {
         square(a, 0, a.length, log2modulus, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static native void mod(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, byte[] var6, int var7, int var8);

   public static void mod(byte[] a, byte[] modulus, byte[] result) {
      try {
         mod(a, 0, a.length, modulus, 0, modulus.length, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static native void mod(byte[] var0, int var1, int var2, int var3, byte[] var4, int var5, int var6);

   public static void mod(byte[] a, int log2modulus, byte[] result) {
      try {
         mod(a, 0, a.length, log2modulus, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static native void invert(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, byte[] var6, int var7, int var8);

   public static void invert(byte[] a, byte[] modulus, byte[] result) {
      try {
         invert(a, 0, a.length, modulus, 0, modulus.length, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static native void gcd(byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, byte[] var6, int var7, int var8);

   public static void gcd(byte[] a, byte[] b, byte[] result) {
      try {
         gcd(a, 0, a.length, b, 0, b.length, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static void exponent(
      byte[] base,
      int baseOffset,
      int baseLength,
      byte[] exponent,
      int exponentOffset,
      int exponentLength,
      byte[] modulus,
      int modulusOffset,
      int modulusLength,
      byte[] result,
      int resultOffset,
      int resultLength
   ) {
      Certicom.assertAccessAllowed();
      exponent0(
         base, baseOffset, baseLength, exponent, exponentOffset, exponentLength, modulus, modulusOffset, modulusLength, result, resultOffset, resultLength
      );
   }

   private static native void exponent0(
      byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, byte[] var6, int var7, int var8, byte[] var9, int var10, int var11
   );

   public static void exponent(byte[] base, byte[] exponent, byte[] modulus, byte[] result) {
      try {
         exponent(base, 0, base.length, exponent, 0, exponent.length, modulus, 0, modulus.length, result, 0, result.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static void increment(
      byte[] a, int aOffset, int aLength, byte[] modulus, int modulusOffset, int modulusLength, byte[] result, int resultOffset, int resultLength
   ) {
      try {
         add(a, aOffset, aLength, ONE, 0, 1, modulus, modulusOffset, modulusLength, result, resultOffset, resultLength);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static void increment(byte[] a, byte[] modulus, byte[] result) {
      add(a, ONE, modulus, result);
   }

   public static void increment(byte[] a, int aOffset, int aLength, int log2modulus, byte[] result, int resultOffset, int resultLength) {
      try {
         add(a, aOffset, aLength, ONE, 0, 1, log2modulus, result, resultOffset, resultLength);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static void increment(byte[] a, int log2modulus, byte[] result) {
      add(a, ONE, log2modulus, result);
   }

   public static void decrement(
      byte[] a, int aOffset, int aLength, byte[] modulus, int modulusOffset, int modulusLength, byte[] result, int resultOffset, int resultLength
   ) {
      try {
         subtract(a, aOffset, aLength, ONE, 0, 1, modulus, modulusOffset, modulusLength, result, resultOffset, resultLength);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static void decrement(byte[] a, byte[] modulus, byte[] result) {
      subtract(a, ONE, modulus, result);
   }

   public static void decrement(byte[] a, int aOffset, int aLength, int log2modulus, byte[] result, int resultOffset, int resultLength) {
      try {
         subtract(a, aOffset, aLength, ONE, 0, 1, log2modulus, result, resultOffset, resultLength);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static void decrement(byte[] a, int log2modulus, byte[] result) {
      subtract(a, ONE, log2modulus, result);
   }

   public static native void divide(
      byte[] var0, int var1, int var2, byte[] var3, int var4, int var5, byte[] var6, int var7, int var8, byte[] var9, int var10, int var11
   );

   public static void divide(byte[] dividend, byte[] divisor, byte[] quotient, byte[] remainder) {
      try {
         divide(dividend, 0, dividend.length, divisor, 0, divisor.length, quotient, 0, quotient.length, remainder, 0, remainder.length);
      } catch (NullPointerException e) {
         throw new IllegalArgumentException();
      }
   }

   public static byte[] trim(byte[] array) {
      if (array == null) {
         throw new IllegalArgumentException();
      }

      int zeros = 0;

      for (int i = 0; i < array.length && array[i] == 0; i++) {
         zeros++;
      }

      return zeros == 0 ? array : Arrays.copy(array, zeros, array.length - zeros);
   }

   public static byte[] pad(byte[] array, int numberOfZeros) {
      if (array == null || numberOfZeros < 0) {
         throw new IllegalArgumentException();
      }

      if (numberOfZeros == 0) {
         return array;
      }

      byte[] temp = new byte[array.length + numberOfZeros];
      System.arraycopy(array, 0, temp, numberOfZeros, array.length);
      return temp;
   }

   public static byte[] ensureLength(byte[] array, int length) {
      if (array != null && length >= 0) {
         if (array.length < length) {
            return pad(array, length - array.length);
         }

         if (array.length > length) {
            int numZeros = array.length - length;

            for (int i = 0; i < numZeros; i++) {
               if (array[i] != 0) {
                  throw new NumberFormatException();
               }
            }

            array = Arrays.copy(array, numZeros, array.length - numZeros);
         }

         return array;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static byte[] valueOf(long i) {
      if (i < 0) {
         throw new IllegalArgumentException();
      }

      if (i == 0) {
         return new byte[1];
      }

      int length = 8;

      int shift;
      for (shift = 56; (i >>> shift & 255) == 0; shift -= 8) {
         length--;
      }

      byte[] n = new byte[length];

      for (int j = 0; j < length; j++) {
         n[j] = (byte)(i >>> shift);
         shift -= 8;
      }

      return n;
   }

   public static long valueOf(byte[] array) {
      array = trim(array);
      int length = array.length;
      if (length > 8) {
         throw new IllegalArgumentException();
      }

      long n = 0;

      for (int i = 0; i < length; i++) {
         n <<= 8;
         n += array[i] & 0xFF;
      }

      return n;
   }

   public static byte[] createArray(int log2n) {
      if (log2n < 0) {
         throw new IllegalArgumentException();
      }

      byte[] n = new byte[(log2n + 8) / 8];
      n[0] = (byte)(1 << log2n % 8);
      return n;
   }

   public static int getNumBits(byte[] array) {
      if (array == null) {
         throw new IllegalArgumentException();
      }

      int length = array.length;
      int zeroBits = 0;

      for (int index = 0; index < length; zeroBits += 8) {
         byte b = array[index];
         if (b != 0) {
            while ((b & 128) == 0) {
               zeroBits++;
               b = (byte)(b << 1);
            }
            break;
         }

         index++;
      }

      return length * 8 - zeroBits;
   }
}
