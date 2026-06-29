package net.rim.ecmascript.runtime;

class GlobalProperties$16 extends HostFunction {
   GlobalProperties$16(String x0, String x1, int x2) {
      super(x0, x1, x2);
   }

   @Override
   public long run() {
      return Value.makeStringValue(GlobalProperties.uriEncode(Convert.toString(this.getParm(0)), true));
   }
}
