package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.KeyStoreData;

class SecureEmailKeyStoreData {
   private KeyStoreData _keyStoreData;
   private String _emailAddress;

   SecureEmailKeyStoreData(KeyStoreData keyStoreData) {
      this(keyStoreData, null);
   }

   SecureEmailKeyStoreData(KeyStoreData keyStoreData, String emailAddress) {
      if (keyStoreData == null) {
         throw new Object();
      }

      this._keyStoreData = keyStoreData;
      this._emailAddress = emailAddress;
   }

   Certificate getCertificate() {
      return this._keyStoreData.getCertificate();
   }

   String getLabel() {
      return this._keyStoreData.getLabel();
   }

   String getEmailAddress() {
      return this._emailAddress;
   }
}
