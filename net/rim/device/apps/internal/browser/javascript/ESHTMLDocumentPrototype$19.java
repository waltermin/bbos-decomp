package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import org.w3c.dom.NodeList;
import org.w3c.dom.html2.HTMLDocument;

class ESHTMLDocumentPrototype$19 extends JavaScriptHostFunction {
   private final ESHTMLDocumentPrototype this$0;

   ESHTMLDocumentPrototype$19(ESHTMLDocumentPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      String elementName = Convert.toString(this.getParm(1));
      ESHTMLDocument htmlDoc = (ESHTMLDocument)this.getThis();
      NodeList list = ((HTMLDocument)htmlDoc.getNode()).getElementsByName(elementName);
      return htmlDoc.getEngine().lookupElementToESObjectLong(list);
   }
}
