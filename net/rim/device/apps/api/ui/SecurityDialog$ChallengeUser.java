package net.rim.device.apps.api.ui;

import net.rim.device.api.system.UserAuthenticator;
import net.rim.device.internal.ui.component.PleaseWaitWorkerThread;

class SecurityDialog$ChallengeUser extends PleaseWaitWorkerThread {
   private String _oldPassword;
   private String _newPassword;
   private String _userAuthenticatorPassword;
   private UserAuthenticator _userAuthenticator;
   private boolean _changePassword;
   private boolean _initializeAuthenticator;
   private boolean _result;
   private int[] _uSBChannels;

   public SecurityDialog$ChallengeUser(String password, String userAuthenticatorPassword) {
      this._newPassword = password;
      this._userAuthenticatorPassword = userAuthenticatorPassword;
   }

   public SecurityDialog$ChallengeUser(String password, String userAuthenticatorPassword, int[] uSBChannels) {
      this._uSBChannels = uSBChannels;
      this._newPassword = password;
      this._userAuthenticatorPassword = userAuthenticatorPassword;
   }

   public SecurityDialog$ChallengeUser(String oldPassword, String newPassword, String userAuthenticatorPassword) {
      this._newPassword = newPassword;
      this._oldPassword = oldPassword;
      this._userAuthenticatorPassword = userAuthenticatorPassword;
      this._changePassword = true;
   }

   public SecurityDialog$ChallengeUser(String userAuthenticatorPassword, UserAuthenticator userAuthenticator) {
      this._userAuthenticatorPassword = userAuthenticatorPassword;
      this._userAuthenticator = userAuthenticator;
      this._initializeAuthenticator = true;
   }

   public boolean getResult() {
      return this._result;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void doWork() {
      this._result = false;

      try {
         if (this._changePassword) {
            this._result = SecurityDialog._security.setPassword(this._oldPassword, this._newPassword, this._userAuthenticatorPassword);
            if (this._result) {
               SecurityDialog.addPasswordHistory(this._newPassword);
            }
         } else if (this._initializeAuthenticator) {
            this._result = SecurityDialog._security.setUserAuthenticatorPassword(this._userAuthenticator, this._userAuthenticatorPassword);
         } else {
            if (this._uSBChannels == null) {
               this._result = SecurityDialog._security.verifyPassword(this._newPassword, this._userAuthenticatorPassword);
               return;
            }

            this._result = SecurityDialog._security.verifyPassword(this._newPassword, this._userAuthenticatorPassword, this._uSBChannels);
         }
      } catch (Throwable var3) {
         this.setThrowable(e);
         return;
      }
   }
}
