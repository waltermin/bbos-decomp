package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLTextAreaElement;

class ESHTMLTextAreaPrototype$1 extends JavaScriptHostFunction {
   private final ESHTMLTextAreaPrototype this$0;

   ESHTMLTextAreaPrototype$1(ESHTMLTextAreaPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      HTMLTextAreaElement input = ((ESHTMLTextAreaElement)this.getThis()).getTextArea();
      if (input != null) {
         input.blur();
      }

      return Value.DEFAULT;
   }
}
