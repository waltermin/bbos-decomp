package net.rim.device.api.smartcard;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.TicketDialog;

public class SmartCardTicketDialog extends TicketDialog implements GlobalEventListener {
   private Application _app = Application.getApplication();

   static String getPassword(RichTextField labelField) {
      return getPassword(labelField, false);
   }

   static String getPassword(RichTextField labelField, boolean allowOnLockScreen) throws SmartCardCancelException {
      SmartCardTicketDialog dialog = new SmartCardTicketDialog(labelField, true, null, false, 134217728);
      if (!allowOnLockScreen) {
         ApplicationManager appMan = ApplicationManager.getApplicationManager();
         if (appMan.isSystemLocked() && isSmartCardAuthenticatorInstalled()) {
            throw new SmartCardCancelException();
         }
      } else {
         dialog.setStatusPriority(-2147483644);
      }

      BackgroundDialog.show(dialog);
      if (dialog.getCloseReason() == -1) {
         throw new SmartCardCancelException();
      } else {
         return new String(dialog.getPassword());
      }
   }

   public SmartCardTicketDialog(RichTextField label, boolean promptForPassword, String promptForPasswordString, boolean revealPassword, long style) {
      super(
         label,
         promptForPassword,
         promptForPasswordString,
         revealPassword,
         style,
         Security.getInstance().isSmartPasswordEntryEnabledOnUserAuthenticatorPassword()
      );
      this._app.addGlobalEventListener(this);
      this.setCancelAllowed(true);
   }

   @Override
   protected void onUndisplay() {
      this._app.removeGlobalEventListener(this);
      super.onUndisplay();
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7131874474196788121L && isSmartCardAuthenticatorInstalled()) {
         this._app.invokeLater(new SmartCardTicketDialog$1(this));
      }
   }

   public static boolean isSmartCardAuthenticatorInstalled() {
      return GenericSmartCardUserAuthenticatorFacade.isSmartCardAuthenticatorInstalled();
   }

   static void access$000(SmartCardTicketDialog x0, int x1) {
      x0.close(x1);
   }
}
