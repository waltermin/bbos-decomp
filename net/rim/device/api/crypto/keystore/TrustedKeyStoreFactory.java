package net.rim.device.api.crypto.keystore;

class TrustedKeyStoreFactory implements PersistableRIMKeyStoreFactory {
   @Override
   public KeyStore createInstance() {
      return TrustedKeyStore.getInstance();
   }
}
