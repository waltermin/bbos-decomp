package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import org.w3c.dom.html2.HTMLTableSectionElement;

class ESHTMLTableSectionElementPrototype$1 extends JavaScriptHostFunction {
   private final ESHTMLTableSectionElementPrototype this$0;

   ESHTMLTableSectionElementPrototype$1(ESHTMLTableSectionElementPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      HTMLTableSectionElement table = ((ESHTMLTableSectionElement)this.getThis()).getTableSectionElement();
      int index = Convert.toInt32(this.getParm(0));

      try {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(table.insertRow(index));
      } catch (Throwable var5) {
         throw ESDOMException.createThrownValue(e);
      }
   }
}
