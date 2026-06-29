package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;

class ESHTMLDocumentPrototype$9 extends JavaScriptHostFunction {
   private final ESHTMLDocumentPrototype this$0;

   ESHTMLDocumentPrototype$9(ESHTMLDocumentPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      String data = Convert.toString(this.getParm(0));
      ESHTMLDocument htmlDoc = (ESHTMLDocument)this.getThis();
      Comment comment = ((Document)htmlDoc.getNode()).createComment(data);
      return htmlDoc.getEngine().lookupElementToESObjectLong(comment);
   }
}
