package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.Node;

class ESNodePrototype$9 extends JavaScriptHostFunction {
   private final ESNodePrototype this$0;

   ESNodePrototype$9(ESNodePrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      Node node = ((ESNode)this.getThis()).getNode();
      return Value.makeBooleanValue(node.hasAttributes());
   }
}
