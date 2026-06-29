package net.rim.device.apps.api.ui;

import net.rim.device.api.system.UserAuthenticator;
import net.rim.device.internal.ui.component.PleaseWaitWorkerThread;

class SecurityDialog$GetAuthenticatorThread extends PleaseWaitWorkerThread {
   private UserAuthenticator _userAuthenticator;
   private boolean _itAdminEnforced;

   public SecurityDialog$GetAuthenticatorThread(boolean itAdminEnforced) {
      this._itAdminEnforced = itAdminEnforced;
   }

   public UserAuthenticator getUserAuthenticator() {
      return this._userAuthenticator;
   }

   @Override
   public void doWork() {
      while (this._userAuthenticator == null) {
         UserAuthenticator[] registeredUserAuthenticators = SecurityDialog._security.getRegisteredUserAuthenticators();
         if (registeredUserAuthenticators != null && registeredUserAuthenticators.length != 0) {
            for (int i = 0; i < registeredUserAuthenticators.length; i++) {
               try {
                  if (registeredUserAuthenticators[i].isReadyForInitialization()) {
                     this._userAuthenticator = registeredUserAuthenticators[i];
                     break;
                  }
               } finally {
                  continue;
               }
            }

            if (this._userAuthenticator != null || this._itAdminEnforced) {
               continue;
            }

            return;
         }

         return;
      }
   }
}
