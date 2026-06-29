package net.rim.device.api.math;

public final class Fixed32 {
   public static final int MAX_VALUE = Integer.MAX_VALUE;
   public static final int MIN_VALUE = Integer.MIN_VALUE;
   public static final int PI = 205887;
   public static final int E = 178145;
   public static final short NUM_FRACTION_BITS = 16;
   public static final int FP090 = 5898240;
   public static final int FP180 = 11796480;
   public static final int FP270 = 17694720;
   public static final int FP360 = 23592960;
   public static final int RAD2DEG = 3754936;
   public static final int ONE = 65536;
   public static final int HALF = 32768;
   public static final int QUARTER = 16384;
   private static final int INT_TEN_THOU = 10000;
   private static final int INT_FIVE_THOU = 5000;
   private static final int[] POWERS_OF_TEN = new int[]{
      1000, 100, 10, 1, 1866989824, 727916, 1094676481, 1655138688, 1979777150, 846737962, 16781645, 1701539702, -977993472, -1958422706, 262159, -333485820
   };
   public static final int TWOPI = 411774;
   public static final int PI_OVER_2 = 102943;

   private Fixed32() {
   }

   public static final native int radToDeg(int var0);

   public static final native int degToRad(int var0);

   public static final int abs(int a) {
      if (a < 0) {
         if (a == Integer.MIN_VALUE) {
            return Integer.MAX_VALUE;
         }

         a = -a;
      }

      return a;
   }

   public static final native int mul(int var0, int var1);

   public static final native int div(int var0, int var1);

   public static final int toInt(int fp) {
      return fp >> 16;
   }

   public static final int toRoundedInt(int value) {
      if ((value & 32768) != 0) {
         value += 65536;
      }

      return value >> 16;
   }

   public static final int toIntTenThou(int fp) {
      long value = (long)fp * 10000;
      if ((value & 32768) != 0) {
         value += 65536;
      }

      return (int)(value >> 16);
   }

   public static final int toFP(int i) {
      return i << 16;
   }

   public static final int tenThouToFP(int tenThou) {
      int rounder = tenThou < 0 ? -5000 : 5000;
      return (int)((((long)tenThou << 16) + rounder) / 10000);
   }

   public static final native int sqrt(int var0);

   public static final native int sind(int var0);

   public static final native int cosd(int var0);

   public static final native int tand(int var0);

   public static final native int atand2(int var0, int var1);

   public static final int ceil(int val) {
      return -floor(-val);
   }

   public static final native int floor(int var0);

   public static final native int round(int var0);

   public static final native int Sin(int var0);

   public static final native int Cos(int var0);

   public static final native int Tan(int var0);

   public static final native int ArcTan(int var0);

   public static final native int ArcCos(int var0);

   public static final int parseFixed32(String value) {
      if (value == null) {
         throw new NumberFormatException();
      }

      int wholePart = 0;
      int fractionalPart = 0;
      int indexOfDecimal = value.indexOf(46);
      if (indexOfDecimal == -1) {
         wholePart = Integer.parseInt(value);
      } else {
         wholePart = indexOfDecimal == 0 ? 0 : Integer.parseInt(value.substring(0, indexOfDecimal));
         int stringSize = value.length();
         if (stringSize == indexOfDecimal + 1) {
            throw new NumberFormatException();
         }

         for (int i = 3; i >= 0; i--) {
            int position = indexOfDecimal + i + 1;
            int valueAtPosition = position >= stringSize ? 0 : value.charAt(position) - '0';
            if (valueAtPosition < 0 || valueAtPosition > 9) {
               throw new NumberFormatException();
            }

            fractionalPart += valueAtPosition * POWERS_OF_TEN[i];
         }

         fractionalPart = div(fractionalPart << 16, 655360000);
      }

      boolean positive = value.indexOf(45) == -1;
      return positive ? (wholePart << 16) + fractionalPart : (wholePart << 16) - fractionalPart;
   }

   public static final int divtoInt(int n, int m) {
      long ln = n;
      long lm = m;
      long ret = (ln << 32) / lm;
      ret += 2147483648L;
      return (int)(ret >> 32);
   }
}
