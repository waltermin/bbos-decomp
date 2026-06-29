package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLFormElement;

class ESHTMLFormElementPrototype$1 extends JavaScriptHostFunction {
   private final ESHTMLFormElementPrototype this$0;

   ESHTMLFormElementPrototype$1(ESHTMLFormElementPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      HTMLFormElement form = ((ESHTMLFormElement)this.getThis()).getFormElement();
      if (form != null) {
         form.submit();
      }

      return Value.NULL;
   }
}
