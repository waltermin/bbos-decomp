package net.rim.device.api.crypto.keystore;

class PGPPersistableRIMKeyStoreFactory implements PersistableRIMKeyStoreFactory {
   @Override
   public KeyStore createInstance() {
      return PGPKeyStore.getInstance();
   }
}
