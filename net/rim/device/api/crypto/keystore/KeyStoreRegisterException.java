package net.rim.device.api.crypto.keystore;

public class KeyStoreRegisterException extends KeyStoreException {
   private Exception _e;

   public KeyStoreRegisterException() {
   }

   public KeyStoreRegisterException(String msg) {
      super(msg);
   }

   public KeyStoreRegisterException(Exception e) {
      this._e = e;
   }

   public Exception getException() {
      return this._e;
   }
}
