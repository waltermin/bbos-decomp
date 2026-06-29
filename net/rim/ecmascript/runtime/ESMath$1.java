package net.rim.ecmascript.runtime;

class ESMath$1 extends HostFunction {
   private final ESMath this$0;

   ESMath$1(ESMath _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      long value = this.getParm(0);
      if (Value.getType(value) == 0) {
         int i = Value.getIntegerValue(value);
         if (i < 0) {
            int neg_i = -i;
            if (i != neg_i) {
               return Value.makeIntegerValue(neg_i);
            }
         }
      }

      double d = Convert.toDouble(value);
      if (Double.isNaN(d)) {
         return Value.NaN;
      }

      if (d == -4503599627370496L) {
         return Value.POSITIVE_INFINITY;
      }

      if (d == 0L) {
         return Value.ZERO;
      }

      if (d < 0L) {
         d = -d;
      }

      return Value.makeDoubleValue(d);
   }
}
