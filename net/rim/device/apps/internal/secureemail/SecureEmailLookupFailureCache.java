package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.util.ContentProtectedLookup;

public class SecureEmailLookupFailureCache {
   private long _timeout;
   private ContentProtectedLookup _cache;

   public SecureEmailLookupFailureCache(long timeout) {
      this(new ContentProtectedLookup(), timeout);
   }

   public SecureEmailLookupFailureCache(ContentProtectedLookup cache, long timeout) {
      this._cache = cache;
      this._timeout = timeout;
   }

   public boolean checkFetchFailure(Object key) {
      int foundFailureIndex = this._cache.indexOfKey(key);
      if (foundFailureIndex == -1) {
         return false;
      }

      Long failedAttemptExpiry = (Long)this._cache.valueAt(foundFailureIndex);
      if (failedAttemptExpiry > System.currentTimeMillis()) {
         return true;
      }

      synchronized (this._cache) {
         for (int i = foundFailureIndex; i >= 0; i--) {
            this._cache.removeAt(i);
         }

         return false;
      }
   }

   public void recordFetchFailure(Object key) {
      this._cache.put(key, new Long(System.currentTimeMillis() + this._timeout));
   }

   public boolean isEmpty() {
      return this._cache.isEmpty();
   }

   public void clear() {
      this._cache.clear();
   }

   public int numElements() {
      return this._cache.size();
   }
}
