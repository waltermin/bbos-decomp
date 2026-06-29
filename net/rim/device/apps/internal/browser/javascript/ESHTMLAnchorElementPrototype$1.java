package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLAnchorElement;

class ESHTMLAnchorElementPrototype$1 extends JavaScriptHostFunction {
   private final ESHTMLAnchorElementPrototype this$0;

   ESHTMLAnchorElementPrototype$1(ESHTMLAnchorElementPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      HTMLAnchorElement input = ((ESHTMLAnchorElement)this.getThis()).getAnchorElement();
      if (input != null) {
         input.blur();
      }

      return Value.DEFAULT;
   }
}
