package net.rim.device.api.crypto.keystore;

class DevicePersistableRIMKeyStoreFactory implements PersistableRIMKeyStoreFactory {
   @Override
   public KeyStore createInstance() {
      return DeviceKeyStore.getInstance();
   }
}
