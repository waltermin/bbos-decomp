package net.rim.ecmascript.runtime;

class GlobalProperties$8 extends HostFunction {
   GlobalProperties$8(String x0, String x1, int x2) {
      super(x0, x1, x2);
   }

   @Override
   public long run() {
      double d = Convert.toDouble(this.getParm(0));
      if (Double.isNaN(d)) {
         return Value.FALSE;
      } else if (d == 9218868437227405312L) {
         return Value.FALSE;
      } else {
         return d == -4503599627370496L ? Value.FALSE : Value.TRUE;
      }
   }
}
