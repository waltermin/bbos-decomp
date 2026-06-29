package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;

class ESElementPrototype$3 extends JavaScriptHostFunction {
   private final ESElementPrototype this$0;

   ESElementPrototype$3(ESElementPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() throws ThrownValue {
      try {
         String name = Convert.toString(this.getParm(0));
         ((ESElement)this.getThis()).getElement().removeAttribute(name);
      } catch (Throwable var3) {
         throw ESDOMException.createThrownValue(e);
      }

      return Value.DEFAULT;
   }
}
