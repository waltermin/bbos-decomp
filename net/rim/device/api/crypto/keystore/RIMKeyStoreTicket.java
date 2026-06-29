package net.rim.device.api.crypto.keystore;

final class RIMKeyStoreTicket implements KeyStoreTicket {
   private KeyStorePasswordTicket _passwordTicket;
   private KeyStore _keyStore;

   public RIMKeyStoreTicket(String additionalPrompt, KeyStore keyStore) {
      this._passwordTicket = KeyStorePasswordManager.getInstance().getTicket(additionalPrompt, keyStore);
      this._keyStore = keyStore;
   }

   public RIMKeyStoreTicket(KeyStore keyStore, byte[] hash) {
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
