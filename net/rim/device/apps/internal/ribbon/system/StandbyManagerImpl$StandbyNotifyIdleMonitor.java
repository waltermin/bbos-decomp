package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.ui.component.Dialog;

final class StandbyManagerImpl$StandbyNotifyIdleMonitor extends Thread {
   Dialog _dialog;
   private final StandbyManagerImpl this$0;

   StandbyManagerImpl$StandbyNotifyIdleMonitor(StandbyManagerImpl _1, Dialog dialog, long idleTime) {
      this.this$0 = _1;
      this._dialog = dialog;
      Application.getApplication().invokeLater(this, idleTime, false);
   }

   @Override
   public final void run() {
      this._dialog.close();
      if (!this.this$0._muteKeyPressed) {
         Backlight.enable(false);
         this.this$0._enableBacklightForNotify = false;
      }

      this.this$0._muteKeyPressed = false;
   }
}
