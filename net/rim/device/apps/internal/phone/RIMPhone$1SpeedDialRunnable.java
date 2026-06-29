package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.ui.ClickAndHoldKey;

final class RIMPhone$1SpeedDialRunnable implements Runnable {
   int _keycode;
   private final RIMPhone this$0;

   RIMPhone$1SpeedDialRunnable(RIMPhone _1, int keycode) {
      this.this$0 = _1;
      this._keycode = keycode;
   }

   @Override
   public final void run() {
      if (this.this$0._clickAndHoldKey == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         this.this$0._clickAndHoldKey = (ClickAndHoldKey)ar.get(-6860088460751500843L);
      }

      this.this$0._clickAndHoldKey.doClickAndHold(this._keycode);
   }
}
