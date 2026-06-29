package net.rim.device.apps.internal.secureemail.encodings.pgp.sendmethods;

import net.rim.device.api.crypto.CryptoException;
import net.rim.device.api.crypto.keystore.KeyStoreData;

public class PGPSigningKeyNotAvailableException extends CryptoException {
   private KeyStoreData _keyData;

   public PGPSigningKeyNotAvailableException(KeyStoreData keyData) {
      super("");
      this._keyData = keyData;
   }

   public KeyStoreData getKeyData() {
      return this._keyData;
   }
}
