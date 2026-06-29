package net.rim.ecmascript.runtime;

class GlobalProperties$3 extends HostFunction {
   GlobalProperties$3(String x0, String x1, int x2) {
      super(x0, x1, x2);
   }

   @Override
   public long run() {
      long value = this.getParm(0);
      GlobalObject global = this.getGlobalInstance();
      if (Value.getType(value) != 2) {
         global.version = Convert.toInt32(value);
      }

      return Value.makeIntegerValue(global.version);
   }
}
