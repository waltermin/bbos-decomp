package net.rim.device.api.crypto.keystore;

class KeyStoreITPolicyListener$KeyStoreITPolicyCheck extends Thread {
   private boolean _promptForPassword;

   public KeyStoreITPolicyListener$KeyStoreITPolicyCheck(boolean promptForPassword) {
      this._promptForPassword = promptForPassword;
   }

   @Override
   public void run() {
      if (this._promptForPassword) {
         KeyStorePasswordManager.getInstance().performITPolicyCheck();
      }

      ((TrustedKeyStore)TrustedKeyStore.getInstance()).performITPolicyCheck();
      KeyStorePasswordManager.getInstance().checkPassword();
   }
}
