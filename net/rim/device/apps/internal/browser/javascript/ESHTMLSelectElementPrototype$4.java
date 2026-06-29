package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLSelectElement;

class ESHTMLSelectElementPrototype$4 extends JavaScriptHostFunction {
   private final ESHTMLSelectElementPrototype this$0;

   ESHTMLSelectElementPrototype$4(ESHTMLSelectElementPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      int index = Convert.toInt32(this.getParm(0));
      HTMLSelectElement input = ((ESHTMLSelectElement)this.getThis()).getSelectElement();
      if (input != null) {
         input.remove(index);
      }

      return Value.DEFAULT;
   }
}
