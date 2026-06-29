package net.rim.device.api.crypto.keystore;

import net.rim.device.api.util.Arrays;
import net.rim.device.internal.system.DevicePasswordListener;

class KeyStorePasswordManager$KeyStoreDevicePasswordListener implements DevicePasswordListener {
   private final KeyStorePasswordManager this$0;

   private KeyStorePasswordManager$KeyStoreDevicePasswordListener(KeyStorePasswordManager _1) {
      this.this$0 = _1;
   }

   @Override
   public void changePassword(String oldPassword, String newPassword) {
      if (oldPassword != null && newPassword != null && newPassword.length() > 0) {
         byte[] oldPasswordBytes = oldPassword != null ? oldPassword.getBytes() : null;
         if (this.this$0._helper.isPassphraseSet() && Arrays.equals(this.this$0._helper.getHash(), KeyStoreUtilitiesInternal.computeHash(oldPasswordBytes))) {
            try {
               this.this$0.saveNewPassword(oldPasswordBytes, newPassword.getBytes());
               return;
            } catch (KeyStoreCancelException var5) {
            }
         }
      }
   }

   @Override
   public void unlock(String password) {
      if (password != null && password.length() > 0 && this.this$0._helper.isPassphraseSet()) {
         byte[] passwordBytes = password.getBytes();
         if (Arrays.equals(this.this$0._helper.getHash(), KeyStoreUtilitiesInternal.computeHash(passwordBytes))) {
            KeyStoreUtilitiesInternal.setTimeoutReminder();
            this.this$0._helper.setLastTime(System.currentTimeMillis());
            if (this.this$0._helper.getPassphraseTimeout() > 0) {
               this.this$0.setInternalPassword(passwordBytes);
            }
         }
      }
   }

   KeyStorePasswordManager$KeyStoreDevicePasswordListener(KeyStorePasswordManager x0, KeyStorePasswordManager$1 x1) {
      this(x0);
   }
}
