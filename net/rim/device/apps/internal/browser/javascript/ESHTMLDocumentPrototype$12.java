package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;

class ESHTMLDocumentPrototype$12 extends JavaScriptHostFunction {
   private final ESHTMLDocumentPrototype this$0;

   ESHTMLDocumentPrototype$12(ESHTMLDocumentPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() throws ThrownValue {
      String name = Convert.toString(this.getParm(0));
      ESHTMLDocument htmlDoc = (ESHTMLDocument)this.getThis();

      try {
         Attr attrib = ((Document)htmlDoc.getNode()).createAttribute(name);
         if (attrib != null) {
            return htmlDoc.getEngine().lookupElementToESObjectLong(attrib);
         }
      } catch (Throwable var5) {
         throw ESDOMException.createThrownValue(e);
      }

      return Value.DEFAULT;
   }
}
