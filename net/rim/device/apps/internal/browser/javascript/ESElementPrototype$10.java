package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;

class ESElementPrototype$10 extends JavaScriptHostFunction {
   private final ESElementPrototype this$0;

   ESElementPrototype$10(ESElementPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      try {
         String namespaceURI = Convert.toString(this.getParm(0));
         String localName = Convert.toString(this.getParm(1));
         ((ESElement)this.getThis()).getElement().removeAttributeNS(namespaceURI, localName);
         return Value.DEFAULT;
      } catch (Throwable var4) {
         throw ESDOMException.createThrownValue(e);
      }
   }
}
