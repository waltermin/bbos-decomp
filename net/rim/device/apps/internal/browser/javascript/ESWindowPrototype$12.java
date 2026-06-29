package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.SimpleOKCancelInputDialog;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;

class ESWindowPrototype$12 extends JavaScriptHostFunction {
   private final ESWindowPrototype this$0;

   ESWindowPrototype$12(ESWindowPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      try {
         String message = Convert.toString(this.getParm(0));
         String defaultValue = Convert.toString(this.getParm(1));
         SimpleOKCancelInputDialog dialog = (SimpleOKCancelInputDialog)(new Object(0, message, 0, 1000000, 134217728));
         dialog.setText(defaultValue);
         BackgroundDialog.show(dialog);
         return dialog.getCloseReason() == -1 ? Value.NULL : Value.makeStringValue(dialog.getText());
      } finally {
         ;
      }
   }
}
