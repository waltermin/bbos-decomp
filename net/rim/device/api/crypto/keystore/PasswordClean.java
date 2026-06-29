package net.rim.device.api.crypto.keystore;

class PasswordClean implements Runnable {
   @Override
   public void run() {
      KeyStorePasswordManager.getInstance().clean();
   }
}
