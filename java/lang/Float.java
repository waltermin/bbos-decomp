package java.lang;

public final class Float {
   private float value;
   public static final float POSITIVE_INFINITY = POSITIVE_INFINITY;
   public static final float NEGATIVE_INFINITY = NEGATIVE_INFINITY;
   public static final float NaN = NaN;
   public static final float MAX_VALUE = MAX_VALUE;
   public static final float MIN_VALUE = MIN_VALUE;

   public static final String toString(float f) {
      return new FloatingDecimal(f).toJavaFormatString();
   }

   public static final Float valueOf(String s) {
      return new Float(FloatingDecimal.readJavaFormatString(s).floatValue());
   }

   public static final float parseFloat(String s) {
      return FloatingDecimal.readJavaFormatString(s).floatValue();
   }

   public static final boolean isNaN(float v) {
      return v != v;
   }

   public static final boolean isInfinite(float v) {
      return v == 2139095040 || v == -8388608;
   }

   public Float(float value) {
      this.value = value;
   }

   public Float(double value) {
      this.value = (float)value;
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
      return this.value;
   }

   public final double doubleValue() {
      return this.value;
   }

   @Override
   public final int hashCode() {
      return floatToIntBits(this.value);
   }

   @Override
   public final boolean equals(Object obj) {
      return obj instanceof Float && floatToIntBits(((Float)obj).value) == floatToIntBits(this.value);
   }

   public static final native int floatToIntBits(float var0);

   public static final native float intBitsToFloat(int var0);
}
