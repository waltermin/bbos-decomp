package net.rim.device.api.crypto.keystore;

final class SyncableRIMKeyStoreTicket implements KeyStoreTicket {
   private KeyStorePasswordTicket _passwordTicket;
   private KeyStore _keyStore;

   public SyncableRIMKeyStoreTicket(String prompt, KeyStore keyStore) {
      this._passwordTicket = KeyStorePasswordManager.getInstance().getTicket(prompt, keyStore);
      this._keyStore = keyStore;
   }

   public SyncableRIMKeyStoreTicket(KeyStore keyStore) {
      this._passwordTicket = KeyStorePasswordManager.getInstance().getTicket(keyStore);
      this._keyStore = keyStore;
   }

   public SyncableRIMKeyStoreTicket(byte[] hash, KeyStore keyStore) {
      this._passwordTicket = KeyStorePasswordManager.getInstance().createTicket(hash);
      this._keyStore = keyStore;
   }

   public final boolean access(KeyStore keyStore) {
      return keyStore == this._keyStore && KeyStorePasswordManager.getInstance().checkTicket(this._passwordTicket);
   }

   public final KeyStorePasswordTicket getPasswordTicket() {
      return this._passwordTicket;
   }
}
