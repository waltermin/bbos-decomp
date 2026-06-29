package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.Dialog;

final class StandbyManagerImpl$EnterStandbyIdleMonitor extends Thread {
   Dialog _dialog;
   private final StandbyManagerImpl this$0;

   StandbyManagerImpl$EnterStandbyIdleMonitor(StandbyManagerImpl _1, Dialog dialog, long idleTime) {
      this.this$0 = _1;
      this._dialog = dialog;
      Application.getApplication().invokeLater(this, idleTime, false);
   }

   @Override
   public final void run() {
      this._dialog.close();
      if (!this.this$0._muteKeyPressed && !this.this$0._interruptEnterStandby) {
         this.this$0._inStandby = true;
         Backlight.enable(false);
         Keypad.enableStandbyMode(true);
      }

      this.this$0._muteKeyPressed = false;
      this.this$0._interruptEnterStandby = false;
   }
}
