package net.rim.device.api.crypto.keystore;

import net.rim.device.apps.internal.passwordwizard.Password;

public final class KeystorePassword extends Password {
   private KeyStoreManagerHelper _helper = KeyStoreManagerHelper.getInstance();

   public final boolean isPasswordEnabled() {
      return this._helper.isPassphraseSet();
   }

   public final boolean setPassword(String password) {
      if (this.isPasswordEnabled()) {
         throw new Object("Device Password is already set");
      }

      byte[] hash = KeyStoreUtilitiesInternal.computeHash(password.getBytes());
      this._helper.setHash(hash);
      return true;
   }
}
