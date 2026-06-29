package net.rim.plazmic.internal.math;

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
   private static final int C9 = 1365;
   private static final int C7 = -5579;
   private static final int C5 = 11806;
   private static final int C3 = -21646;
   private static final int C1 = 65527;
   private static final int ONE = 65536;
   private static final int HALF = 32768;
   private static final int TEN_THOU = 10000;
   private static final int FIVE_THOU = 5000;
   private static final short[] A = new short[]{
      18300,
      18295,
      18283,
      18267,
      18244,
      18217,
      18183,
      18144,
      18100,
      18050,
      17994,
      17933,
      17867,
      17795,
      17718,
      17635,
      17547,
      17454,
      17355,
      17251,
      17142,
      17027,
      16908,
      16783,
      16653,
      16518,
      16378,
      16233,
      16083,
      15928,
      15769,
      15604,
      15435,
      15261,
      15082,
      14899,
      14711,
      14519,
      14322,
      14121,
      13916,
      13707,
      13493,
      13275,
      13053,
      12827,
      12597,
      12364,
      12127,
      11885,
      11641,
      11393,
      11141,
      10886,
      10627,
      10366,
      10101,
      9833,
      9562,
      9288,
      9012,
      8732,
      8450,
      8166,
      7879,
      7589,
      7297,
      7003,
      6707,
      6409,
      6109,
      5807,
      5503,
      5198,
      4891,
      4582,
      4272,
      3961,
      3649,
      3335,
      3021,
      2705,
      2389,
      2072,
      1754,
      1436,
      1117,
      798,
      479,
      160,
      0,
      2048,
      24899,
      30067,
      18463,
      29537,
      104,
      10,
      14,
      8465,
      12900,
      5632,
      5422,
      5934,
      3118,
      28531,
      1966,
      115,
      11798,
      11797,
      -22672,
      28026,
      11869,
      2975,
      8046,
      27950,
      26641,
      5632,
      5471,
      28767,
      31399,
      23917,
      27999,
      26641,
      18770,
      -24499,
      28531,
      1966,
      115,
      24904,
      26739,
      -28845,
      18432,
      29537,
      29800,
      25185,
      126,
      27721,
      26494,
      16671,
      26482,
      -30065,
      152,
      25167,
      -15510,
      23296,
      11885,
      11797,
      29452,
      -20881,
      29447,
      23296,
      25715,
      24320,
      29452,
      -20881,
      29447,
      24320,
      29452,
      -20881,
      -27641,
      4980,
      7283,
      115,
      6239,
      18032,
      26535,
      115,
      6239,
      -22928,
      29858,
      24320,
      8304,
      26972,
      29541,
      24320,
      8819,
      -28555,
      17529,
      29541,
      29283,
      -30615
   };
   private static final short[] B = new short[]{
      0,
      0,
      2,
      5,
      10,
      19,
      32,
      49,
      71,
      99,
      134,
      176,
      225,
      284,
      351,
      429,
      517,
      616,
      727,
      851,
      987,
      1137,
      1302,
      1481,
      1676,
      1887,
      2115,
      2359,
      2622,
      2902,
      3202,
      3521,
      3859,
      4218,
      4598,
      4998,
      5421,
      5865,
      6332,
      6822,
      7335,
      7872,
      8433,
      9019,
      9629,
      10264,
      10925,
      11611,
      12323,
      13061,
      13826,
      14617,
      15435,
      16280,
      17152,
      18052,
      18979,
      19933,
      20915,
      21924,
      22962,
      24027,
      25120,
      26240,
      27388,
      28564,
      29768,
      30999,
      32258,
      -31992,
      -30679,
      -29339,
      -27972,
      -26578,
      -25158,
      -23712,
      -22240,
      -20742,
      -19219,
      -17671,
      -16098,
      -14501,
      -12880,
      -11236,
      -9568,
      -7877,
      -6165,
      -4430,
      -2675,
      -898,
      -1,
      0,
      11,
      -12284,
      2304,
      11828,
      11827,
      11824,
      12849,
      52,
      25,
      -12284,
      5888,
      25938,
      25971,
      29281,
      26723,
      18720,
      8302,
      28493,
      26996,
      28271,
      19488,
      25716,
      46,
      0,
      1,
      -12284,
      2,
      0,
      1,
      -12278,
      51,
      0,
      91,
      -12280,
      18300,
      18295,
      18283,
      18267,
      18244,
      18217,
      18183,
      18144,
      18100,
      18050,
      17994,
      17933,
      17867,
      17795,
      17718,
      17635,
      17547,
      17454,
      17355,
      17251,
      17142,
      17027,
      16908,
      16783,
      16653,
      16518,
      16378,
      16233,
      16083,
      15928,
      15769,
      15604,
      15435,
      15261,
      15082,
      14899,
      14711,
      14519,
      14322,
      14121,
      13916,
      13707,
      13493,
      13275,
      13053,
      12827,
      12597,
      12364,
      12127,
      11885,
      11641,
      11393,
      11141,
      10886,
      10627,
      10366
   };

   private Fixed32() {
   }

   public static final int mul(int n, int m) {
      return (int)((long)n * m >> 16);
   }

   public static final int div(int n, int m) {
      return (int)(((long)n << 16) / m);
   }

   public static final int toInt(int fp) {
      return fp >> 16;
   }

   public static final int toIntTenThou(int fp) {
      int rounder = fp < 0 ? -32768 : 32768;
      return (int)(((long)fp * 10000 + rounder) / 65536);
   }

   public static final int toFP(int i) {
      return i << 16;
   }

   public static final int tenThouToFP(int tenThou) {
      int rounder = tenThou < 0 ? -5000 : 5000;
      return (int)((((long)tenThou << 16) + rounder) / 10000);
   }

   public static final int sqrt(int n) {
      if (n > 0) {
         int i = 0;

         for (int mask = 32768; mask != 0; mask >>= 1) {
            if ((long)(mask + i) * (mask + i) <= n) {
               i += mask;
            }
         }

         i <<= 8;
         i = i + div(n, i) + 1 >> 1;
         return i + div(n, i) + 1 >> 1;
      } else if (n == 0) {
         return 0;
      } else {
         throw new IllegalArgumentException("Sqrt of neg");
      }
   }

   private static final int sinusoid(int ang, short s) {
      ang -= (ang / 23592960 - (ang < 0 ? 1 : 0)) * 23592960;
      if (s != 0) {
         ang += 5898240;
      } else if (ang < 5898240) {
         ang += 23592960;
      }

      short quadrant;
      if (ang < 11796480) {
         quadrant = 2;
         ang = 11796480 - ang;
      } else if (ang < 17694720) {
         quadrant = 3;
         ang -= 11796480;
      } else if (ang < 23592960) {
         quadrant = 4;
         ang = 23592960 - ang;
      } else {
         quadrant = 1;
         ang -= 23592960;
      }

      int interval = ang >> 16;
      int result = (int)((long)A[interval] * ang >> 20) + (B[interval] & '\uffff');
      return quadrant > 2 ? -result : result;
   }

   public static final int sind(int ang) {
      return sinusoid(ang, (short)0);
   }

   public static final int cosd(int ang) {
      return sinusoid(ang, (short)1);
   }

   public static final int tand(int ang) {
      return div(sinusoid(ang, (short)0), sinusoid(ang, (short)1));
   }

   private static final int atand(int arg) {
      int argSq = arg == 65536 ? arg : arg * arg >>> 16;
      int rad = ((((((((1365 * argSq >> 16) + -5579) * argSq >> 16) + 11806) * argSq >> 16) + -21646) * argSq >> 16) + 65527) * arg >>> 16;
      return mul(3754936, rad);
   }

   public static final int atand2(int y, int x) {
      int result = 0;
      short quadrant = 0;
      byte var5;
      if (x < 0) {
         if (x == Integer.MIN_VALUE) {
            x++;
         }

         x = -x;
         if (y < 0) {
            if (y == Integer.MIN_VALUE) {
               y++;
            }

            y = -y;
            var5 = 3;
         } else {
            var5 = 2;
         }
      } else if (y < 0) {
         if (y == Integer.MIN_VALUE) {
            y++;
         }

         y = -y;
         var5 = 4;
      } else {
         var5 = 1;
      }

      result = x > y ? atand(div(y, x)) : 5898240 - atand(div(x, y));
      switch (var5) {
         case 2:
         default:
            return 11796480 - result;
         case 3:
            return result - 11796480;
         case 4:
            result = -result;
         case 1:
            return result;
      }
   }
}
