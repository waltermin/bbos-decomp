package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ThrownValue;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

class ESHTMLDocumentPrototype$15 extends JavaScriptHostFunction {
   private final ESHTMLDocumentPrototype this$0;

   ESHTMLDocumentPrototype$15(ESHTMLDocumentPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() throws ThrownValue {
      Node node = ESNode.getNode(this.getParm(0));
      boolean deep = Convert.toBoolean(this.getParm(1));
      ESHTMLDocument htmlDoc = (ESHTMLDocument)this.getThis();

      try {
         Node newNode = ((Document)htmlDoc.getNode()).importNode(node, deep);
         return htmlDoc.getEngine().lookupElementToESObjectLong(newNode);
      } catch (Throwable var6) {
         throw ESDOMException.createThrownValue(e);
      }
   }
}
