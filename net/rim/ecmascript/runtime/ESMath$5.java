package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;

class ESMath$5 extends HostFunction {
   private final ESMath this$0;

   ESMath$5(ESMath _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      double y = Convert.toDouble(this.getParm(0));
      double x = Convert.toDouble(this.getParm(1));
      if (Double.isNaN(x) || Double.isNaN(y)) {
         return Value.NaN;
      }

      if (y > 0L && x == 0L) {
         return Value.makeDoubleValue((double)4609753056924675352L);
      }

      if (Misc.compareDouble(y, (double)0L) == 0) {
         return Misc.compareDouble(x, (double)0L) >= 0 ? Value.ZERO : Value.makeDoubleValue((double)4614256656552045848L);
      }

      if (Misc.compareDouble(y, (double)Long.MIN_VALUE) == 0) {
         return Misc.compareDouble(x, (double)0L) >= 0 ? Value.MINUS_ZERO : Value.makeDoubleValue((double)-4609115380302729960L);
      }

      if (y < 0L && x == 0L) {
         return Value.makeDoubleValue((double)-4613618979930100456L);
      }

      if (y == 9218868437227405312L) {
         if (x == 9218868437227405312L) {
            return Value.makeDoubleValue((double)4605249457297304856L);
         } else {
            return x == -4503599627370496L ? Value.makeDoubleValue((double)4612488097114038738L) : Value.makeDoubleValue((double)4609753056924675352L);
         }
      } else if (y == -4503599627370496L) {
         if (x == 9218868437227405312L) {
            return Value.makeDoubleValue((double)-4618122579557470952L);
         } else {
            return x == -4503599627370496L ? Value.makeDoubleValue((double)-4610883939740737070L) : Value.makeDoubleValue((double)-4613618979930100456L);
         }
      } else if (x == 9218868437227405312L) {
         return y > 0L ? Value.ZERO : Value.MINUS_ZERO;
      } else if (x != -4503599627370496L) {
         return Value.makeDoubleValue(ESMath.atan(x / y));
      } else {
         return y > 0L ? Value.makeDoubleValue((double)4614256656552045848L) : Value.makeDoubleValue((double)-4609115380302729960L);
      }
   }
}
