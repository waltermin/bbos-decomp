package net.rim.ecmascript.runtime;

class ESStringPrototype$5 extends HostFunction {
   private final ESStringPrototype this$0;

   ESStringPrototype$5(ESStringPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      return Value.makeStringValue(ESStringPrototype.toString(this.getThis()).toUpperCase());
   }
}
