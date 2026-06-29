package java.lang;

import net.rim.device.api.util.NumberUtilities;

public final class Integer {
   private int value;
   public static final int MIN_VALUE;
   public static final int MAX_VALUE;
   static final byte[] digits = new byte[]{
      48,
      49,
      50,
      51,
      52,
      53,
      54,
      55,
      56,
      57,
      97,
      98,
      99,
      100,
      101,
      102,
      103,
      104,
      105,
      106,
      107,
      108,
      109,
      110,
      111,
      112,
      113,
      114,
      115,
      116,
      117,
      118,
      119,
      120,
      121,
      122
   };

   public static final String toString(int i, int radix) {
      return Long.toString(i, radix);
   }

   public static final String toHexString(int i) {
      return toUnsignedString(i, 4);
   }

   public static final String toOctalString(int i) {
      return toUnsignedString(i, 3);
   }

   public static final String toBinaryString(int i) {
      return toUnsignedString(i, 1);
   }

   private static final String toUnsignedString(int i, int shift) {
      byte[] buf = new byte[32];
      int charPos = 32;
      int radix = 1 << shift;
      int mask = radix - 1;

      do {
         buf[--charPos] = digits[i & mask];
         i >>>= shift;
      } while (i != 0);

      return new String(buf, charPos, 32 - charPos);
   }

   public static final String toString(int i) {
      return toString(i, 10);
   }

   public static final int parseInt(String s, int radix) {
      return NumberUtilities.parseInt(s, 0, MAX_VALUE, radix);
   }

   public static final int parseInt(String s) {
      return parseInt(s, 10);
   }

   public static final Integer valueOf(String s, int radix) {
      return new Integer(parseInt(s, radix));
   }

   public static final Integer valueOf(String s) {
      return new Integer(parseInt(s, 10));
   }

   public Integer(int value) {
      this.value = value;
   }

   public final byte byteValue() {
      return (byte)this.value;
   }

   public final short shortValue() {
      return (short)this.value;
   }

   public final int intValue() {
      return this.value;
   }

   public final long longValue() {
      return this.value;
   }

   public final float floatValue() {
      return this.value;
   }

   public final double doubleValue() {
      return this.value;
   }

   @Override
   public final String toString() {
      return toString(this.value);
   }

   @Override
   public final int hashCode() {
      return this.value;
   }

   @Override
   public final boolean equals(Object obj) {
      return !(obj instanceof Integer) ? false : ((Integer)obj).value == this.value;
   }
}
