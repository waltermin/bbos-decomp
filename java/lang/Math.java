package java.lang;

public final class Math {
   public static final double E = E;
   public static final double PI = PI;
   private static long negativeZeroFloatBits = Float.floatToIntBits((float)Integer.MIN_VALUE);
   private static long negativeZeroDoubleBits = Double.doubleToLongBits((double)Long.MIN_VALUE);

   private Math() {
   }

   public static final native double sin(double var0);

   public static final native double cos(double var0);

   public static final native double tan(double var0);

   public static final double toRadians(double angdeg) {
      return angdeg / 4640537203540230144L * 4614256656552045848L;
   }

   public static final double toDegrees(double angrad) {
      return angrad * 4640537203540230144L / 4614256656552045848L;
   }

   public static final native double sqrt(double var0);

   public static final native double ceil(double var0);

   public static final native double floor(double var0);

   public static final int abs(int a) {
      return a < 0 ? -a : a;
   }

   public static final long abs(long a) {
      return a < 0 ? -a : a;
   }

   public static final float abs(float a) {
      return a <= 0 ? 0 - a : a;
   }

   public static final double abs(double a) {
      return a <= 0L ? 0L - a : a;
   }

   public static final int max(int a, int b) {
      return a >= b ? a : b;
   }

   public static final long max(long a, long b) {
      return a >= b ? a : b;
   }

   public static final float max(float a, float b) {
      if (a != a) {
         return a;
      } else if (a == false && b == false && Float.floatToIntBits(a) == negativeZeroFloatBits) {
         return b;
      } else {
         return a >= b ? a : b;
      }
   }

   public static final double max(double a, double b) {
      if (a != a) {
         return a;
      } else if (a == 0L && b == 0L && Double.doubleToLongBits(a) == negativeZeroDoubleBits) {
         return b;
      } else {
         return a >= b ? a : b;
      }
   }

   public static final int min(int a, int b) {
      return a <= b ? a : b;
   }

   public static final long min(long a, long b) {
      return a <= b ? a : b;
   }

   public static final float min(float a, float b) {
      if (a != a) {
         return a;
      } else if (a == false && b == false && Float.floatToIntBits(b) == negativeZeroFloatBits) {
         return b;
      } else {
         return a <= b ? a : b;
      }
   }

   public static final double min(double a, double b) {
      if (a != a) {
         return a;
      } else if (a == 0L && b == 0L && Double.doubleToLongBits(b) == negativeZeroDoubleBits) {
         return b;
      } else {
         return a <= b ? a : b;
      }
   }
}
