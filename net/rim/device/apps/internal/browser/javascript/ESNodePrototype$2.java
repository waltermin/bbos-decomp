package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ThrownValue;
import org.w3c.dom.Node;

class ESNodePrototype$2 extends JavaScriptHostFunction {
   private final ESNodePrototype this$0;

   ESNodePrototype$2(ESNodePrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() throws ThrownValue {
      Node node = ((ESNode)this.getThis()).getNode();
      Node newChild = ESNode.getNode(this.getParm(0));
      Node oldChild = ESNode.getNode(this.getParm(1));

      try {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(node.replaceChild(newChild, oldChild));
      } catch (Throwable var6) {
         throw ESDOMException.createThrownValue(e);
      }
   }
}
