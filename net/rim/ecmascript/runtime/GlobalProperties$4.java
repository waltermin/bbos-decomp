package net.rim.ecmascript.runtime;

class GlobalProperties$4 extends HostFunction {
   GlobalProperties$4(String x0, String x1, int x2) {
      super(x0, x1, x2);
   }

   @Override
   public long run() {
      this.getGlobalInstance().stop();
      return Value.UNDEFINED;
   }
}
