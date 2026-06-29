package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;

class ESEnumPrototype$1 extends HostFunction {
   private final ESEnumPrototype this$0;

   ESEnumPrototype$1(ESEnumPrototype this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      ESEnum thiz = (ESEnum)this.getThis();
      String v = thiz.getStringValue();
      return v == null ? Value.NULL : Value.makeStringValue(v);
   }
}
