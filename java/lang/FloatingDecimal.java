package java.lang;

class FloatingDecimal {
   boolean isExceptional;
   boolean isNegative;
   int decExponent;
   char[] digits;
   int nDigits;
   int bigIntExp;
   int bigIntNBits;
   boolean mustSetRoundDir = false;
   int roundDir;
   static final long signMask = Long.MIN_VALUE;
   static final long expMask = 9218868437227405312L;
   static final long fractMask = 4503599627370495L;
   static final int expShift = 52;
   static final int expBias = 1023;
   static final long fractHOB = 4503599627370496L;
   static final long expOne = 4607182418800017408L;
   static final int maxSmallBinExp = 62;
   static final int minSmallBinExp = -21;
   static final int maxDecimalDigits = 15;
   static final int maxDecimalExponent = 308;
   static final int minDecimalExponent = -324;
   static final int bigDecimalExponent = 324;
   static final long highbyte = -72057594037927936L;
   static final long highbit = Long.MIN_VALUE;
   static final long lowbytes = 72057594037927935L;
   static final int singleSignMask = Integer.MIN_VALUE;
   static final int singleExpMask = 2139095040;
   static final int singleFractMask = 8388607;
   static final int singleExpShift = 23;
   static final int singleFractHOB = 8388608;
   static final int singleExpBias = 127;
   static final int singleMaxDecimalDigits = 7;
   static final int singleMaxDecimalExponent = 38;
   static final int singleMinDecimalExponent = -45;
   static final int intDecimalDigits = 9;
   private static FDBigInt[] b5p;
   private static final double[] small10pow = new double[]{
      (double)4607182418800017408L,
      (double)4621819117588971520L,
      (double)4636737291354636288L,
      (double)4652007308841189376L,
      (double)4666723172467343360L,
      (double)4681608360884174848L,
      (double)4696837146684686336L,
      (double)4711630319722168320L,
      (double)4726483295884279808L,
      (double)4741671816366391296L,
      (double)4756540486875873280L,
      (double)4771362005757984768L,
      (double)4786511204640096256L,
      (double)4801453603149578240L,
      (double)4816244402031689728L,
      (double)4831355200913801216L,
      (double)4846369599423283200L,
      (double)4861130398305394688L,
      (double)4876203697187506176L,
      (double)4891288408196988160L,
      (double)4906019910204099648L,
      (double)4921056587992461136L,
      (double)4936209963552724370L
   };
   private static final float[] singleSmall10pow = new float[]{
      (float)1065353216,
      (float)1092616192,
      (float)1120403456,
      (float)1148846080,
      (float)1176256512,
      (float)1203982336,
      (float)1232348160,
      (float)1259902592,
      (float)1287568416,
      (float)1315859240,
      (float)1343554297
   };
   private static final double[] big10pow = new double[]{
      (double)4846369599423283200L, (double)5085611494797045271L, (double)5564284217833028085L, (double)6521906365687930162L, (double)8436737289693151036L
   };
   private static final double[] tiny10pow = new double[]{
      (double)4367597403136100796L, (double)4128101167230658355L, (double)3649340653511681853L, (double)2691949749288605597L, (double)776877706278891331L
   };
   private static final int maxSmallTen = small10pow.length - 1;
   private static final int singleMaxSmallTen = singleSmall10pow.length - 1;
   private static final int[] small5pow = new int[]{
      1,
      5,
      25,
      125,
      625,
      3125,
      15625,
      78125,
      390625,
      1953125,
      9765625,
      48828125,
      244140625,
      1220703125,
      -804913144,
      3145776,
      3145776,
      3145776,
      3145776,
      51,
      4408146,
      4801362,
      5391186,
      5526098,
      -804913024,
      10682432,
      10813476,
      15270120,
      15466745,
      13041906,
      14155786,
      852216,
      15007941,
      6226836,
      59966374,
      61408155,
      61342624,
      60294051,
      2098078,
      15073478,
      13172959,
      2162720,
      2293794,
      2424996,
      2555942,
      2687016,
      2818090,
      2949164,
      3080238,
      3211312,
      3342386,
      3473460,
      3604534,
      3735608,
      3866682,
      3997756
   };
   private static final long[] long5pow = new long[]{
      1L,
      5L,
      25L,
      125L,
      625L,
      3125L,
      15625L,
      78125L,
      390625L,
      1953125L,
      9765625L,
      48828125L,
      244140625L,
      1220703125L,
      6103515625L,
      30517578125L,
      152587890625L,
      762939453125L,
      3814697265625L,
      19073486328125L,
      95367431640625L,
      476837158203125L,
      2384185791015625L,
      11920928955078125L,
      59604644775390625L,
      298023223876953125L,
      1490116119384765625L,
      7785283598L,
      107374182405L,
      2684354560125L,
      67108864003125L,
      1677721600078125L,
      41943040001953125L,
      1048576000048828125L,
      -3457075628379835499L,
      13511005043687472L,
      13511005043687472L,
      18932842905993267L,
      23154967561454418L,
      -3457075114198937006L,
      46443525786763328L,
      66429163965841640L,
      60798637932216562L,
      64458615776149752L,
      257553615195931540L,
      263464563992232859L,
      9011176454751139L,
      56577428111622342L,
      9851770215923744L,
      10977687302897828L,
      12103604389871656L,
      13229521476845612L,
      14355438563819568L,
      15481355650793524L,
      16607272737767480L,
      17733189824741436L,
      18859106911715489L,
      19985023998689348L,
      21110941085663304L,
      22236858172637260L,
      23362775259611216L,
      24488692346585172L,
      55169481988178008L,
      47007266017181910L,
      27866443607507135L,
      28992360694480996L,
      30118277781454952L,
      31244194868428908L,
      32370111955402864L,
      33496029042376820L,
      64176818683969656L,
      63051477130739958L,
      4055009087275139887L,
      69524336427688247L,
      72057589742960640L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      83435776L,
      1125899906853888L,
      288511902802247684L,
      218286321031985168L,
      -3457635109216518142L,
      1232789702068551L,
      16187396L,
      16777215L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L,
      0L
   };
   private static final int[] n5bits = new int[]{
      0,
      3,
      5,
      7,
      10,
      12,
      14,
      17,
      19,
      21,
      24,
      26,
      28,
      31,
      33,
      35,
      38,
      40,
      42,
      45,
      47,
      49,
      52,
      54,
      56,
      59,
      61,
      0,
      -804519909,
      1,
      0,
      5,
      0,
      25,
      0,
      125,
      0,
      625,
      0,
      3125,
      0,
      15625,
      0,
      78125,
      0,
      390625,
      0,
      1953125,
      0,
      9765625,
      0,
      48828125,
      0,
      244140625,
      0,
      1220703125,
      0,
      1808548329,
      1,
      452807053,
      7,
      -2030932031,
      35,
      -1564725563,
      177,
      766306777,
      888,
      -463433411,
      4440,
      1977800241,
      22204,
      1299066613,
      111022,
      -2094601527,
      555111,
      -1883073043,
      2775557,
      -825430623,
      13877787,
      167814181,
      69388939,
      839070905,
      346944695,
      -804650994,
      1,
      5,
      25,
      125,
      625,
      3125,
      15625,
      78125,
      390625,
      1953125,
      9765625,
      48828125,
      244140625,
      1220703125,
      -804913144,
      3145776,
      3145776,
      3145776,
      3145776,
      51,
      4408146,
      4801362,
      5391186,
      5526098
   };
   private static final char[] infinity = new char[]{'I', 'n', 'f', 'i', 'n', 'i', 't', 'y', 'ጲ', '퀄', '䑉', '̳', '\u0000', '\u0000', '瘇', '䕇'};
   private static final char[] notANumber = new char[]{'N', 'a', 'N', 'Ā', 'Ā', '浛'};
   private static final char[] zero = new char[]{'0', '0', '0', '0', '0', '0', '0', '0', '3', '\u0000', '䍒', 'C', '䍒', 'I', '䍒', 'R'};

   private FloatingDecimal(boolean negSign, int decExponent, char[] digits, int n, boolean e) {
      this.isNegative = negSign;
      this.isExceptional = e;
      this.decExponent = decExponent;
      this.digits = digits;
      this.nDigits = n;
   }

   private static int countBits(long v) {
      if (v == 0) {
         return 0;
      }

      while ((v & -72057594037927936L) == 0) {
         v <<= 8;
      }

      while (v > 0) {
         v <<= 1;
      }

      int n;
      for (n = 0; (v & 72057594037927935L) != 0; n += 8) {
         v <<= 8;
      }

      while (v != 0) {
         v <<= 1;
         n++;
      }

      return n;
   }

   private static synchronized FDBigInt big5pow(int p) {
      if (p < 0) {
         throw new RuntimeException("Assertion botch: negative power of 5");
      }

      if (b5p == null) {
         b5p = new FDBigInt[p + 1];
      } else if (b5p.length <= p) {
         FDBigInt[] t = new FDBigInt[p + 1];
         System.arraycopy(b5p, 0, t, 0, b5p.length);
         b5p = t;
      }

      if (b5p[p] != null) {
         return b5p[p];
      }

      if (p < small5pow.length) {
         return b5p[p] = new FDBigInt(small5pow[p]);
      }

      if (p < long5pow.length) {
         return b5p[p] = new FDBigInt(long5pow[p]);
      }

      int q = p >> 1;
      int r = p - q;
      FDBigInt bigq = b5p[q];
      if (bigq == null) {
         bigq = big5pow(q);
      }

      if (r < small5pow.length) {
         return b5p[p] = bigq.mult(small5pow[r]);
      }

      FDBigInt bigr = b5p[r];
      if (bigr == null) {
         bigr = big5pow(r);
      }

      return b5p[p] = bigq.mult(bigr);
   }

   private static FDBigInt multPow52(FDBigInt v, int p5, int p2) {
      if (p5 != 0) {
         if (p5 < small5pow.length) {
            v = v.mult(small5pow[p5]);
         } else {
            v = v.mult(big5pow(p5));
         }
      }

      if (p2 != 0) {
         v.lshiftMe(p2);
      }

      return v;
   }

   private static FDBigInt constructPow52(int p5, int p2) {
      FDBigInt v = new FDBigInt(big5pow(p5));
      if (p2 != 0) {
         v.lshiftMe(p2);
      }

      return v;
   }

   private FDBigInt doubleToBigInt(double dval) {
      long lbits = Double.doubleToLongBits(dval) & Long.MAX_VALUE;
      int binexp = (int)(lbits >>> 52);
      lbits &= 4503599627370495L;
      if (binexp > 0) {
         lbits |= 4503599627370496L;
      } else {
         if (lbits == 0) {
            throw new RuntimeException("Assertion botch: doubleToBigInt(0.0)");
         }

         binexp++;

         while ((lbits & 4503599627370496L) == 0) {
            lbits <<= 1;
            binexp--;
         }
      }

      binexp -= 1023;
      int nbits = countBits(lbits);
      int lowOrderZeros = 53 - nbits;
      lbits >>>= lowOrderZeros;
      this.bigIntExp = binexp + 1 - nbits;
      this.bigIntNBits = nbits;
      return new FDBigInt(lbits);
   }

   private static double ulp(double dval, boolean subtracting) {
      long lbits = Double.doubleToLongBits(dval) & Long.MAX_VALUE;
      int binexp = (int)(lbits >>> 52);
      if (subtracting && binexp >= 52 && (lbits & 4503599627370495L) == 0) {
         binexp--;
      }

      double ulpval;
      if (binexp > 52) {
         ulpval = Double.longBitsToDouble((long)(binexp - 52) << 52);
      } else if (binexp == 0) {
         ulpval = Double.MIN_VALUE;
      } else {
         ulpval = Double.longBitsToDouble((long)1 << binexp - 1);
      }

      if (subtracting) {
         ulpval = -ulpval;
      }

      return ulpval;
   }

   float stickyRound(double dval) {
      long lbits = Double.doubleToLongBits(dval);
      long binexp = lbits & 9218868437227405312L;
      if (binexp != 0 && binexp != 9218868437227405312L) {
         lbits += this.roundDir;
         return (float)Double.longBitsToDouble(lbits);
      } else {
         return (float)dval;
      }
   }

   private void developLongDigits(int decExponent, long lvalue, long insignificant) {
      int i;
      for (i = 0; insignificant >= 10; i++) {
         insignificant /= 10;
      }

      if (i != 0) {
         long pow10 = long5pow[i] << i;
         long residue = lvalue % pow10;
         lvalue /= pow10;
         decExponent += i;
         if (residue >= pow10 >> 1) {
            lvalue += 1;
         }
      }

      char[] digits;
      int ndigits;
      int digitno;
      if (lvalue > Integer.MAX_VALUE) {
         ndigits = 20;
         digits = new char[20];
         digitno = ndigits - 1;
         int c = (int)(lvalue % 10);

         for (lvalue /= 10; c == 0; lvalue /= 10) {
            decExponent++;
            c = (int)(lvalue % 10);
         }

         while (lvalue != 0) {
            digits[digitno--] = (char)(c + 48);
            decExponent++;
            c = (int)(lvalue % 10);
            lvalue /= 10;
         }

         digits[digitno] = (char)(c + 48);
      } else {
         if (lvalue <= 0) {
            throw new RuntimeException("Assertion botch: value " + lvalue + " <= 0");
         }

         int ivalue = (int)lvalue;
         ndigits = 10;
         digits = new char[10];
         digitno = ndigits - 1;
         int c = ivalue % 10;

         for (ivalue /= 10; c == 0; ivalue /= 10) {
            decExponent++;
            c = ivalue % 10;
         }

         while (ivalue != 0) {
            digits[digitno--] = (char)(c + 48);
            decExponent++;
            c = ivalue % 10;
            ivalue /= 10;
         }

         digits[digitno] = (char)(c + 48);
      }

      ndigits -= digitno;
      char[] result;
      if (digitno == 0) {
         result = digits;
      } else {
         result = new char[ndigits];
         System.arraycopy(digits, digitno, result, 0, ndigits);
      }

      this.digits = result;
      this.decExponent = decExponent + 1;
      this.nDigits = ndigits;
   }

   private void roundup() {
      int i;
      int q = this.digits[i = this.nDigits - 1];
      if (q == 57) {
         while (q == 57 && i > 0) {
            this.digits[i] = '0';
            q = this.digits[--i];
         }

         if (q == 57) {
            this.decExponent++;
            this.digits[0] = '1';
            return;
         }
      }

      this.digits[i] = (char)(q + 1);
   }

   public FloatingDecimal(double d) {
      long dBits = Double.doubleToLongBits(d);
      if ((dBits & Long.MIN_VALUE) != 0) {
         this.isNegative = true;
         dBits ^= Long.MIN_VALUE;
      } else {
         this.isNegative = false;
      }

      int binExp = (int)((dBits & 9218868437227405312L) >> 52);
      long fractBits = dBits & 4503599627370495L;
      if (binExp == 2047) {
         this.isExceptional = true;
         if (fractBits == 0) {
            this.digits = infinity;
         } else {
            this.digits = notANumber;
            this.isNegative = false;
         }

         this.nDigits = this.digits.length;
      } else {
         this.isExceptional = false;
         int nSignificantBits;
         if (binExp == 0) {
            if (fractBits == 0) {
               this.decExponent = 0;
               this.digits = zero;
               this.nDigits = 1;
               return;
            }

            while ((fractBits & 4503599627370496L) == 0) {
               fractBits <<= 1;
               binExp--;
            }

            nSignificantBits = 52 + binExp + 1;
            binExp++;
         } else {
            fractBits |= 4503599627370496L;
            nSignificantBits = 53;
         }

         binExp -= 1023;
         this.dtoa(binExp, fractBits, nSignificantBits);
      }
   }

   public FloatingDecimal(float f) {
      int fBits = Float.floatToIntBits(f);
      if ((fBits & -2147483648) != 0) {
         this.isNegative = true;
         fBits ^= Integer.MIN_VALUE;
      } else {
         this.isNegative = false;
      }

      int binExp = (fBits & 2139095040) >> 23;
      int fractBits = fBits & 8388607;
      if (binExp == 255) {
         this.isExceptional = true;
         if (fractBits == 0) {
            this.digits = infinity;
         } else {
            this.digits = notANumber;
            this.isNegative = false;
         }

         this.nDigits = this.digits.length;
      } else {
         this.isExceptional = false;
         int nSignificantBits;
         if (binExp == 0) {
            if (fractBits == 0) {
               this.decExponent = 0;
               this.digits = zero;
               this.nDigits = 1;
               return;
            }

            while ((fractBits & 8388608) == 0) {
               fractBits <<= 1;
               binExp--;
            }

            nSignificantBits = 23 + binExp + 1;
            binExp++;
         } else {
            fractBits |= 8388608;
            nSignificantBits = 24;
         }

         binExp -= 127;
         this.dtoa(binExp, (long)fractBits << 29, nSignificantBits);
      }
   }

   private void dtoa(int binExp, long fractBits, int nSignificantBits) {
      int nFractBits = countBits(fractBits);
      int nTinyBits = Math.max(0, nFractBits - binExp - 1);
      if (binExp <= 62 && binExp >= -21 && nTinyBits < long5pow.length && nFractBits + n5bits[nTinyBits] < 64 && nTinyBits == 0) {
         long halfULP;
         if (binExp > nSignificantBits) {
            halfULP = (long)1 << binExp - nSignificantBits - 1;
         } else {
            halfULP = 0;
         }

         if (binExp >= 52) {
            fractBits <<= binExp - 52;
         } else {
            fractBits >>>= 52 - binExp;
         }

         this.developLongDigits(0, fractBits, halfULP);
      } else {
         double d2 = Double.longBitsToDouble(4607182418800017408L | fractBits & -4503599627370497L);
         int decExp = (int)Math.floor((d2 - 4609434218613702656L) * 4598887322485374355L + 4595512376517860236L + binExp * 4599094494223104507L);
         int B5 = Math.max(0, -decExp);
         int B2 = B5 + nTinyBits + binExp;
         int S5 = Math.max(0, decExp);
         int S2 = S5 + nTinyBits;
         int M5 = B5;
         int M2 = B2 - nSignificantBits;
         fractBits >>>= 53 - nFractBits;
         B2 -= nFractBits - 1;
         int common2factor = Math.min(B2, S2);
         B2 -= common2factor;
         S2 -= common2factor;
         M2 -= common2factor;
         if (nFractBits == 1) {
            M2--;
         }

         if (M2 < 0) {
            B2 -= M2;
            S2 -= M2;
            M2 = 0;
         }

         char[] digits = this.digits = new char[18];
         int ndigit = 0;
         int Bbits = nFractBits + B2 + (B5 < n5bits.length ? n5bits[B5] : B5 * 3);
         int tenSbits = S2 + 1 + (S5 + 1 < n5bits.length ? n5bits[S5 + 1] : (S5 + 1) * 3);
         boolean low;
         boolean high;
         long lowDigitDifference;
         if (Bbits >= 64 || tenSbits >= 64) {
            FDBigInt Bval = multPow52(new FDBigInt(fractBits), B5, B2);
            FDBigInt Sval = constructPow52(S5, S2);
            FDBigInt Mval = constructPow52(M5, M2);
            int shiftBias;
            Bval.lshiftMe(shiftBias = Sval.normalizeMe());
            Mval.lshiftMe(shiftBias);
            FDBigInt tenSval = Sval.mult(10);
            ndigit = 0;
            int q = Bval.quoRemIteration(Sval);
            Mval = Mval.mult(10);
            low = Bval.cmp(Mval) < 0;
            high = Bval.add(Mval).cmp(tenSval) > 0;
            if (q >= 10) {
               throw new RuntimeException("Assertion botch: excessivly large digit " + q);
            }

            if (q == 0 && !high) {
               decExp--;
            } else {
               digits[ndigit++] = (char)(48 + q);
            }

            if (decExp <= -3 || decExp >= 8) {
               low = false;
               high = false;
            }

            while (!low && !high) {
               q = Bval.quoRemIteration(Sval);
               Mval = Mval.mult(10);
               if (q >= 10) {
                  throw new RuntimeException("Assertion botch: excessivly large digit " + q);
               }

               low = Bval.cmp(Mval) < 0;
               high = Bval.add(Mval).cmp(tenSval) > 0;
               digits[ndigit++] = (char)(48 + q);
            }

            if (high && low) {
               Bval.lshiftMe(1);
               lowDigitDifference = Bval.cmp(tenSval);
            } else {
               lowDigitDifference = 0;
            }
         } else if (Bbits < 32 && tenSbits < 32) {
            int b = (int)fractBits * small5pow[B5] << B2;
            int s = small5pow[S5] << S2;
            int m = small5pow[M5] << M2;
            int tens = s * 10;
            ndigit = 0;
            int q = b / s;
            b = 10 * (b % s);
            m *= 10;
            low = b < m;
            high = b + m > tens;
            if (q >= 10) {
               throw new RuntimeException("Assertion botch: excessivly large digit " + q);
            }

            if (q == 0 && !high) {
               decExp--;
            } else {
               digits[ndigit++] = (char)(48 + q);
            }

            if (decExp <= -3 || decExp >= 8) {
               low = false;
               high = false;
            }

            while (!low && !high) {
               q = b / s;
               b = 10 * (b % s);
               m *= 10;
               if (q >= 10) {
                  throw new RuntimeException("Assertion botch: excessivly large digit " + q);
               }

               if (m > 0) {
                  low = b < m;
                  high = b + m > tens;
               } else {
                  low = true;
                  high = true;
               }

               digits[ndigit++] = (char)(48 + q);
            }

            lowDigitDifference = (b << 1) - tens;
         } else {
            long b = fractBits * long5pow[B5] << B2;
            long s = long5pow[S5] << S2;
            long m = long5pow[M5] << M2;
            long tens = s * 10;
            ndigit = 0;
            int q = (int)(b / s);
            b = 10 * (b % s);
            m *= 10;
            low = b < m;
            high = b + m > tens;
            if (q >= 10) {
               throw new RuntimeException("Assertion botch: excessivly large digit " + q);
            }

            if (q == 0 && !high) {
               decExp--;
            } else {
               digits[ndigit++] = (char)(48 + q);
            }

            if (decExp <= -3 || decExp >= 8) {
               low = false;
               high = false;
            }

            while (!low && !high) {
               q = (int)(b / s);
               b = 10 * (b % s);
               m *= 10;
               if (q >= 10) {
                  throw new RuntimeException("Assertion botch: excessivly large digit " + q);
               }

               if (m > 0) {
                  low = b < m;
                  high = b + m > tens;
               } else {
                  low = true;
                  high = true;
               }

               digits[ndigit++] = (char)(48 + q);
            }

            lowDigitDifference = (b << 1) - tens;
         }

         this.decExponent = decExp + 1;
         this.digits = digits;
         this.nDigits = ndigit;
         if (high) {
            if (low) {
               if (lowDigitDifference == 0) {
                  if ((digits[this.nDigits - 1] & 1) != 0) {
                     this.roundup();
                     return;
                  }
               } else if (lowDigitDifference > 0) {
                  this.roundup();
                  return;
               }
            } else {
               this.roundup();
            }
         }
      }
   }

   @Override
   public String toString() {
      StringBuffer result = new StringBuffer(this.nDigits + 8);
      if (this.isNegative) {
         result.append('-');
      }

      if (this.isExceptional) {
         result.append(this.digits, 0, this.nDigits);
      } else {
         result.append("0.");
         result.append(this.digits, 0, this.nDigits);
         result.append('e');
         result.append(this.decExponent);
      }

      return new String(result);
   }

   public String toJavaFormatString() {
      char[] result = new char[this.nDigits + 10];
      int i = 0;
      if (this.isNegative) {
         result[0] = '-';
         i = 1;
      }

      if (this.isExceptional) {
         System.arraycopy(this.digits, 0, result, i, this.nDigits);
         i += this.nDigits;
      } else if (this.decExponent > 0 && this.decExponent < 8) {
         int charLength = Math.min(this.nDigits, this.decExponent);
         System.arraycopy(this.digits, 0, result, i, charLength);
         i += charLength;
         if (charLength < this.decExponent) {
            charLength = this.decExponent - charLength;
            System.arraycopy(zero, 0, result, i, charLength);
            i += charLength;
            result[i++] = '.';
            result[i++] = '0';
         } else {
            result[i++] = '.';
            if (charLength < this.nDigits) {
               int t = this.nDigits - charLength;
               System.arraycopy(this.digits, charLength, result, i, t);
               i += t;
            } else {
               result[i++] = '0';
            }
         }
      } else if (this.decExponent <= 0 && this.decExponent > -3) {
         result[i++] = '0';
         result[i++] = '.';
         if (this.decExponent != 0) {
            System.arraycopy(zero, 0, result, i, -this.decExponent);
            i -= this.decExponent;
         }

         System.arraycopy(this.digits, 0, result, i, this.nDigits);
         i += this.nDigits;
      } else {
         result[i++] = this.digits[0];
         result[i++] = '.';
         if (this.nDigits > 1) {
            System.arraycopy(this.digits, 1, result, i, this.nDigits - 1);
            i += this.nDigits - 1;
         } else {
            result[i++] = '0';
         }

         result[i++] = 'E';
         int e;
         if (this.decExponent <= 0) {
            result[i++] = '-';
            e = -this.decExponent + 1;
         } else {
            e = this.decExponent - 1;
         }

         if (e <= 9) {
            result[i++] = (char)(e + 48);
         } else if (e <= 99) {
            result[i++] = (char)(e / 10 + 48);
            result[i++] = (char)(e % 10 + 48);
         } else {
            result[i++] = (char)(e / 100 + 48);
            e %= 100;
            result[i++] = (char)(e / 10 + 48);
            result[i++] = (char)(e % 10 + 48);
         }
      }

      return new String(result, 0, i);
   }

   public static FloatingDecimal readJavaFormatString(String in) {
      boolean isNegative = false;
      boolean signSeen = false;

      try {
         in = in.trim();
         int l = in.length();
         if (l == 0) {
            throw new NumberFormatException("empty String");
         }

         int i = 0;
         char c;
         char[] digits;
         int nDigits;
         boolean decSeen;
         int decPt;
         int nLeadZero;
         int nTrailZero;
         switch (c = in.charAt(i)) {
            case '-':
               isNegative = true;
            case '+':
               i++;
               signSeen = true;
            default:
               digits = new char[l];
               nDigits = 0;
               decSeen = false;
               decPt = 0;
               nLeadZero = 0;
               nTrailZero = 0;
         }

         label92:
         for (; i < l; i++) {
            switch (c = in.charAt(i)) {
               case '-':
               case '/':
                  break label92;
               case '.':
                  if (decSeen) {
                     throw new NumberFormatException("multiple points");
                  }

                  decPt = i;
                  if (signSeen) {
                     decPt--;
                  }

                  decSeen = true;
                  continue;
               case '0':
               default:
                  if (nDigits > 0) {
                     nTrailZero++;
                  } else {
                     nLeadZero++;
                  }
                  continue;
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
               case '8':
               case '9':
            }

            while (nTrailZero > 0) {
               digits[nDigits++] = '0';
               nTrailZero--;
            }

            digits[nDigits++] = c;
         }

         if (nDigits == 0) {
            digits = zero;
            nDigits = 1;
            if (nLeadZero == 0) {
               throw new NumberFormatException(in);
            }
         }

         int decExp;
         if (decSeen) {
            decExp = decPt - nLeadZero;
         } else {
            decExp = nDigits + nTrailZero;
         }

         if (i < l && (c = in.charAt(i)) == 'e' || c == 'E') {
            int expSign = 1;
            int expVal = 0;
            int reallyBig = 214748364;
            boolean expOverflow = false;
            i++;
            int expAt;
            switch (in.charAt(i)) {
               case '-':
                  expSign = -1;
               case '+':
                  i++;
               default:
                  expAt = i;
            }

            label117:
            while (i < l) {
               if (expVal >= reallyBig) {
                  expOverflow = true;
               }

               switch (c = in.charAt(i++)) {
                  case '/':
                     i--;
                     break label117;
                  case '0':
                  case '1':
                  case '2':
                  case '3':
                  case '4':
                  case '5':
                  case '6':
                  case '7':
                  case '8':
                  case '9':
                  default:
                     expVal = expVal * 10 + (c - '0');
               }
            }

            int expLimit = 324 + nDigits + nTrailZero;
            if (!expOverflow && expVal <= expLimit) {
               decExp += expSign * expVal;
            } else {
               decExp = expSign * expLimit;
            }

            if (i == expAt) {
               throw new NumberFormatException(in);
            }
         }

         if (i >= l || i == l - 1 && (in.charAt(i) == 'f' || in.charAt(i) == 'F' || in.charAt(i) == 'd' || in.charAt(i) == 'D')) {
            return new FloatingDecimal(isNegative, decExp, digits, nDigits, false);
         }
      } catch (StringIndexOutOfBoundsException var19) {
      }

      throw new NumberFormatException(in);
   }

   public double doubleValue() {
      int kDigits = Math.min(this.nDigits, 16);
      this.roundDir = 0;
      int iValue = this.digits[0] - '0';
      int iDigits = Math.min(kDigits, 9);

      for (int i = 1; i < iDigits; i++) {
         iValue = iValue * 10 + this.digits[i] - 48;
      }

      long lValue = iValue;

      for (int i = iDigits; i < kDigits; i++) {
         lValue = lValue * 10 + (this.digits[i] - '0');
      }

      double dValue = lValue;
      int exp = this.decExponent - kDigits;
      if (this.nDigits <= 15) {
         if (exp == 0 || dValue == 0L) {
            return this.isNegative ? -dValue : dValue;
         }

         if (exp < 0) {
            if (exp >= -maxSmallTen) {
               double rValue = dValue / small10pow[-exp];
               double tValue = rValue * small10pow[-exp];
               if (this.mustSetRoundDir) {
                  this.roundDir = tValue == dValue ? 0 : (tValue < dValue ? 1 : -1);
               }

               if (this.isNegative) {
                  return -rValue;
               }

               return rValue;
            }
         } else {
            if (exp <= maxSmallTen) {
               double rValue = dValue * small10pow[exp];
               if (this.mustSetRoundDir) {
                  double tValue = rValue / small10pow[exp];
                  this.roundDir = tValue == dValue ? 0 : (tValue < dValue ? 1 : -1);
               }

               if (this.isNegative) {
                  return -rValue;
               }

               return rValue;
            }

            int slop = 15 - kDigits;
            if (exp <= maxSmallTen + slop) {
               dValue *= small10pow[slop];
               double rValue = dValue * small10pow[exp - slop];
               if (this.mustSetRoundDir) {
                  double tValue = rValue / small10pow[exp - slop];
                  this.roundDir = tValue == dValue ? 0 : (tValue < dValue ? 1 : -1);
               }

               if (this.isNegative) {
                  return -rValue;
               }

               return rValue;
            }
         }
      }

      if (exp <= 0) {
         if (exp < 0) {
            exp = -exp;
            if (this.decExponent < -325) {
               if (this.isNegative) {
                  return (double)Long.MIN_VALUE;
               }

               return (double)0L;
            }

            if ((exp & 15) != 0) {
               dValue /= small10pow[exp & 15];
            }

            if ((exp = exp >> 4) != 0) {
               int j = 0;

               while (exp > 1) {
                  if ((exp & 1) != 0) {
                     dValue *= tiny10pow[j];
                  }

                  j++;
                  exp >>= 1;
               }

               double t = dValue * tiny10pow[j];
               if (t == 0L) {
                  t = dValue * 4611686018427387904L;
                  t *= tiny10pow[j];
                  if (t == 0L) {
                     if (this.isNegative) {
                        return (double)Long.MIN_VALUE;
                     }

                     return (double)0L;
                  }

                  t = Double.MIN_VALUE;
               }

               dValue = t;
            }
         }
      } else {
         if (this.decExponent > 309) {
            if (this.isNegative) {
               return (double)-4503599627370496L;
            }

            return (double)9218868437227405312L;
         }

         if ((exp & 15) != 0) {
            dValue *= small10pow[exp & 15];
         }

         if ((exp = exp >> 4) != 0) {
            int j = 0;

            while (exp > 1) {
               if ((exp & 1) != 0) {
                  dValue *= big10pow[j];
               }

               j++;
               exp >>= 1;
            }

            double t = dValue * big10pow[j];
            if (Double.isInfinite(t)) {
               t = dValue / 4611686018427387904L;
               t *= big10pow[j];
               if (Double.isInfinite(t)) {
                  if (this.isNegative) {
                     return (double)-4503599627370496L;
                  }

                  return (double)9218868437227405312L;
               }

               t = (double)9218868437227405311L;
            }

            dValue = t;
         }
      }

      FDBigInt bigD0 = new FDBigInt(lValue, this.digits, kDigits, this.nDigits);
      exp = this.decExponent - this.nDigits;

      do {
         FDBigInt bigB = this.doubleToBigInt(dValue);
         int B2;
         int B5;
         int D2;
         int D5;
         if (exp >= 0) {
            B5 = 0;
            B2 = 0;
            D5 = exp;
            D2 = exp;
         } else {
            B2 = B5 = -exp;
            D5 = 0;
            D2 = 0;
         }

         if (this.bigIntExp >= 0) {
            B2 += this.bigIntExp;
         } else {
            D2 -= this.bigIntExp;
         }

         int Ulp2 = B2;
         int hulpbias;
         if (this.bigIntExp + this.bigIntNBits <= -1022) {
            hulpbias = this.bigIntExp + 1023 + 52;
         } else {
            hulpbias = 54 - this.bigIntNBits;
         }

         B2 += hulpbias;
         D2 += hulpbias;
         int common2 = Math.min(B2, Math.min(D2, Ulp2));
         B2 -= common2;
         D2 -= common2;
         Ulp2 -= common2;
         bigB = multPow52(bigB, B5, B2);
         FDBigInt bigD = multPow52(new FDBigInt(bigD0), D5, D2);
         FDBigInt diff;
         int cmpResult;
         boolean overvalue;
         if ((cmpResult = bigB.cmp(bigD)) > 0) {
            overvalue = true;
            diff = bigB.sub(bigD);
            if (this.bigIntNBits == 1 && this.bigIntExp > -1023) {
               if (--Ulp2 < 0) {
                  Ulp2 = 0;
                  diff.lshiftMe(1);
               }
            }
         } else {
            if (cmpResult >= 0) {
               break;
            }

            overvalue = false;
            diff = bigD.sub(bigB);
         }

         FDBigInt halfUlp = constructPow52(B5, Ulp2);
         if ((cmpResult = diff.cmp(halfUlp)) < 0) {
            this.roundDir = overvalue ? -1 : 1;
            break;
         }

         if (cmpResult == 0) {
            dValue += 4602678819172646912L * ulp(dValue, overvalue);
            this.roundDir = overvalue ? -1 : 1;
            break;
         }

         dValue += ulp(dValue, overvalue);
      } while (dValue != 0L && dValue != 9218868437227405312L);

      return this.isNegative ? -dValue : dValue;
   }

   public float floatValue() {
      int kDigits = Math.min(this.nDigits, 8);
      int iValue = this.digits[0] - '0';

      for (int i = 1; i < kDigits; i++) {
         iValue = iValue * 10 + this.digits[i] - 48;
      }

      float fValue = iValue;
      int exp = this.decExponent - kDigits;
      if (this.nDigits > 7) {
         if (this.decExponent >= this.nDigits && this.nDigits + this.decExponent <= 15) {
            long lValue = iValue;

            for (int i = kDigits; i < this.nDigits; i++) {
               lValue = lValue * 10 + (this.digits[i] - '0');
            }

            double dValue = lValue;
            exp = this.decExponent - this.nDigits;
            dValue *= small10pow[exp];
            fValue = (float)dValue;
            if (this.isNegative) {
               return -fValue;
            }

            return fValue;
         }
      } else {
         if (exp == 0 || fValue == false) {
            if (this.isNegative) {
               return -fValue;
            }

            return fValue;
         }

         if (exp >= 0) {
            if (exp <= singleMaxSmallTen) {
               fValue *= singleSmall10pow[exp];
               if (this.isNegative) {
                  return -fValue;
               }

               return fValue;
            }

            int slop = 7 - kDigits;
            if (exp <= singleMaxSmallTen + slop) {
               fValue *= singleSmall10pow[slop];
               fValue *= singleSmall10pow[exp - slop];
               if (this.isNegative) {
                  return -fValue;
               }

               return fValue;
            }
         } else if (exp >= -singleMaxSmallTen) {
            fValue /= singleSmall10pow[-exp];
            if (this.isNegative) {
               return -fValue;
            }

            return fValue;
         }
      }

      if (this.decExponent > 39) {
         return (float)(this.isNegative ? -8388608 : 2139095040);
      }

      if (this.decExponent < -46) {
         return (float)(this.isNegative ? Integer.MIN_VALUE : 0);
      }

      this.mustSetRoundDir = true;
      double dValue = this.doubleValue();
      return this.stickyRound(dValue);
   }
}
