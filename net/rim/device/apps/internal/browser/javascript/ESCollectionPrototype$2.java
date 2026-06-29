package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;

class ESCollectionPrototype$2 extends JavaScriptHostFunction {
   private final ESCollectionPrototype this$0;

   ESCollectionPrototype$2(ESCollectionPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      String str = Convert.toString(this.getParm(0));
      return ((ESCollection)this.getThis()).requestNamedItems(str);
   }
}
