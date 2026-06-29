package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

class ESHTMLDocumentPrototype$14 extends JavaScriptHostFunction {
   private final ESHTMLDocumentPrototype this$0;

   ESHTMLDocumentPrototype$14(ESHTMLDocumentPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      String tagName = Convert.toString(this.getParm(0));
      ESHTMLDocument htmlDoc = (ESHTMLDocument)this.getThis();
      NodeList nodeList = ((Document)htmlDoc.getNode()).getElementsByTagName(tagName);
      return htmlDoc.getEngine().lookupElementToESObjectLong(nodeList);
   }
}
