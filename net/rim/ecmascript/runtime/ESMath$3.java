package net.rim.ecmascript.runtime;

class ESMath$3 extends HostFunction {
   private final ESMath this$0;

   ESMath$3(ESMath _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      double d = Convert.toDouble(this.getParm(0));
      if (Double.isNaN(d) || d > 4607182418800017408L || d < -4616189618054758400L) {
         return Value.NaN;
      } else {
         return d == 0L ? Value.makeDoubleValue(d) : Value.makeDoubleValue(this.this$0.asin(d));
      }
   }
}
