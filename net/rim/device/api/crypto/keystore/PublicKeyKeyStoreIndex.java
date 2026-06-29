package net.rim.device.api.crypto.keystore;

import net.rim.device.api.crypto.PublicKey;

public class PublicKeyKeyStoreIndex implements KeyStoreIndex {
   public static final long ID = 7540601854220086457L;

   @Override
   public void addToIndex(KeyStoreData data, KeyStoreDataMap dataMap) {
      PublicKey publicKey = data.getPublicKey();
      if (publicKey != null) {
         dataMap.add(publicKey.hashCode(), data);
      }
   }

   @Override
   public int getHash(Object target) {
      if (target instanceof PublicKey) {
         return target.hashCode();
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public long getID() {
      return 7540601854220086457L;
   }

   @Override
   public boolean matches(KeyStoreData data, Object target) {
      if (!(target instanceof PublicKey)) {
         return false;
      }

      PublicKey publicKey = (PublicKey)target;
      return publicKey.equals(data.getPublicKey());
   }
}
