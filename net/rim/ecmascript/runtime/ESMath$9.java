package net.rim.ecmascript.runtime;

class ESMath$9 extends HostFunction {
   private final ESMath this$0;

   ESMath$9(ESMath _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      double d = Convert.toDouble(this.getParm(0));
      if (Double.isNaN(d)) {
         return Value.NaN;
      } else if (d == 0L) {
         return Value.makeDoubleValue(d);
      } else if (d == 9218868437227405312L) {
         return Value.POSITIVE_INFINITY;
      } else {
         return d == -4503599627370496L ? Value.NEGATIVE_INFINITY : Value.makeDoubleValue(Math.floor(d));
      }
   }
}
