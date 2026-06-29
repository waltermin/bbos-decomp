package net.rim.device.api.crypto.keystore;

import net.rim.device.api.util.IntMultiMap;

public class KeyStoreDataMap {
   private IntMultiMap _map;

   public KeyStoreDataMap(IntMultiMap map) {
      if (map == null) {
         throw new IllegalArgumentException();
      }

      this._map = map;
   }

   public void add(int hash, KeyStoreData data) {
      this._map.add(hash, data);
   }

   public boolean checkHash(int hash) {
      return this._map.containsKey(hash);
   }
}
