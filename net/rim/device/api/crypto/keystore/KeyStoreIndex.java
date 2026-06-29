package net.rim.device.api.crypto.keystore;

public interface KeyStoreIndex {
   void addToIndex(KeyStoreData var1, KeyStoreDataMap var2);

   int getHash(Object var1);

   boolean matches(KeyStoreData var1, Object var2);

   long getID();
}
