package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;

class ESWindowPrototype$1 extends JavaScriptHostFunction {
   private final ESWindowPrototype this$0;

   ESWindowPrototype$1(ESWindowPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      int count = this.getNumParms();
      StringBuffer out = (StringBuffer)(new Object());

      for (int i = 0; i < count; i++) {
         try {
            out.append(Convert.toString(this.getParm(i)));
         } finally {
            continue;
         }
      }

      BackgroundDialog.showMessage(out.toString());
      return Value.NULL;
   }
}
