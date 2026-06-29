package net.rim.device.apps.internal.passwordwizard;

import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.component.PopupDialogClosedListener;

final class PasswordWizard$PasswordRunnable$GlobalStatusListener implements PopupDialogClosedListener {
   private final PasswordWizard$PasswordRunnable this$0;

   PasswordWizard$PasswordRunnable$GlobalStatusListener(PasswordWizard$PasswordRunnable _1) {
      this.this$0 = _1;
   }

   @Override
   public final void dialogClosed(PopupDialog dialog, int closeReason) {
      byte[] password = this.this$0._dialog.getPassword();
      if (password == null) {
         System.exit(0);
      }

      PasswordWizard$WorkerThread thread = new PasswordWizard$WorkerThread(new String(password));
      thread.start();
   }
}
