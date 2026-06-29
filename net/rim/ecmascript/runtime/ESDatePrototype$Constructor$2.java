package net.rim.ecmascript.runtime;

class ESDatePrototype$Constructor$2 extends HostFunction {
   private final ESDatePrototype$Constructor this$0;

   ESDatePrototype$Constructor$2(ESDatePrototype$Constructor _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      try {
         return Value.makeDoubleValue(ESDatePrototype.parseDate(Convert.toString(this.getParm(0))));
      } catch (ESDatePrototype$CantParse cp) {
         return Value.NaN;
      }
   }
}
