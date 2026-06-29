package net.rim.ecmascript.runtime;

class ESObjectPrototype$4 extends HostFunction {
   private final ESObjectPrototype this$0;

   ESObjectPrototype$4(ESObjectPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      boolean rc = false;
      long value = this.getParm(0);
      if (this.getThis().hasOwnElement(value) && (this.getThis().getAttributesElement(value) & 2) == 0) {
         rc = true;
      }

      return Value.makeBooleanValue(rc);
   }
}
