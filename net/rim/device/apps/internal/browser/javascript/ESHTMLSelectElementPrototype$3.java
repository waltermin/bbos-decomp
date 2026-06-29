package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLSelectElement;

class ESHTMLSelectElementPrototype$3 extends JavaScriptHostFunction {
   private final ESHTMLSelectElementPrototype this$0;

   ESHTMLSelectElementPrototype$3(ESHTMLSelectElementPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      HTMLElement element = ESHTMLElement.getHTMLElement(this.getParm(0));
      HTMLElement before = ESHTMLElement.getHTMLElement(this.getParm(1));
      HTMLSelectElement input = ((ESHTMLSelectElement)this.getThis()).getSelectElement();

      try {
         if (input != null) {
            input.add(element, before);
         }

         return Value.DEFAULT;
      } catch (Throwable var6) {
         throw ESDOMException.createThrownValue(e);
      }
   }
}
