package java.lang;

public final class Long {
   private long value;
   public static final long MIN_VALUE;
   public static final long MAX_VALUE;

   public static final String toString(long i, int radix) {
      byte[] buf = new byte[10];
      return new String(buf, 0, toString(i, radix, buf, 0));
   }

   static final int toString(long i, int radix, Object obuf, int offset) {
      if (radix < 2 || radix > 36) {
         radix = 10;
      }

      int len;
      if (!(obuf instanceof byte[])) {
         len = StringBuffer.formatNumeric((char[])obuf, offset, radix, i);
      } else {
         len = StringBuffer.formatNumeric((byte[])obuf, offset, radix, i);
      }

      return len;
   }

   public static final String toString(long i) {
      return toString(i, 10);
   }

   public static final long parseLong(String s, int radix) {
      if (s == null) {
         throw new NumberFormatException("null");
      }

      if (radix >= 2 && radix <= 36) {
         long result = 0;
         boolean negative = false;
         int i = 0;
         int max = s.length();
         if (max <= 0) {
            throw new NumberFormatException(s);
         }

         long limit;
         if (s.charAt(0) == '-') {
            negative = true;
            limit = MIN_VALUE;
            i++;
         } else {
            limit = -9223372036854775807L;
         }

         long multmin = limit / radix;
         if (i < max) {
            int digit = Character.digit(s.charAt(i++), radix);
            if (digit < 0) {
               throw new NumberFormatException(s);
            }

            result = -digit;
         }

         while (i < max) {
            int digit = Character.digit(s.charAt(i++), radix);
            if (digit < 0) {
               throw new NumberFormatException(s);
            }

            if (result < multmin) {
               throw new NumberFormatException(s);
            }

            result *= radix;
            if (result < limit + digit) {
               throw new NumberFormatException(s);
            }

            result -= digit;
         }

         if (!negative) {
            return -result;
         } else if (i > 1) {
            return result;
         } else {
            throw new NumberFormatException(s);
         }
      } else {
         StringBuffer msg = new StringBuffer("radix ").append(radix);
         if (radix < 2) {
            msg.append(" less than Character.MIN_RADIX");
         } else {
            msg.append(" greater than Character.MAX_RADIX");
         }

         throw new NumberFormatException(msg.toString());
      }
   }

   public static final long parseLong(String s) {
      return parseLong(s, 10);
   }

   public Long(long value) {
      this.value = value;
   }

   public final long longValue() {
      return this.value;
   }

   public final float floatValue() {
      return (float)this.value;
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
      return (int)(this.value ^ this.value >> 32);
   }

   @Override
   public final boolean equals(Object obj) {
      return !(obj instanceof Long) ? false : ((Long)obj).value == this.value;
   }
}
