package net.rim.device.apps.internal.passwordwizard;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;

final class PasswordWizard$ErrorRunnable implements Runnable {
   @Override
   public final void run() {
      String label = CryptoCommonResources.getString(4);
      Dialog dialog = (Dialog)(new Object(0, label, 0, null, 33554432));
      dialog.setDialogClosedListener(new PasswordWizard$GlobalStatusListener());
      dialog.show();
   }
}
