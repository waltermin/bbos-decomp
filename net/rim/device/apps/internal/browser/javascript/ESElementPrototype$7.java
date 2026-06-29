package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;

class ESElementPrototype$7 extends JavaScriptHostFunction {
   private final ESElementPrototype this$0;

   ESElementPrototype$7(ESElementPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      String tagName = Convert.toString(this.getParm(0));
      return JavaScriptEngine.getInstance().lookupElementToESObjectLong(((ESElement)this.getThis()).getElement().getElementsByTagName(tagName));
   }
}
