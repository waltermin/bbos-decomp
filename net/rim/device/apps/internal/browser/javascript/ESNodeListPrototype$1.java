package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;

class ESNodeListPrototype$1 extends JavaScriptHostFunction {
   private final ESNodeListPrototype this$0;

   ESNodeListPrototype$1(ESNodeListPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      int index = Convert.toInt32(this.getParm(0));
      return ((ESNodeList)this.getThis()).requestItem(index);
   }
}
