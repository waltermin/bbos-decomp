package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;

class ESHTMLDocumentPrototype$3 extends JavaScriptHostFunction {
   private final ESHTMLDocumentPrototype this$0;

   ESHTMLDocumentPrototype$3(ESHTMLDocumentPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      JavaScriptEngine engine = ((ESHTMLDocument)this.getThis()).getEngine();
      engine.openPrintStream();
      return Value.NULL;
   }
}
