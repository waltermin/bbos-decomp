package net.rim.ecmascript.runtime;

class ESObjectPrototype$2 extends HostFunction {
   private final ESObjectPrototype this$0;

   ESObjectPrototype$2(ESObjectPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      return Value.makeBooleanValue(this.getThis().hasOwnField(Convert.toInternString(this.getParm(0))));
   }
}
