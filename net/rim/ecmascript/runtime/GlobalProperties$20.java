package net.rim.ecmascript.runtime;

class GlobalProperties$20 extends HostFunction {
   GlobalProperties$20(String x0, String x1, int x2) {
      super(x0, x1, x2);
   }

   @Override
   public long run() {
      return Value.makeObjectValue(new JavaCharArray(JavaArray.toIndex(this.getParm(0))));
   }
}
