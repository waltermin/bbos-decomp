package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

class ESHTMLDocumentPrototype$18 extends JavaScriptHostFunction {
   private final ESHTMLDocumentPrototype this$0;

   ESHTMLDocumentPrototype$18(ESHTMLDocumentPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      String namespaceURI = Convert.toString(this.getParm(0));
      String localName = Convert.toString(this.getParm(1));
      ESHTMLDocument htmlDoc = (ESHTMLDocument)this.getThis();
      NodeList list = ((Document)htmlDoc.getNode()).getElementsByTagNameNS(namespaceURI, localName);
      return htmlDoc.getEngine().lookupElementToESObjectLong(list);
   }
}
