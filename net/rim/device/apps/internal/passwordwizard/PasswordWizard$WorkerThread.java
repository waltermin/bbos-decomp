package net.rim.device.apps.internal.passwordwizard;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.UserAuthenticator;
import net.rim.device.internal.system.Security;

final class PasswordWizard$WorkerThread extends Thread {
   private String _userAuthenticatorPassword;
   private Security _security;

   public PasswordWizard$WorkerThread(String userAuthenticatorPassword) {
      this._userAuthenticatorPassword = userAuthenticatorPassword;
      this._security = Security.getInstance();
   }

   public final UserAuthenticator getUserAuthenticator() {
      UserAuthenticator[] registeredUserAuthenticators = this._security.getRegisteredUserAuthenticators();
      if (registeredUserAuthenticators != null && registeredUserAuthenticators.length > 0) {
         for (int i = 0; i < registeredUserAuthenticators.length; i++) {
            try {
               if (registeredUserAuthenticators[i].isReadyForInitialization()) {
                  return registeredUserAuthenticators[i];
               }
            } finally {
               continue;
            }
         }
      }

      return null;
   }

   @Override
   public final void run() {
      boolean result = false;

      label52:
      try {
         UserAuthenticator userAuthenticator = this.getUserAuthenticator();
         if (userAuthenticator != null
            && PasswordWizard._devicePassword.isPasswordValid(this._userAuthenticatorPassword)
            && this._security.setUserAuthenticatorPassword(userAuthenticator, this._userAuthenticatorPassword)) {
            result = PasswordWizard._devicePassword.setPassword(this._userAuthenticatorPassword)
               && PasswordWizard._keyStorePassword.setPassword(this._userAuthenticatorPassword);
            if (!result) {
               this._security.uninitializeUserAuthenticator();
            }
         }
      } finally {
         break label52;
      }

      if (!result) {
         Application app = Application.getApplication();
         app.invokeLater(new PasswordWizard$ErrorRunnable());
      } else {
         System.exit(0);
      }
   }
}
