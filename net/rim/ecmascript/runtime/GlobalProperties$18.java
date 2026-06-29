package net.rim.ecmascript.runtime;

class GlobalProperties$18 extends HostFunction {
   GlobalProperties$18(String x0, String x1, int x2) {
      super(x0, x1, x2);
   }

   @Override
   public long run() {
      return Value.makeObjectValue(new JavaBooleanArray(JavaArray.toIndex(this.getParm(0))));
   }
}
