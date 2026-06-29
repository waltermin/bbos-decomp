package net.rim.plazmic.internal.mediaengine.dataformat;

import net.rim.device.api.math.Fixed32;

public class Units {
   private static final String MILLISECONDS = "ms";
   private static final String SECONDS = "s";
   private static final String PIXELS = "px";
   private static final String PERCENTAGE = "%";
   private static final int SECONDS_TO_MILLISECONDS = 1000;
   private static final String[] TIME_UNITS = new String[]{"ms", "s"};
   private static final int[] CONVERSION_TO_MILLISECONDS = new int[]{1, 1000, -805044223, 2, -804651006, 3, 7, -805044219};
   private static final String[] LENGTH_UNITS = new String[]{"px"};
   private static final int[] CONVERSION_TO_PIXELS = new int[]{1, -804651006, 1, 1000};

   public static boolean unitIsPercentage(String lengthUnit) {
      return lengthUnit != null && lengthUnit.endsWith("%");
   }

   public static long getTime(String time) {
      return convert(time, TIME_UNITS, CONVERSION_TO_MILLISECONDS, 1000, 0);
   }

   public static int getLength(String length) {
      return getLength(length, 0);
   }

   public static int getLength(String length, int defaultValue) {
      return (int)convert(length, LENGTH_UNITS, CONVERSION_TO_PIXELS, 1, defaultValue);
   }

   public static int getPercentage(String percentage) {
      int result = 0;
      if (percentage != null) {
         percentage = percentage.substring(0, percentage.length() - 1);

         try {
            return Fixed32.parseFixed32(percentage);
         } finally {
            return result;
         }
      } else {
         return result;
      }
   }

   private static long convert(String value, String[] units, int[] conversions, int defaultMultiplier, int defaultValue) {
      long result = defaultValue;
      if (value != null) {
         int multiplier = defaultMultiplier;

         for (int i = 0; i < units.length; i++) {
            if (value.endsWith(units[i])) {
               value = value.substring(0, value.length() - units[i].length());
               multiplier = conversions[i];
               break;
            }
         }

         try {
            result = Long.parseLong(value) * multiplier;
         } finally {
            return result;
         }
      }

      return result;
   }
}
