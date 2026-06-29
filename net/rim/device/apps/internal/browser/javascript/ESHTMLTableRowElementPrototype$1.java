package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ThrownValue;
import org.w3c.dom.html2.HTMLTableRowElement;

class ESHTMLTableRowElementPrototype$1 extends JavaScriptHostFunction {
   private final ESHTMLTableRowElementPrototype this$0;

   ESHTMLTableRowElementPrototype$1(ESHTMLTableRowElementPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() throws ThrownValue {
      HTMLTableRowElement table = ((ESHTMLTableRowElement)this.getThis()).getTableRowElement();
      int index = Convert.toInt32(this.getParm(0));

      try {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(table.insertCell(index));
      } catch (Throwable var5) {
         throw ESDOMException.createThrownValue(e);
      }
   }
}
