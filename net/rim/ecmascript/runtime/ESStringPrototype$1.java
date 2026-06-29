package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class ESStringPrototype$1 extends HostFunction {
   private final ESStringPrototype this$0;

   ESStringPrototype$1(ESStringPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      try {
         return Value.makeStringValue(((ESString)this.getThis()).getValue());
      } finally {
         throw ThrownValue.typeError(Resources.getString(50), "toString");
      }
   }
}
