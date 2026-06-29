package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ThrownValue;
import org.w3c.dom.Attr;

class ESElementPrototype$5 extends JavaScriptHostFunction {
   private final ESElementPrototype this$0;

   ESElementPrototype$5(ESElementPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() throws ThrownValue {
      try {
         Attr attr = ESAttr.getAttr(this.getParm(0));
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(((ESElement)this.getThis()).getElement().setAttributeNode(attr));
      } catch (Throwable var3) {
         throw ESDOMException.createThrownValue(e);
      }
   }
}
