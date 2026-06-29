package net.rim.device.apps.internal.phone.pattern;

import net.rim.device.api.util.Arrays;

class CannedHashPatterns {
   private static final int[] _International_1_Digit_CC;
   private static final int[] _International_2_Digit_CC;
   private static final int[] _International_3_Digit_CC;
   private static final int[] _Local_CC_1;
   private static final int[] _Local_CC_20;
   private static final int[] _Local_CC_27;
   private static final int[] _Local_CC_30;
   private static final int[] _Local_CC_31;
   private static final int[] _Local_CC_32;
   private static final int[] _Local_CC_33;
   private static final int[] _Local_CC_34;
   private static final int[] _Local_CC_36;
   private static final int[] _Local_CC_39;
   private static final int[] _Local_CC_41;
   private static final int[] _Local_CC_43;
   private static final int[] _Local_CC_44;
   private static final int[] _Local_CC_46;
   private static final int[] _Local_CC_47;
   private static final int[] _Local_CC_49;
   private static final int[] _Local_CC_52;
   private static final int[] _Local_CC_55;
   private static final int[] _Local_CC_58;
   private static final int[] _Local_CC_61;
   private static final int[] _Local_CC_64;
   private static final int[] _Local_CC_65;
   private static final int[] _Local_CC_86;
   private static final int[] _Local_CC_90;
   private static final int[] _Local_CC_351;
   private static final int[] _Local_CC_353;
   private static final int[] _Local_CC_358;
   private static final int[] _Local_CC_420;
   private static final int[] _Local_CC_852;
   private static final int[] _Local_CC_886;

   public static boolean isInternational(int hashVal, int countryCode) {
      if (countryCode > 0) {
         if (countryCode < 10) {
            return searchHashVal(hashVal, _International_1_Digit_CC);
         }

         if (countryCode < 100) {
            return searchHashVal(hashVal, _International_2_Digit_CC);
         }

         if (countryCode < 1000) {
            return searchHashVal(hashVal, _International_3_Digit_CC);
         }
      }

      return false;
   }

   static boolean isLocal(int hashVal, int countryCode) {
      switch (countryCode) {
         case 1:
            return searchHashVal(hashVal, _Local_CC_1);
         case 20:
            return searchHashVal(hashVal, _Local_CC_20);
         case 27:
            return searchHashVal(hashVal, _Local_CC_27);
         case 30:
            return searchHashVal(hashVal, _Local_CC_30);
         case 31:
            return searchHashVal(hashVal, _Local_CC_31);
         case 32:
            return searchHashVal(hashVal, _Local_CC_32);
         case 33:
            return searchHashVal(hashVal, _Local_CC_33);
         case 34:
            return searchHashVal(hashVal, _Local_CC_34);
         case 36:
            return searchHashVal(hashVal, _Local_CC_36);
         case 39:
            return searchHashVal(hashVal, _Local_CC_39);
         case 41:
            return searchHashVal(hashVal, _Local_CC_41);
         case 43:
            return searchHashVal(hashVal, _Local_CC_43);
         case 44:
            return searchHashVal(hashVal, _Local_CC_44);
         case 46:
            return searchHashVal(hashVal, _Local_CC_46);
         case 47:
            return searchHashVal(hashVal, _Local_CC_47);
         case 49:
            return searchHashVal(hashVal, _Local_CC_49);
         case 52:
            return searchHashVal(hashVal, _Local_CC_52);
         case 55:
            return searchHashVal(hashVal, _Local_CC_55);
         case 58:
            return searchHashVal(hashVal, _Local_CC_58);
         case 61:
            return searchHashVal(hashVal, _Local_CC_61);
         case 64:
            return searchHashVal(hashVal, _Local_CC_64);
         case 65:
            return searchHashVal(hashVal, _Local_CC_65);
         case 86:
            return searchHashVal(hashVal, _Local_CC_86);
         case 90:
            return searchHashVal(hashVal, _Local_CC_90);
         case 351:
            return searchHashVal(hashVal, _Local_CC_351);
         case 353:
            return searchHashVal(hashVal, _Local_CC_353);
         case 358:
            return searchHashVal(hashVal, _Local_CC_358);
         case 420:
            return searchHashVal(hashVal, _Local_CC_420);
         case 852:
            return searchHashVal(hashVal, _Local_CC_852);
         case 886:
            return searchHashVal(hashVal, _Local_CC_886);
         default:
            return false;
      }
   }

   static boolean searchHashVal(int hashVal, int[] patternHashVals) {
      int len = patternHashVals.length;
      int idx = Arrays.binarySearch(patternHashVals, hashVal, 0, len);
      return idx >= 0;
   }
}
