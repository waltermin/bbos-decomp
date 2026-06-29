package net.rim.ecmascript.runtime;

class ESObjectPrototype$3 extends HostFunction {
   private final ESObjectPrototype this$0;

   ESObjectPrototype$3(ESObjectPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      boolean rc = false;
      ESObject v = Value.checkIfObjectValue(this.getParm(0));
      if (v != null && this.getThis() == v.getPrototype()) {
         rc = true;
      }

      return Value.makeBooleanValue(rc);
   }
}
