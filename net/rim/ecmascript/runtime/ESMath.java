package net.rim.ecmascript.runtime;

class ESMath extends ESObject {
   private static final double pi;
   private static final double pibysix;
   private static final double pibyfour;
   private static final double pibytwo;
   private static final double tan15;
   private static final double sqrt3;
   private static final double[] atanPoly = new double[]{
      (double)4586558024442191043L,
      (double)-4634035982931263455L,
      (double)4590197988978956453L,
      (double)-4632157313457631885L,
      (double)4592670810479699296L,
      (double)-4629057045627423859L,
      (double)4596373779693870454L,
      (double)-4623695617433709840L,
      (double)4607182418800017408L
   };

   static long floorDivide(long a, long b) {
      long q = a / b;
      long rem = a % b;
      return rem != 0 && a < 0 ? q - 1 : q;
   }

   static int positiveMod(long t, long modulus) {
      t %= modulus;
      if (t < 0) {
         t += modulus;
      }

      return (int)t;
   }

   private static ESMath$ScaledDouble scale(double x) {
      ESMath$ScaledDouble sd = new ESMath$ScaledDouble();
      long l = Double.doubleToLongBits(x);
      sd.exp = 0;
      sd.fraction = x;
      if (x != 0L) {
         sd.exp = (int)(l >>> 52) & 2047;
         sd.exp -= 1022;
         l &= -9218868437227405313L;
         l |= 4602678819172646912L;
         sd.longFraction = l;
         sd.fraction = Double.longBitsToDouble(l);
      }

      return sd;
   }

   private static double unScale(double x, int n) {
      if (x == 0L) {
         return (double)0L;
      }

      ESMath$ScaledDouble sd = scale(x);
      long l = sd.longFraction;
      int exp = sd.exp + n;
      exp += 1022;
      if (exp <= 0) {
         return (double)0L;
      }

      if (exp >= 2047) {
         return (double)(l >= 0 ? 9218868437227405312L : -4503599627370496L);
      }

      l &= -9218868437227405313L;
      l |= (long)exp << 52;
      return Double.longBitsToDouble(l);
   }

   private static double evalPoly(double x, double[] poly) {
      double result = poly[0];

      for (int i = 1; i < poly.length; i++) {
         result = result * x + poly[i];
      }

      return result;
   }

   private static double oddPoly(double x, double[] poly) {
      return x * evalPoly(x * x, poly);
   }

   private static native double log(double var0);

   private static native double exp(double var0);

   private double asin(double x) {
      double z = 4607182418800017408L - x * x;
      if (z == 0L) {
         return (double)(x < 0L ? -4613618979930100456L : 4609753056924675352L);
      } else {
         return atan(x / Math.sqrt(z));
      }
   }

   private double acos(double x) {
      double z = 4607182418800017408L - x * x;
      if (z == 0L) {
         return (double)(x < 0L ? 4614256656552045848L : 0L);
      } else {
         return 4609753056924675352L - atan(x / Math.sqrt(z));
      }
   }

   private static double atan(double x) {
      boolean neg = false;
      if (x < 0L) {
         neg = true;
         x = -x;
      }

      if (x == 4607182418800017408L) {
         x = (double)4605249457297304856L;
      } else if (x > 4457293557087583675L) {
         boolean inverted = false;
         if (x > 4607182418800017408L) {
            x = 4607182418800017408L / x;
            inverted = true;
         }

         boolean reduced = false;
         if (x > 4598498563450654038L) {
            reduced = true;
            x = (x * 4610479282544200874L - 4607182418800017408L) / (x + 4610479282544200874L);
         }

         x = oddPoly(x, atanPoly);
         if (reduced) {
            x += 4602891378046628709L;
         }

         if (inverted) {
            x = 4609753056924675352L - x;
         }
      }

      if (neg) {
         x = -x;
      }

      return x;
   }

   private static boolean oddInteger(double d) {
      long l = (long)d;
      return d != l ? false : (l & 1) != 0;
   }

   private static double doPow(double v, long e) {
      double p = (double)4607182418800017408L;

      do {
         if ((e & 1) != 0) {
            p *= v;
         }

         v *= v;
         e >>>= 1;
      } while (e != 0);

      return p;
   }

   ESMath() {
      super("Math", GlobalObject.getInstance().objectPrototype);
      this.setGrowthIncrement(30);
      this.addHostFunction(new ESMath$1(this, "Math", "abs", 1));
      this.addHostFunction(new ESMath$2(this, "Math", "acos", 1));
      this.addHostFunction(new ESMath$3(this, "Math", "asin", 1));
      this.addHostFunction(new ESMath$4(this, "Math", "atan", 1));
      this.addHostFunction(new ESMath$5(this, "Math", "atan2", 2));
      this.addHostFunction(new ESMath$6(this, "Math", "ceil", 1));
      this.addHostFunction(new ESMath$7(this, "Math", "cos", 1));
      this.addHostFunction(new ESMath$8(this, "Math", "exp", 1));
      this.addHostFunction(new ESMath$9(this, "Math", "floor", 1));
      this.addHostFunction(new ESMath$10(this, "Math", "log", 1));
      this.addHostFunction(new ESMath$11(this, "Math", "max", 2));
      this.addHostFunction(new ESMath$12(this, "Math", "min", 2));
      this.addHostFunction(new ESMath$13(this, "Math", "pow", 2));
      this.addHostFunction(new ESMath$14(this, "Math", "random", 0));
      this.addHostFunction(new ESMath$15(this, "Math", "round", 1));
      this.addHostFunction(new ESMath$16(this, "Math", "sin", 1));
      this.addHostFunction(new ESMath$17(this, "Math", "sqrt", 1));
      this.addHostFunction(new ESMath$18(this, "Math", "tan", 1));
      this.addField("E", 7, Value.makeDoubleValue((double)4613303445314885481L));
      this.addField("LN10", 7, Value.makeDoubleValue((double)4612367379483415830L));
      this.addField("LN2", 7, Value.makeDoubleValue((double)4604418534313441775L));
      this.addField("LOG2E", 7, Value.makeDoubleValue((double)4609176140021203710L));
      this.addField("LOG10E", 7, Value.makeDoubleValue((double)4601495173785380110L));
      this.addField("PI", 7, Value.makeDoubleValue((double)4614256656552045848L));
      this.addField("SQRT2", 7, Value.makeDoubleValue((double)4609047870845172685L));
      this.addField("SQRT1_2", 7, Value.makeDoubleValue((double)4604544271217802189L));
   }
}
