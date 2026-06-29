package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;

class ESNamedNodeMapPrototype$1 extends JavaScriptHostFunction {
   private final ESNamedNodeMapPrototype this$0;

   ESNamedNodeMapPrototype$1(ESNamedNodeMapPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      String str = Convert.toString(this.getParm(0));
      return JavaScriptEngine.getInstance().lookupElementToESObjectLong(((ESNamedNodeMap)this.getThis()).getNamedNodeMap().getNamedItem(str));
   }
}
