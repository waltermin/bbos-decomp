package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLInputElement;

class ESHTMLInputElementPrototype$3 extends JavaScriptHostFunction {
   private final ESHTMLInputElementPrototype this$0;

   ESHTMLInputElementPrototype$3(ESHTMLInputElementPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      HTMLInputElement input = ((ESHTMLInputElement)this.getThis()).getInputElement();
      if (input != null) {
         input.select();
      }

      return Value.NULL;
   }
}
