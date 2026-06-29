package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.apps.api.ui.WorldPhoneDisclaimerDialog;
import net.rim.device.internal.system.RadioInternal;

final class SystemMonitor$2 implements Runnable {
   private final SystemMonitor this$0;

   SystemMonitor$2(SystemMonitor _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      WorldPhoneDisclaimerDialog dialog = new WorldPhoneDisclaimerDialog(this.this$0._rbf.getString(167), RadioInternal.getEnabledRadios());
      dialog.showDialog();
   }
}
