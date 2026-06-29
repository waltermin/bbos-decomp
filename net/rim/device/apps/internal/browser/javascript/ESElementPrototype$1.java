package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;

class ESElementPrototype$1 extends JavaScriptHostFunction {
   private final ESElementPrototype this$0;

   ESElementPrototype$1(ESElementPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      String name = Convert.toString(this.getParm(0));
      return JavaScriptEngine.makeStringValue(((ESElement)this.getThis()).getElement().getAttribute(name));
   }
}
