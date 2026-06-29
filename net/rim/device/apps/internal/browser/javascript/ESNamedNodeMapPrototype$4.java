package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;

class ESNamedNodeMapPrototype$4 extends JavaScriptHostFunction {
   private final ESNamedNodeMapPrototype this$0;

   ESNamedNodeMapPrototype$4(ESNamedNodeMapPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      int index = Convert.toInt32(this.getParm(0));
      return JavaScriptEngine.getInstance().lookupElementToESObjectLong(((ESNamedNodeMap)this.getThis()).getNamedNodeMap().item(index));
   }
}
