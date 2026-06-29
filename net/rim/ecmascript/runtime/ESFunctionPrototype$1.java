package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;

class ESFunctionPrototype$1 extends HostFunction {
   private final ESFunctionPrototype this$0;

   ESFunctionPrototype$1(ESFunctionPrototype _1, String x0, String x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   public long run() throws ThrownValue {
      try {
         return Value.makeStringValue(((ESFunction)this.getThis()).getSource());
      } finally {
         throw ThrownValue.typeError(Resources.getString(50), "toString");
      }
   }
}
