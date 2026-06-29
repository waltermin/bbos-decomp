package net.rim.wica.runtime.script.internal.appenv;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.HostFunction;
import net.rim.ecmascript.runtime.Value;

class ESStringBundlePrototype$1 extends HostFunction {
   private final ESStringBundlePrototype this$0;

   ESStringBundlePrototype$1(ESStringBundlePrototype this$0, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public long run() {
      ESStringBundle thiz = (ESStringBundle)this.getThis();
      String key = Convert.toString(this.getParm(0));
      if (key != null) {
         String value = thiz.getString(key);
         return value == null ? Value.NULL : Value.makeStringValue(value);
      } else {
         return Value.DEFAULT;
      }
   }
}
