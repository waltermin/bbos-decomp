package java.lang;

public final class Short {
   private short value;
   public static final short MIN_VALUE = -32768;
   public static final short MAX_VALUE = 32767;

   public static final short parseShort(String s) {
      return parseShort(s, 10);
   }

   public static final short parseShort(String s, int radix) {
      int i = Integer.parseInt(s, radix);
      if (i >= -32768 && i <= 32767) {
         return (short)i;
      } else {
         throw new NumberFormatException();
      }
   }

   public Short(short value) {
      this.value = value;
   }

   public final short shortValue() {
      return this.value;
   }

   @Override
   public final String toString() {
      return String.valueOf(this.value);
   }

   @Override
   public final int hashCode() {
      return this.value;
   }

   @Override
   public final boolean equals(Object obj) {
      return !(obj instanceof Short) ? false : ((Short)obj).value == this.value;
   }
}
