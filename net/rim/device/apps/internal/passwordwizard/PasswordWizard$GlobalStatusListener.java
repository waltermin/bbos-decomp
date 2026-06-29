package net.rim.device.apps.internal.passwordwizard;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;

final class PasswordWizard$GlobalStatusListener implements DialogClosedListener {
   @Override
   public final void dialogClosed(Dialog dialog, int closeReason) {
      Application app = Application.getApplication();
      app.invokeLater(new PasswordWizard$PasswordRunnable());
   }
}
