package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.certificate.Certificate;

public class PrivateKeysKeyStoreIndex implements KeyStoreIndex {
   public static final long ID = -8376547269562148933L;

   public void addToIndex(KeyStoreData data, Certificate certificate, KeyStoreDataMap dataMap) {
      if (data.isPrivateKeySet() || data.isSymmetricKeySet()) {
         dataMap.add(0, data);
      }
   }

   @Override
   public void addToIndex(KeyStoreData data, KeyStoreDataMap dataMap) {
      this.addToIndex(data, null, dataMap);
   }

   @Override
   public int getHash(Object target) {
      if (target instanceof KeyStoreData) {
         KeyStoreData temp = (KeyStoreData)target;
         if (temp.isPrivateKeySet() || temp.isSymmetricKeySet()) {
            return 0;
         }
      }

      throw new IllegalArgumentException();
   }

   @Override
   public long getID() {
      return -8376547269562148933L;
   }

   @Override
   public boolean matches(KeyStoreData data, Object target) {
      return data.equals(target);
   }
}
