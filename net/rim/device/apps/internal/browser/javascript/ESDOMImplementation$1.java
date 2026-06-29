package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.DOMImplementation;

class ESDOMImplementation$1 extends JavaScriptHostFunction {
   private final ESDOMImplementation this$0;

   ESDOMImplementation$1(ESDOMImplementation _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      DOMImplementation impl = this.this$0._doc.getImplementation();
      String feature = Convert.toString(this.getParm(0));
      String version = Convert.toString(this.getParm(1));
      return Value.makeBooleanValue(impl.hasFeature(feature, version));
   }
}
