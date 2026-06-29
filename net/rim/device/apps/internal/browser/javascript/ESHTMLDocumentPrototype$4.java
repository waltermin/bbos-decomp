package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;

class ESHTMLDocumentPrototype$4 extends JavaScriptHostFunction {
   private final ESHTMLDocumentPrototype this$0;

   ESHTMLDocumentPrototype$4(ESHTMLDocumentPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      JavaScriptEngine engine = ((ESHTMLDocument)this.getThis()).getEngine();
      engine.closePrintStream();
      return Value.NULL;
   }
}
