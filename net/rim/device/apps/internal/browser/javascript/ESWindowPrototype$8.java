package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.Bitmap;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;

class ESWindowPrototype$8 extends JavaScriptHostFunction {
   private final ESWindowPrototype this$0;

   ESWindowPrototype$8(ESWindowPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      int count = this.getNumParms();
      if (count == 1) {
         try {
            String message = Convert.toString(this.getParm(0));
            SimpleChoiceDialog dialog = (SimpleChoiceDialog)(new Object(
               message, new Object[]{CommonResource.getString(100), CommonResource.getString(10005)}, 0, Bitmap.getPredefinedBitmap(0), 134217728
            ));
            BackgroundDialog.show(dialog);
            return dialog.getCloseReason() == -1 ? Value.makeBooleanValue(false) : Value.makeBooleanValue(dialog.getSelectedIndex() == 0);
         } finally {
            return Value.NULL;
         }
      } else {
         return Value.NULL;
      }
   }
}
