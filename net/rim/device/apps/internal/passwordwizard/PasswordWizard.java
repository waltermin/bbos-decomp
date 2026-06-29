package net.rim.device.apps.internal.passwordwizard;

import net.rim.device.api.crypto.keystore.KeystorePassword;
import net.rim.device.api.system.UserAuthenticator;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.internal.system.FIPSPolicy;
import net.rim.device.internal.system.Security;
import net.rim.vm.Process;

final class PasswordWizard extends UiApplication {
   static DevicePassword _devicePassword = new DevicePassword();
   static KeystorePassword _keyStorePassword = new KeystorePassword();

   public static final void main(String[] args) {
      Process.waitForIdle();
      Security security = Security.getInstance();
      boolean authenticatorReady = false;
      UserAuthenticator[] registeredUserAuthenticators = security.getRegisteredUserAuthenticators();
      if (registeredUserAuthenticators != null && registeredUserAuthenticators.length > 0) {
         for (int i = 0; i < registeredUserAuthenticators.length; i++) {
            try {
               if (registeredUserAuthenticators[i].isInitializationPossible()) {
                  authenticatorReady = true;
                  break;
               }
            } finally {
               continue;
            }
         }
      }

      if (!authenticatorReady || _devicePassword.isPasswordEnabled() || _keyStorePassword.isPasswordEnabled() || FIPSPolicy.isDevicePasswordRequired()) {
         System.exit(0);
      }

      PasswordWizard wizard = new PasswordWizard();
      wizard.invokeLater(new PasswordWizard$PasswordRunnable());
      wizard.enterEventDispatcher();
   }
}
