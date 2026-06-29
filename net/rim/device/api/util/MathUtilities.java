package net.rim.device.api.util;

public final class MathUtilities {
   private MathUtilities() {
   }

   public static final int clamp(int low, int value, int high) {
      if (value < low) {
         return low;
      } else {
         return value > high ? high : value;
      }
   }

   public static final int wrap(int low, int value, int high) {
      if (value < low) {
         return high;
      } else {
         return value > high ? low : value;
      }
   }

   public static final int log2(int value) {
      return log2(value & 4294967295L);
   }

   public static final int log2(long value) {
      int result = 0;
      if ((value & -4294967296L) != 0) {
         value >>>= 32;
         result |= 32;
      }

      if ((value & -65536) != 0) {
         value >>>= 16;
         result |= 16;
      }

      if ((value & 65280) != 0) {
         value >>>= 8;
         result |= 8;
      }

      if ((value & 240) != 0) {
         value >>>= 4;
         result |= 4;
      }

      if ((value & 12) != 0) {
         value >>>= 2;
         result |= 2;
      }

      if ((value & 2) != 0) {
         result |= 1;
      }

      return result;
   }

   public static final native double log(double var0);

   public static final native double exp(double var0);
}
