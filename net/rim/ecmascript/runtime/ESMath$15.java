package net.rim.ecmascript.runtime;

class ESMath$15 extends HostFunction {
   private final ESMath this$0;

   ESMath$15(ESMath _1, String x0, String x1, int x2) {
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
      } else if (d == -4503599627370496L) {
         return Value.NEGATIVE_INFINITY;
      } else if (d < 0L && d >= -4620693217682128896L) {
         return Value.MINUS_ZERO;
      } else {
         return d > 0L && d < 4602678819172646912L ? Value.ZERO : Value.makeDoubleValue(Math.floor(d + 4602678819172646912L));
      }
   }
}
