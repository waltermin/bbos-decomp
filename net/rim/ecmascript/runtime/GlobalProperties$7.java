package net.rim.ecmascript.runtime;

class GlobalProperties$7 extends HostFunction {
   GlobalProperties$7(String x0, String x1, int x2) {
      super(x0, x1, x2);
   }

   @Override
   public long run() {
      return Value.makeBooleanValue(Double.isNaN(Convert.toDouble(this.getParm(0))));
   }
}
