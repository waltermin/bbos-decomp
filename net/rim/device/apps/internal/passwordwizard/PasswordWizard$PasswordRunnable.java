package net.rim.device.apps.internal.passwordwizard;

import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.internal.ui.component.PasswordDialog;

final class PasswordWizard$PasswordRunnable implements Runnable {
   private PasswordDialog _dialog;

   @Override
   public final void run() {
      String label = CryptoCommonResources.getString(3);
      this._dialog = (PasswordDialog)(new Object(label, false, 32, 33554432));
      this._dialog.setStatusPriority(-2147483642);
      this._dialog.setPopupDialogClosedListener(new PasswordWizard$PasswordRunnable$GlobalStatusListener(this));
      this._dialog.show();
   }
}
