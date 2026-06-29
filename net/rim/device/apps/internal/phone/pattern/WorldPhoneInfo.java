package net.rim.device.apps.internal.phone.pattern;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.AbstractString;
import net.rim.device.internal.system.RadioInternal;

public class WorldPhoneInfo {
   private static final char[] NV_IDD;
   private static final char[] NV_NDD;
   private static final int[] NPNL_4_12;
   private static final int[] NPNL_6_9;
   private static final int[] NPNL_6_11;
   private static final int[] NPNL_6_12;
   private static final int[] NPNL_6_13;
   private static final int[] NPNL_7_8;
   private static final int[] NPNL_7_9;
   private static final int[] NPNL_7_10;
   private static final int[] NPNL_8_8;
   private static final int[] NPNL_8_9;
   private static final int[] NPNL_8_10;
   private static final int[] NPNL_8_11;
   private static final int[] NPNL_9_9;
   private static final int[] NPNL_9_10;
   private static final int[] NPNL_9_11;
   private static final int[] NPNL_10_10;
   private static final char[] NDD_0;
   private static final char[] NDD_1;
   private static final char[] NDD_00;
   private static final char[] NDD_01;
   private static final char[] NDD_06;
   private static final char[] NDD_09;
   private static final char[] NDD_7;
   private static final char[] NDD_8;
   private static final char[] NDD_22;
   private static final short[] NDD_0_CC;
   private static final short[] NDD_1_CC;
   private static final short[] NDD_8_CC;
   private static final char[] IDD_00;
   private static final char[] IDD_09;
   private static final char[] IDD_001;
   private static final char[] IDD_010;
   private static final char[] IDD_011;
   private static final char[] IDD_0011;
   private static final short[] IDD_00_CC;
   private static final String[] TOLL_FREE_UK;
   private static final String[] SPECIAL_NATIONAL_HONGKONG;
   private static short[] _validCountryCodes;

   public static int[] getNationalPhoneNumberLengthRange(int countryCode) {
      switch (countryCode) {
         case 1:
         case 44:
         case 54:
         case 58:
         case 90:
            return NPNL_10_10;
         case 20:
         case 32:
         case 36:
         case 51:
         case 64:
         case 886:
            return NPNL_8_9;
         case 27:
         case 31:
         case 33:
         case 34:
         case 61:
         case 351:
         case 420:
            return NPNL_9_9;
         case 30:
         case 55:
         case 92:
            return NPNL_9_10;
         case 39:
            return NPNL_6_13;
         case 40:
         case 45:
         case 47:
         case 212:
         case 800:
         case 852:
            return NPNL_8_8;
         case 41:
            return NPNL_8_10;
         case 43:
            return NPNL_4_12;
         case 46:
         case 82:
         case 84:
         case 353:
            return NPNL_7_9;
         case 48:
         case 49:
            return NPNL_6_12;
         case 52:
         case 60:
         case 66:
            return NPNL_7_10;
         case 63:
         case 91:
            return NPNL_6_11;
         case 65:
            return NPNL_7_8;
         case 81:
         case 598:
            return NPNL_8_11;
         case 86:
            return NPNL_9_11;
         case 358:
            return NPNL_6_9;
         default:
            return null;
      }
   }

   public static int getDefaultNationalPhoneNumberLength(int countryCode) {
      switch (countryCode) {
         case 39:
         case 92:
            return 10;
         case 51:
         case 66:
            return 9;
         case 65:
         case 598:
            return 8;
         default:
            int[] range = getNationalPhoneNumberLengthRange(countryCode);
            return range != null && range.length == 2 ? (range[0] + range[1]) / 2 : -1;
      }
   }

   public static char[] getNationalDialingDigits(int countryCode) {
      if (NV_NDD != null && countryCode == SmartDialingOptions.getOptions().getCountryCode()) {
         return NV_NDD;
      }

      short cc = (short)countryCode;
      if (arrayBinarySearch(NDD_1_CC, cc) >= 0) {
         return NDD_1;
      }

      if (arrayBinarySearch(NDD_0_CC, cc) >= 0) {
         return NDD_0;
      }

      if (arrayBinarySearch(NDD_8_CC, cc) >= 0) {
         return NDD_8;
      }

      switch (countryCode) {
         case 36:
            return NDD_06;
         case 52:
            return NDD_01;
         case 57:
            return NDD_09;
         case 213:
            return NDD_7;
         case 231:
            return NDD_22;
         case 682:
            return NDD_00;
         default:
            return null;
      }
   }

   public static boolean canNationalNumbersStartWithNDD(int cc) {
      switch (cc) {
         case 39:
            return true;
         default:
            return false;
      }
   }

   public static char[] getInternationalDialingDigits(int countryCode, int mobilityCC) {
      if (NV_IDD != null) {
         return NV_IDD;
      }

      if (countryCode == 1) {
         return IDD_011;
      }

      if (arrayBinarySearch(IDD_00_CC, (short)countryCode) >= 0) {
         return IDD_00;
      }

      switch (countryCode) {
         case 27:
            return IDD_09;
         case 61:
            return IDD_0011;
         case 65:
         case 66:
         case 82:
         case 852:
            return IDD_001;
         case 81:
            return IDD_010;
         case 886:
            return null;
         default:
            return null;
      }
   }

   private static String[] getSpecialNationalPrefixStrings(int countryCode) {
      switch (countryCode) {
         case 44:
            return TOLL_FREE_UK;
         case 852:
            return SPECIAL_NATIONAL_HONGKONG;
         default:
            return null;
      }
   }

   public static boolean isSpecialNationalNumber(StringBuffer numberBuf, int countryCode) {
      if (numberBuf != null && numberBuf.length() != 0) {
         String[] prefix = getSpecialNationalPrefixStrings(countryCode);
         if (prefix == null) {
            return false;
         }

         int prefixIndex = 0;
         int charCount = 0;
         char numberChar = numberBuf.charAt(charCount);

         while (prefixIndex < prefix.length) {
            String prefixString = prefix[prefixIndex];
            char prefixChar = prefixString.charAt(charCount);
            if (prefixChar == numberChar) {
               if (++charCount == prefixString.length()) {
                  return true;
               }

               if (charCount == numberBuf.length()) {
                  return false;
               }

               numberChar = numberBuf.charAt(charCount);
            } else {
               if (prefixChar <= numberChar) {
                  break;
               }

               prefixIndex++;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static short[] getCountryCodes() {
      return _validCountryCodes;
   }

   public static boolean isValidCountryCode(int countryCode) {
      if (countryCode < 10) {
         return countryCode == 0 || countryCode == 1 || countryCode == 7;
      }

      int idx = arrayBinarySearch(_validCountryCodes, (short)countryCode);
      return idx >= 0;
   }

   private static int arrayBinarySearch(short[] array, short value) {
      int idxA = 0;
      int idxB = array.length;

      while (idxB > idxA) {
         int idx = (idxA + idxB) / 2;
         short v = array[idx];
         if (v == value) {
            return idx;
         }

         if (v > value) {
            idxB = idx;
         } else {
            idxA = idx + 1;
         }
      }

      return -1;
   }

   public static int mapCountryCode(String country) {
      System.out.println("Phone mapCC (" + country + ')');
      if (country.length() != 2) {
         return -1;
      }

      short val = (short)(country.charAt(0) * 256 + country.charAt(1));
      short[] set = new short[]{
         16708,
         376,
         16709,
         971,
         16710,
         93,
         16713,
         264,
         16716,
         355,
         16717,
         374,
         16718,
         599,
         16719,
         244,
         16722,
         54,
         16723,
         684,
         16724,
         43,
         16725,
         61,
         16727,
         297,
         16730,
         994,
         16961,
         387,
         16964,
         880,
         16965,
         32,
         16966,
         226,
         16967,
         359,
         16968,
         973,
         16969,
         257,
         16970,
         229,
         16974,
         673,
         16975,
         591,
         16978,
         55,
         16980,
         975,
         16983,
         267,
         16985,
         375,
         16986,
         501,
         17217,
         1,
         17222,
         236,
         17224,
         41,
         17225,
         225,
         17227,
         682,
         17228,
         56,
         17229,
         237,
         17230,
         86,
         17231,
         57,
         17234,
         506,
         17237,
         53,
         17238,
         238,
         17241,
         357,
         17242,
         420,
         17477,
         49,
         17482,
         253,
         17483,
         45,
         17498,
         213,
         17731,
         593,
         17733,
         372,
         17735,
         20,
         17746,
         291,
         17747,
         34,
         17748,
         251,
         17993,
         358,
         17994,
         679,
         17995,
         500,
         17997,
         691,
         17999,
         298,
         18002,
         33,
         18241,
         241,
         18242,
         44,
         18245,
         995,
         18246,
         594,
         18248,
         233,
         18249,
         350,
         18252,
         299,
         18253,
         220,
         18254,
         224,
         18256,
         590,
         18257,
         240,
         18258,
         30,
         18260,
         502,
         18263,
         245,
         18265,
         592,
         18507,
         852,
         18510,
         504,
         18514,
         385,
         18516,
         509,
         18517,
         36,
         18756,
         62,
         18757,
         353,
         18764,
         972,
         18766,
         91,
         18769,
         964,
         18770,
         98,
         18771,
         354,
         18772,
         39,
         19023,
         962,
         19024,
         81,
         19269,
         254,
         19271,
         996,
         19272,
         855,
         19273,
         686,
         19277,
         269,
         19280,
         850,
         19282,
         82,
         19285,
         965,
         19521,
         856,
         19522,
         961,
         19529,
         423,
         19531,
         94,
         19538,
         231,
         19539,
         266,
         19540,
         370,
         19541,
         352,
         19542,
         371,
         19545,
         218,
         19777,
         212,
         19779,
         377,
         19780,
         373,
         19783,
         261,
         19784,
         692,
         19787,
         389,
         19788,
         223,
         19789,
         95,
         19790,
         976,
         19791,
         853,
         19793,
         596,
         19794,
         222,
         19796,
         356,
         19797,
         230,
         19798,
         960,
         19799,
         265,
         19800,
         52,
         19801,
         60,
         19802,
         258,
         20033,
         264,
         20035,
         687,
         20037,
         227,
         20039,
         234,
         20041,
         505,
         20044,
         31,
         20047,
         47,
         20048,
         977,
         20050,
         674,
         20053,
         683,
         20058,
         64,
         20301,
         968,
         20545,
         507,
         20549,
         51,
         20550,
         689,
         20551,
         675,
         20552,
         63,
         20555,
         92,
         20556,
         48,
         20564,
         351,
         20567,
         680,
         20569,
         595,
         20801,
         974,
         21061,
         262,
         21071,
         40,
         21077,
         7,
         21079,
         250,
         21313,
         966,
         21314,
         677,
         21315,
         248,
         21316,
         249,
         21317,
         46,
         21319,
         65,
         21321,
         386,
         21323,
         421,
         21324,
         232,
         21325,
         378,
         21326,
         221,
         21327,
         252,
         21330,
         597,
         21332,
         239,
         21334,
         503,
         21337,
         963,
         21338,
         268,
         21572,
         235,
         21575,
         228,
         21576,
         66,
         21579,
         690,
         21581,
         993,
         21582,
         216,
         21583,
         676,
         21584,
         670,
         21586,
         90,
         21590,
         688,
         21591,
         886,
         21594,
         255,
         21825,
         380,
         21831,
         256,
         21843,
         1,
         21849,
         598,
         21850,
         998,
         22081,
         379,
         22085,
         58,
         22094,
         84,
         22101,
         678,
         22342,
         681,
         22853,
         967,
         23105,
         27,
         23117,
         260,
         23127,
         263,
         22,
         -12278,
         16976,
         -29604,
         -13193,
         -26098,
         25980,
         -22583,
         18602,
         -21397,
         14928,
         -19557,
         -19773,
         -16438,
         -15376,
         -15589,
         -29763,
         -3549,
         -867,
         -213,
         7933,
         227,
         -14371,
         268,
         4650,
         3074,
         -3523,
         4638,
         -867,
         6760,
         25974,
         6826,
         -1930,
         12733,
         -29712,
         12982,
         22320,
         13463,
         759,
         14968,
         -24016,
         16258,
         -10711,
         29758,
         7523,
         31022,
         101,
         -12278,
         24147,
         -32323,
         3754,
         -32117,
         -2032,
         -32077,
         21805,
         -32075,
         31044,
         -31393,
         -24147,
         -31055,
         -6096,
         -31013,
         -3149,
         -30873,
         25683,
         -30311,
         801,
         -30082,
         -13922,
         -29986,
         16976,
         -29604,
         32117,
         -29283,
         -9582,
         -29282,
         2544,
         -28574,
         18208,
         -27979,
         22669,
         -27306,
         -24816,
         -27264,
         29664,
         -26670,
         -12035,
         -26669,
         -3987,
         -24369,
         15759,
         -24280,
         9973,
         -24239,
         -31726,
         -24238,
         15940,
         -21658,
         -28883,
         -21355,
         -29149,
         -19224,
         27709,
         -18962,
         6144,
         -17913,
         16815,
         -17891,
         1312,
         -17792,
         -2740,
         -17342,
         -10414,
         -16349,
         17385,
         -16073,
         -30100,
         -16031,
         -15376,
         -15589,
         -25328,
         -15373,
         6828,
         -15080,
         24879,
         -15038,
         -17533,
         -12586,
         9167,
         -12080,
         2592,
         -11317,
         -3194,
         -11277,
         20643,
         -11275,
         -5265,
         -11051,
         -12014,
         -10738,
         4128,
         -6670,
         13840,
         -6103,
         13933,
         -6103,
         -10304,
         -5641,
         20737,
         -5173,
         20830,
         -5173,
         -15907,
         -4379,
         4218,
         -3110,
         22269,
         -3068,
         7901,
         -1759,
         7994,
         -1759,
         -16778,
         2013,
         7059,
         2015,
         24000,
         3701,
         -20992,
         5492,
         -24717,
         6524,
         -867,
         6760,
         2137,
         6825,
         25974,
         6826,
         26080,
         6996,
         26173,
         6996,
         14918,
         7420,
         -26781,
         7421,
         26387,
         7554,
         6849,
         9787,
         30686,
         9788,
         -20628,
         9871,
         27417,
         11009,
         -14282,
         11010,
         -7779,
         13652,
         16058,
         13654,
         18810,
         15010,
         9299,
         15024,
         4894,
         17495,
         -3147,
         18057,
         20690,
         18059,
         -10314,
         18969,
         1142,
         20279,
         24979,
         20280,
         -27613,
         20354,
         -9734,
         21360,
         -10371,
         21622,
         7680,
         21665,
         -15114,
         23943,
         2128,
         25212,
         -3658,
         25252,
         20179,
         25254,
         11812,
         26033,
         31937,
         27301,
         21043,
         30023,
         15257,
         30064,
         -26442,
         30065,
         -16314,
         31020,
         7523,
         31022,
         -16371,
         32150,
         24,
         -12278,
         24147,
         -32323,
         3754,
         -32117,
         -2032,
         -32077,
         -6096,
         -31013,
         18208,
         -27979,
         22669,
         -27306,
         -12035,
         -26669,
         27709,
         -18962,
         -11907,
         -17956,
         -16739,
         -17835,
         1312,
         -17792,
         -25328,
         -15373,
         4218,
         -3110,
         -10992,
         1972,
         7059,
         2015,
         -26781,
         7421,
         -14282,
         11010,
         -1993,
         13611,
         -7779,
         13652,
         -27613,
         20354,
         -10371,
         21622,
         -15114,
         23943,
         2128,
         25212,
         -16314,
         31020,
         1504,
         -12278,
         -8586,
         -32703,
         -9940,
         -32541,
         -15726,
         -32500,
         5228,
         -32498,
         8018,
         -32498,
         8111,
         -32498,
         -24607,
         -32237,
         -5779,
         -32216,
         -7905,
         -32198,
         3754,
         -32117,
         -2032,
         -32077,
         18922,
         -32075,
         21712,
         -32075,
         21805,
         -32075,
         -30856,
         -32028,
         -7019,
         -32027,
         -23042,
         -31984,
         -17696,
         -31954,
         -13487,
         -31671,
         16192,
         -31515,
         16285,
         -31515,
         10499,
         -31474,
         -31293,
         -31473,
         -31200,
         -31473,
         -28706,
         -31410,
         -8131,
         -31269,
         -5341,
         -31269,
         -5248,
         -31269,
         -6096,
         -31013,
         17499,
         -30954,
         23326,
         -30929,
         -7658,
         -30853,
         -6658,
         -30547,
         -14714,
         -30518,
         26917,
         -30119,
         -15023,
         -29712,
         -14930,
         -29712,
         9728,
         -29675,
         12518,
         -29675,
         12611,
         -29675,
         11692,
         -29674,
         -20716,
         -29671,
         238,
         -29669,
         3028,
         -29669,
         3121,
         -29669
      };
      int len = set.length;

      for (int idx = 0; idx < len; idx += 2) {
         if (set[idx] == val) {
            return set[idx + 1];
         }
      }

      return -1;
   }

   public static int parseCountryCode(String str) {
      if (str != null) {
         int countryCode = 0;
         int len = str.length();

         for (int idx = 0; idx < len; idx++) {
            char c = str.charAt(idx);
            if (c >= '0' && c <= '9') {
               countryCode = countryCode * 10 + (c - '0');
               if (isValidCountryCode(countryCode)) {
                  return countryCode;
               }
            }
         }
      }

      return -1;
   }

   public static int parseCountryCode(StringBuffer str) {
      if (str != null) {
         int countryCode = 0;
         int len = str.length();

         for (int idx = 0; idx < len; idx++) {
            char c = str.charAt(idx);
            if (c >= '0' && c <= '9') {
               countryCode = countryCode * 10 + (c - '0');
               if (isValidCountryCode(countryCode)) {
                  return countryCode;
               }
            }
         }
      }

      return -1;
   }

   public static int parseCountryCode(byte[] bytes) {
      if (bytes != null) {
         int countryCode = 0;
         int len = bytes.length;

         for (int idx = 0; idx < len; idx++) {
            char c = (char)bytes[idx];
            if (c >= '0' && c <= '9') {
               countryCode = countryCode * 10 + (c - '0');
               if (isValidCountryCode(countryCode)) {
                  return countryCode;
               }
            }
         }
      }

      return -1;
   }

   static int parseCountryCode(AbstractString str, int startIndex) {
      int countryCode = 0;
      int len = str.length();

      for (int idx = startIndex; idx < len; idx++) {
         char c = str.charAt(idx);
         if (Character.isDigit(c)) {
            countryCode = countryCode * 10 + (c - '0');
            if (isValidCountryCode(countryCode)) {
               return countryCode;
            }
         }
      }

      return -1;
   }

   public static int getCountryCodeLength(int countryCode) {
      if (countryCode < 0 || countryCode >= 1000) {
         throw new IllegalArgumentException();
      } else if (countryCode < 10) {
         return 1;
      } else {
         return countryCode < 100 ? 2 : 3;
      }
   }

   static {
      char[] nv_idd = null;
      char[] nv_ndd = null;
      if (RadioInfo.getNetworkType() == 4) {
         label46:
         try {
            String number = RadioInternal.readNVString(4);
            if (number != null) {
               nv_idd = number.toCharArray();
            }
         } finally {
            break label46;
         }

         label44:
         try {
            String number = RadioInternal.readNVString(3);
            if (number != null) {
               nv_ndd = number.toCharArray();
            }
         } finally {
            break label44;
         }
      }

      NV_IDD = nv_idd;
      NV_NDD = nv_ndd;
      NPNL_4_12 = new int[]{4, 12, -804651006, 6, 9, -804651006, 6, 11};
      NPNL_6_9 = new int[]{6, 9, -804651006, 6, 11, -804651006, 6, 12};
      NPNL_6_11 = new int[]{6, 11, -804651006, 6, 12, -804651006, 6, 13};
      NPNL_6_12 = new int[]{6, 12, -804651006, 6, 13, -804651006, 7, 8};
      NPNL_6_13 = new int[]{6, 13, -804651006, 7, 8, -804651006, 7, 9};
      NPNL_7_8 = new int[]{7, 8, -804651006, 7, 9, -804651006, 7, 10};
      NPNL_7_9 = new int[]{7, 9, -804651006, 7, 10, -804782067, 24248327, 24445299};
      NPNL_7_10 = new int[]{7, 10, -804782067, 24248327, 24445299, 24576374, 65012092, 65143777};
      NPNL_8_8 = new int[]{8, 8, -804651006, 8, 9, -804651006, 8, 10};
      NPNL_8_9 = new int[]{8, 9, -804651006, 8, 10, -804651006, 8, 11};
      NPNL_8_10 = new int[]{8, 10, -804651006, 8, 11, -804651006, 9, 9};
      NPNL_8_11 = new int[]{8, 11, -804651006, 9, 9, -804651006, 9, 10};
      NPNL_9_9 = new int[]{9, 9, -804651006, 9, 10, -804651006, 9, 11};
      NPNL_9_10 = new int[]{9, 10, -804651006, 9, 11, -804651006, 10, 10};
      NPNL_9_11 = new int[]{9, 11, -804651006, 10, 10, -804650992, -1993329394, -1973851867};
      NPNL_10_10 = new int[]{10, 10, -804650992, -1993329394, -1973851867, -1666439585, -1402255190, -686723914};
      NDD_0 = new char[]{'0', '\u0000'};
      NDD_1 = new char[]{'1', '\u0000'};
      NDD_00 = new char[]{'0', '0', '\u0003', '퀆'};
      NDD_01 = new char[]{'0', '1', '\u0003', '퀆'};
      NDD_06 = new char[]{'0', '6', '\u0002', '퀆'};
      NDD_09 = new char[]{'0', '9', '\u0013', '퀊'};
      NDD_7 = new char[]{'7', '\u0000'};
      NDD_8 = new char[]{'8', '\u0000'};
      NDD_22 = new char[]{'2', '2', '\u0001', '퀊'};
      NDD_0_CC = new short[]{
         20,
         27,
         31,
         32,
         33,
         40,
         41,
         43,
         44,
         46,
         47,
         49,
         51,
         53,
         54,
         55,
         56,
         58,
         60,
         61,
         62,
         63,
         64,
         66,
         81,
         82,
         84,
         86,
         90,
         91,
         92,
         93,
         94,
         98,
         212,
         216,
         218,
         221,
         222,
         223,
         224,
         225,
         227,
         230,
         232,
         234,
         239,
         244,
         248,
         249,
         250,
         251,
         254,
         255,
         256,
         258,
         260,
         261,
         262,
         263,
         264,
         266,
         291,
         353,
         354,
         355,
         356,
         357,
         358,
         359,
         372,
         377,
         378,
         381,
         382,
         385,
         386,
         387,
         389,
         421,
         501,
         502,
         504,
         505,
         507,
         508,
         509,
         591,
         592,
         593,
         595,
         596,
         598,
         599,
         673,
         674,
         683,
         686,
         687,
         808,
         850,
         853,
         855,
         856,
         880,
         886,
         960,
         961,
         962,
         963,
         964,
         965,
         966,
         967,
         968,
         970,
         972,
         974,
         976,
         977,
         50,
         -12280,
         20,
         30,
         31,
         32,
         33,
         34,
         36,
         39,
         40,
         41,
         43,
         44,
         46,
         47,
         48,
         49,
         51,
         52,
         54,
         55,
         58,
         60,
         63,
         64,
         66,
         84,
         86,
         90,
         91,
         92,
         212,
         351,
         353,
         354,
         355,
         356,
         357,
         358,
         359,
         372,
         377,
         378,
         381,
         382,
         385,
         386,
         387,
         389,
         420,
         598,
         28,
         -12278,
         -9940,
         -32541,
         21805,
         -32075,
         22669,
         -27306,
         32763,
         -26534,
         31169,
         -24119,
         -15379,
         -21836,
         -28883,
         -21355,
         -23552,
         -20271,
         4749,
         -14320,
         2592,
         -11317,
         29068,
         -9576,
         4128,
         -6670,
         -10304,
         -5641,
         27872,
         -4330,
         -5939,
         -3188,
         4218,
         -3110,
         5754,
         1537,
         7059,
         2015,
         24000,
         3701,
         -4313,
         5019,
         -16595,
         8380,
         21907,
         12735,
         7680,
         21665,
         14322,
         24514,
         20352,
         28112,
         27393,
         28698,
         13126,
         29760,
         31137,
         30795,
         16,
         -12278,
         21805,
         -32075,
         -3987,
         -24369,
         -1523,
         -22666,
         2592,
         -11317
      };
      NDD_1_CC = new short[]{1, 691, 692, 0, 2, -12278};
      NDD_8_CC = new short[]{7, 370, 371, 373, 374, 375, 380, 992, 993, 994, 995, 996, 998, 0, 2, -12278, 8, 0, 8, 0, 2, -12278, 8, 0, 9, 0};
      IDD_00 = new char[]{'0', '0', '\u0003', '퀆'};
      IDD_09 = new char[]{'0', '9', '\u0013', '퀊'};
      IDD_001 = new char[]{'0', '0', '1', '\u0000', '\u0004', '퀆'};
      IDD_010 = new char[]{'0', '1', '0', '\u0000', '\u0003', '퀆'};
      IDD_011 = new char[]{'0', '1', '1', '\u0000', '\u0002', '퀆'};
      IDD_0011 = new char[]{'0', '0', '1', '1', '\u0002', '퀆', '0', '1'};
      IDD_00_CC = new short[]{
         20,
         30,
         31,
         32,
         33,
         34,
         36,
         39,
         40,
         41,
         43,
         44,
         46,
         47,
         48,
         49,
         51,
         52,
         54,
         55,
         58,
         60,
         63,
         64,
         66,
         84,
         86,
         90,
         91,
         92,
         212,
         351,
         353,
         354,
         355,
         356,
         357,
         358,
         359,
         372,
         377,
         378,
         381,
         382,
         385,
         386,
         387,
         389,
         420,
         598,
         28,
         -12278,
         -9940,
         -32541,
         21805,
         -32075,
         22669,
         -27306,
         32763,
         -26534,
         31169,
         -24119,
         -15379,
         -21836,
         -28883,
         -21355,
         -23552,
         -20271,
         4749,
         -14320,
         2592,
         -11317,
         29068,
         -9576,
         4128,
         -6670,
         -10304,
         -5641,
         27872,
         -4330,
         -5939,
         -3188,
         4218,
         -3110,
         5754,
         1537,
         7059,
         2015,
         24000,
         3701,
         -4313,
         5019,
         -16595,
         8380,
         21907,
         12735,
         7680,
         21665,
         14322,
         24514
      };
      TOLL_FREE_UK = new String[]{"90", "80", "09", "08", "070", "0645", "0541", "0500", "0345"};
      SPECIAL_NATIONAL_HONGKONG = new String[]{"133"};
      _validCountryCodes = new short[]{
         1,
         7,
         20,
         27,
         28,
         30,
         31,
         32,
         33,
         34,
         36,
         39,
         40,
         41,
         43,
         44,
         45,
         46,
         47,
         48,
         49,
         51,
         52,
         53,
         54,
         55,
         56,
         57,
         58,
         60,
         61,
         62,
         63,
         64,
         65,
         66,
         81,
         82,
         83,
         84,
         86,
         89,
         90,
         91,
         92,
         93,
         94,
         95,
         98,
         210,
         211,
         212,
         213,
         214,
         215,
         216,
         217,
         218,
         219,
         220,
         221,
         222,
         223,
         224,
         225,
         226,
         227,
         228,
         229,
         230,
         231,
         232,
         233,
         234,
         235,
         236,
         237,
         238,
         239,
         240,
         241,
         242,
         243,
         244,
         245,
         246,
         247,
         248,
         249,
         250,
         251,
         252,
         253,
         254,
         255,
         256,
         257,
         258,
         259,
         260,
         261,
         262,
         263,
         264,
         265,
         266,
         267,
         268,
         269,
         290,
         291,
         292,
         293,
         294,
         295,
         296,
         297,
         298,
         299,
         350,
         351,
         352,
         353,
         354,
         355,
         356,
         357,
         358,
         359,
         370,
         371,
         372,
         373,
         374,
         375,
         376,
         377,
         378,
         379,
         380,
         381,
         382,
         383,
         384,
         385,
         386,
         387,
         388,
         389,
         420,
         421,
         422,
         423,
         424,
         425,
         426,
         427,
         428,
         429,
         500,
         501,
         502,
         503,
         504,
         505,
         506,
         507,
         508,
         509,
         590,
         591,
         592,
         593,
         594,
         595,
         596,
         597,
         598,
         599,
         670,
         671,
         672,
         673,
         674,
         675,
         676,
         677,
         678,
         679,
         680,
         681,
         682,
         683,
         684,
         685,
         686,
         687,
         688,
         689,
         690,
         691,
         692,
         693,
         694,
         695,
         696,
         697,
         698,
         699,
         800,
         801,
         802,
         803,
         804,
         805,
         806,
         807,
         808,
         809,
         850,
         851,
         852,
         853,
         854,
         855,
         856,
         857,
         858,
         859,
         870,
         871,
         872,
         873,
         874,
         875,
         876,
         877,
         878,
         879,
         880,
         881,
         882,
         883,
         884,
         885,
         886,
         887,
         888,
         889,
         960,
         961,
         962,
         963,
         964,
         965,
         966,
         967,
         968,
         969,
         970,
         971,
         972,
         973,
         974,
         975,
         976,
         977,
         978,
         979,
         990,
         991,
         992,
         993,
         994,
         995,
         996,
         997,
         998,
         999,
         0,
         3,
         -12280,
         1,
         691,
         692,
         0,
         2,
         -12278,
         4,
         0,
         12,
         0,
         2,
         -12278,
         6,
         0,
         9,
         0,
         2,
         -12278,
         6,
         0,
         11,
         0,
         2,
         -12278,
         6,
         0,
         12,
         0,
         2,
         -12278,
         6,
         0,
         13,
         0,
         2,
         -12278,
         7,
         0,
         8,
         0,
         2,
         -12278,
         7,
         0,
         9,
         0,
         2,
         -12278,
         7,
         0,
         10,
         0,
         13,
         -12280,
         7,
         370,
         371,
         373,
         374,
         375,
         380,
         992,
         993,
         994,
         995,
         996,
         998,
         0,
         2,
         -12278,
         8,
         0,
         8,
         0,
         2,
         -12278,
         8,
         0,
         9,
         0,
         2,
         -12278,
         8,
         0,
         10,
         0,
         2,
         -12278,
         8,
         0,
         11,
         0,
         2,
         -12278,
         9,
         0,
         9,
         0,
         2,
         -12278,
         9,
         0,
         10,
         0,
         2,
         -12278,
         9,
         0,
         11,
         0,
         2,
         -12278,
         10,
         0,
         10,
         0,
         16,
         -12278,
         13582,
         -30416,
         26917,
         -30119,
         9823,
         -25428,
         18602,
         -21397,
         27830,
         -10479,
         18391,
         -2451,
         17195,
         2795,
         25974,
         6826,
         759,
         14968,
         -24016,
         16258,
         9838,
         16599,
         21716,
         17646,
         -9246,
         21074,
         30495,
         21677,
         -167,
         22705,
         7523,
         31022,
         6,
         -12278,
         21264,
         -32323,
         -24147,
         -31055,
         -30086,
         -26711,
         -11907,
         -17956,
         6144,
         -17913,
         -20944,
         16334,
         7,
         -12278,
         21264,
         -32323,
         -3149,
         -30873,
         -11907,
         -17956,
         31219,
         -4654,
         4218,
         -3110,
         -20944,
         16334,
         7680,
         21665,
         150,
         -12278,
         21264,
         -32323,
         24054,
         -32323,
         24147,
         -32323,
         21712,
         -32075,
         21805,
         -32075,
         2401,
         -32001,
         -24147,
         -31055,
         -29933,
         -31014,
         -8979,
         -31013,
         -6189,
         -31013,
         -6096,
         -31013,
         16976,
         -29604,
         8143,
         -29589,
         11190,
         -29563,
         32144,
         -29562,
         18208,
         -27979,
         -24909,
         -27264,
         -24816,
         -27264,
         -30086,
         -26711,
         29664,
         -26670,
         -14918,
         -26669,
         -12128,
         -26669,
         -12035,
         -26669,
         26529,
         -26630,
         16111,
         -25572,
         -6870,
         -24369,
         -4080,
         -24369,
         -3987,
         -24369,
         21505,
         -22997,
         -22353,
         -22941,
         -1523,
         -22666,
         18509,
         -21397,
         18602,
         -21397,
         12816,
         -21356,
         -28976,
         -21355,
         -28883,
         -21355,
         -26435,
         -20271,
         -23645,
         -20271,
         -23552,
         -20271,
         -3967,
         -19982,
         26223,
         -19886,
         12045,
         -19557,
         14835,
         -19557,
         14928,
         -19557,
         -29149,
         -19224,
         24826,
         -18962,
         27616,
         -18962
      };
   }
}
