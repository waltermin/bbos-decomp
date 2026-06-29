package net.rim.device.apps.internal.lbs.maplet;

import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntLongHashtable;
import net.rim.vm.Persistable;

final class LRUCache implements Persistable {
   private IntHashtable _cache = new IntHashtable(8);
   private IntLongHashtable _lastUsed = new IntLongHashtable();
   static final int AGE_THRESHOLD = DeviceInfo.isSimulator() ? 180000 : 259200000;

   public LRUCache() {
   }

   public final synchronized boolean freeStaleMaplets(int minimumAge) {
      long oldestTime = System.currentTimeMillis() - minimumAge;
      int lastKey = 0;
      int key = lastKey;
      IntEnumeration i = this._lastUsed.keys();

      while (i.hasMoreElements()) {
         key = i.nextElement();
         if (this._lastUsed.get(key) < oldestTime) {
            lastKey = key;
            oldestTime = this._lastUsed.get(key);
         }
      }

      if (lastKey != 0) {
         this._lastUsed.remove(key);
         Maplet maplet = (Maplet)this._cache.remove(key);
         if (maplet != null) {
            LowMemoryManager.markAsRecoverable(maplet);
            return true;
         }
      }

      return false;
   }

   public final synchronized void put(int key, Object value) {
      if (key == 0) {
         throw new IllegalArgumentException("Key cannot be zero.");
      }

      this._lastUsed.put(key, System.currentTimeMillis());
      this._cache.put(key, value);
   }

   public final synchronized Object get(int key) {
      Object value = this._cache.get(key);
      if (value != null) {
         this._lastUsed.put(key, System.currentTimeMillis());
      }

      return value;
   }

   public final synchronized boolean remove(int key) {
      this._lastUsed.remove(key);
      Object value = this._cache.remove(key);
      if (value != null) {
         LowMemoryManager.markAsRecoverable(value);
         return true;
      } else {
         return false;
      }
   }

   public final synchronized boolean clear() {
      int size = this._cache.size();
      this._cache.clear();
      this._lastUsed.clear();
      return size > 0;
   }

   public final int size() {
      return this._cache.size();
   }

   public final IntEnumeration keys() {
      return this._cache.keys();
   }
}
