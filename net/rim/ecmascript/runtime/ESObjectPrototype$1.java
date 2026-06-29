package net.rim.ecmascript.runtime;

class ESObjectPrototype$1 extends HostFunction {
   private final ESObjectPrototype this$0;

   ESObjectPrototype$1(ESObjectPrototype _1, String x0, String x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      return Value.makeObjectValue(this.getThis());
   }
}
