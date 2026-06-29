package net.rim.device.api.crypto.keystore;

final class RIMKeyStoreDataTicket implements KeyStoreDataTicket {
   private KeyStorePasswordTicket _passwordTicket;
   private KeyStoreData _data;

   public RIMKeyStoreDataTicket(String prompt, KeyStoreData data) {
      this._passwordTicket = KeyStorePasswordManager.getInstance().getTicket(prompt, data);
      this._data = data;
   }

   public final boolean access(KeyStoreData data) {
      return data.equals(this._data) && KeyStorePasswordManager.getInstance().checkTicket(this._passwordTicket);
   }

   public final KeyStorePasswordTicket getPasswordTicket() {
      return this._passwordTicket;
   }

   public final KeyStoreData getKeyStoreData() {
      return this._data;
   }
}
