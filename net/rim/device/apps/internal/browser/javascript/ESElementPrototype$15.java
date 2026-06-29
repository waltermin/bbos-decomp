package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;

class ESElementPrototype$15 extends JavaScriptHostFunction {
   private final ESElementPrototype this$0;

   ESElementPrototype$15(ESElementPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      String namespaceURI = Convert.toString(this.getParm(0));
      String localName = Convert.toString(this.getParm(1));
      return Value.makeBooleanValue(((ESElement)this.getThis()).getElement().hasAttributeNS(namespaceURI, localName));
   }
}
