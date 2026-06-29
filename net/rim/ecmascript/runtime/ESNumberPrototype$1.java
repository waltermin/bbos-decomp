package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class ESNumberPrototype$1 extends HostFunction {
   private final ESNumberPrototype this$0;

   ESNumberPrototype$1(ESNumberPrototype _1, String x0, String x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      try {
         return Value.makeStringValue(Convert.toString(Value.makeDoubleValue(((ESNumber)this.getThis()).getValue())));
      } finally {
         throw ThrownValue.typeError(Resources.getString(50), "toString");
      }
   }
}
