package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class ESBooleanPrototype$2 extends HostFunction {
   private final ESBooleanPrototype this$0;

   ESBooleanPrototype$2(ESBooleanPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() throws ThrownValue {
      try {
         return Value.makeBooleanValue(((ESBoolean)this.getThis()).getValue());
      } finally {
         throw ThrownValue.typeError(Resources.getString(50), "valueOf");
      }
   }
}
