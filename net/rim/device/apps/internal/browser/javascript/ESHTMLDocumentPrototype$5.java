package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;

class ESHTMLDocumentPrototype$5 extends JavaScriptHostFunction {
   private final ESHTMLDocumentPrototype this$0;

   ESHTMLDocumentPrototype$5(ESHTMLDocumentPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      int count = this.getNumParms();
      if (count == 1) {
         long value = ((ESHTMLDocument)this.getThis()).getElementById(Convert.toString(this.getParm(0)));
         return value != Value.UNDEFINED && value != Value.DEFAULT ? value : Value.NULL;
      } else {
         return Value.UNDEFINED;
      }
   }
}
