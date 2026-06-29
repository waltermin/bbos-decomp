package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.Node;

class ESNodePrototype$3 extends JavaScriptHostFunction {
   private final ESNodePrototype this$0;

   ESNodePrototype$3(ESNodePrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public long run() {
      Node node = ((ESNode)this.getThis()).getNode();
      Node oldChild = ESNode.getNode(this.getParm(0));

      try {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(node.removeChild(oldChild));
      } catch (Throwable var5) {
         throw ESDOMException.createThrownValue(e);
      }
   }
}
