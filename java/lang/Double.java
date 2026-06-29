package java.lang;

public final class Double {
   private double value;
   public static final double POSITIVE_INFINITY;
   public static final double NEGATIVE_INFINITY;
   public static final double NaN;
   public static final double MAX_VALUE;
   public static final double MIN_VALUE = longBitsToDouble(1);

   public static final String toString(double d) {
      return new FloatingDecimal(d).toJavaFormatString();
   }

   public static final Double valueOf(String s) {
      return new Double(FloatingDecimal.readJavaFormatString(s).doubleValue());
   }

   public static final double parseDouble(String s) {
      return FloatingDecimal.readJavaFormatString(s).doubleValue();
   }

   public static final boolean isNaN(double v) {
      return v != v;
   }

   public static final boolean isInfinite(double v) {
      return v == 9218868437227405312L || v == -4503599627370496L;
   }

   public Double(double value) {
      this.value = value;
   }

   public final boolean isNaN() {
      return isNaN(this.value);
   }

   public final boolean isInfinite() {
      return isInfinite(this.value);
   }

   @Override
   public final String toString() {
      return String.valueOf(this.value);
   }

   public final byte byteValue() {
      return (byte)this.value;
   }

   public final short shortValue() {
      return (short)this.value;
   }

   public final int intValue() {
      return (int)this.value;
   }

   public final long longValue() {
      return (long)this.value;
   }

   public final float floatValue() {
      return (float)this.value;
   }

   public final double doubleValue() {
      return this.value;
   }

   @Override
   public final int hashCode() {
      long bits = doubleToLongBits(this.value);
      return (int)(bits ^ bits >>> 32);
   }

   @Override
   public final boolean equals(Object obj) {
      return obj instanceof Double && doubleToLongBits(((Double)obj).value) == doubleToLongBits(this.value);
   }

   public static final native long doubleToLongBits(double var0);

   public static final native double longBitsToDouble(long var0);
}
