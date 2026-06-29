package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;

class ESElementPrototype$9 extends JavaScriptHostFunction {
   private final ESElementPrototype this$0;

   ESElementPrototype$9(ESElementPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() throws ThrownValue {
      try {
         String namespaceURI = Convert.toString(this.getParm(0));
         String localName = Convert.toString(this.getParm(1));
         String value = Convert.toString(this.getParm(2));
         ((ESElement)this.getThis()).getElement().setAttributeNS(namespaceURI, localName, value);
         return Value.DEFAULT;
      } catch (Throwable var5) {
         throw ESDOMException.createThrownValue(e);
      }
   }
}
