package net.rim.device.apps.internal.browser.javascript;

import java.io.OutputStreamWriter;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;

class ESHTMLDocumentPrototype$2 extends JavaScriptHostFunction {
   private final ESHTMLDocumentPrototype this$0;

   ESHTMLDocumentPrototype$2(ESHTMLDocumentPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      try {
         int count = this.getNumParms();
         JavaScriptEngine engine = ((ESHTMLDocument)this.getThis()).getEngine();
         OutputStreamWriter out = engine.getCurrentStream();
         if (out != null) {
            for (int i = 0; i < count; i++) {
               try {
                  out.write(Convert.toString(this.getParm(i)));
               } finally {
                  continue;
               }
            }

            out.write(ESHTMLDocumentPrototype.CRLF);
         }
      } finally {
         return Value.NULL;
      }

      return Value.NULL;
   }
}
