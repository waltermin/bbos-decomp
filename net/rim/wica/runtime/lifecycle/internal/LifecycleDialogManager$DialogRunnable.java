package net.rim.wica.runtime.lifecycle.internal;

import net.rim.device.api.ui.component.Dialog;

class LifecycleDialogManager$DialogRunnable implements Runnable {
   private Dialog _dialog;

   LifecycleDialogManager$DialogRunnable(Dialog dialog) {
      this._dialog = dialog;
   }

   @Override
   public void run() {
      this._dialog.show();
   }
}
