package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;

class ESNamedNodeMapPrototype$5 extends JavaScriptHostFunction {
   private final ESNamedNodeMapPrototype this$0;

   ESNamedNodeMapPrototype$5(ESNamedNodeMapPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      String namespaceURI = Convert.toString(this.getParm(0));
      String localName = Convert.toString(this.getParm(1));
      return JavaScriptEngine.getInstance()
         .lookupElementToESObjectLong(((ESNamedNodeMap)this.getThis()).getNamedNodeMap().getNamedItemNS(namespaceURI, localName));
   }
}
