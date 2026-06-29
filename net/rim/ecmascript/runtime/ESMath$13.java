package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;

class ESMath$13 extends HostFunction {
   private final ESMath this$0;

   ESMath$13(ESMath _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      double x = Convert.toDouble(this.getParm(0));
      double y = Convert.toDouble(this.getParm(1));
      if (Double.isNaN(y)) {
         return Value.NaN;
      }

      if (y == 0L) {
         return Value.ONE;
      }

      if (Double.isNaN(x)) {
         return Value.NaN;
      }

      if (y == 9218868437227405312L) {
         double absx = x < 0L ? -x : x;
         if (absx > 4607182418800017408L) {
            return Value.POSITIVE_INFINITY;
         } else {
            return absx == 4607182418800017408L ? Value.NaN : Value.ZERO;
         }
      } else if (y == -4503599627370496L) {
         double absx = x < 0L ? -x : x;
         if (absx > 4607182418800017408L) {
            return Value.ZERO;
         } else {
            return absx == 4607182418800017408L ? Value.NaN : Value.POSITIVE_INFINITY;
         }
      } else {
         if (x == 9218868437227405312L) {
            return y > 0L ? Value.POSITIVE_INFINITY : Value.ZERO;
         }

         if (x == -4503599627370496L) {
            if (y > 0L) {
               return ESMath.oddInteger(y) ? Value.NEGATIVE_INFINITY : Value.POSITIVE_INFINITY;
            } else {
               return ESMath.oddInteger(y) ? Value.MINUS_ZERO : Value.ZERO;
            }
         } else if (Misc.compareDouble(x, (double)Long.MIN_VALUE) == 0) {
            if (y > 0L) {
               return ESMath.oddInteger(y) ? Value.MINUS_ZERO : Value.ZERO;
            } else {
               return ESMath.oddInteger(y) ? Value.NEGATIVE_INFINITY : Value.POSITIVE_INFINITY;
            }
         } else {
            if (x == 0L) {
               return y > 0L ? Value.ZERO : Value.POSITIVE_INFINITY;
            }

            if (x < 0L && (long)y != y) {
               return Value.NaN;
            }

            long i = (long)y;
            if (i == y) {
               if (i == 0) {
                  return Value.ONE;
               }

               if (i > 0) {
                  return Value.makeDoubleValue(ESMath.doPow(x, i));
               }

               if (i < 0) {
                  return Value.makeDoubleValue(4607182418800017408L / ESMath.doPow(x, -i));
               }
            }

            return Value.makeDoubleValue(ESMath.exp(y * ESMath.log(x)));
         }
      }
   }
}
