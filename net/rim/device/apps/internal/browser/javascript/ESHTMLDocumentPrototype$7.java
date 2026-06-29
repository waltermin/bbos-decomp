package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

class ESHTMLDocumentPrototype$7 extends JavaScriptHostFunction {
   private final ESHTMLDocumentPrototype this$0;

   ESHTMLDocumentPrototype$7(ESHTMLDocumentPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      ESHTMLDocument htmlDoc = (ESHTMLDocument)this.getThis();
      DocumentFragment fragment = ((Document)htmlDoc.getNode()).createDocumentFragment();
      return htmlDoc.getEngine().lookupElementToESObjectLong(fragment);
   }
}
