package net.rim.device.apps.internal.browser.util;

import net.rim.device.api.io.http.HttpDateParser;
import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.system.ApplicationRegistry;

public final class DateCache implements MemoryCleanerListener {
   private int[] _cacheHash = new int[10];
   private String[] _cacheKey;
   private long[] _cacheValue = new long[10];
   private int _tail;
   private static final long APP_REGISTRY_KEY = 1903262715808435584L;
   private static final int CACHE_SIZE = 10;

   private DateCache() {
      this._cacheKey = new Object[10];
      MemoryCleanerDaemon.addWeakListener(this, false);
   }

   private static final DateCache getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      DateCache instance = (DateCache)ar.getOrWaitFor(1903262715808435584L);
      if (instance == null) {
         instance = new DateCache();
         ar.put(1903262715808435584L, instance);
      }

      return instance;
   }

   public static final long parse(String date) {
      return getInstance().parseInternal(date);
   }

   private final long parseInternal(String date) {
      if (date != null && date.length() != 0) {
         synchronized (this._cacheHash) {
            int hashCode = date.hashCode();

            for (int i = 0; i < 10; i++) {
               if (this._cacheHash[i] == hashCode && date.equals(this._cacheKey[i])) {
                  return this._cacheValue[i];
               }
            }

            long value = HttpDateParser.parse(date);
            this._cacheHash[this._tail] = hashCode;
            this._cacheValue[this._tail] = value;
            this._cacheKey[this._tail] = date;
            this._tail = (this._tail + 1) % 10;
            return value;
         }
      } else {
         return 0;
      }
   }

   @Override
   public final boolean cleanNow(int event) {
      if (event == 10) {
         synchronized (this._cacheHash) {
            boolean gc = false;

            for (int i = 0; i < 10; i++) {
               gc |= this._cacheKey[i] != null;
               this._cacheKey[i] = null;
               this._cacheValue[i] = 0;
               this._cacheHash[i] = 0;
            }

            this._tail = 0;
            return gc;
         }
      } else {
         return false;
      }
   }

   @Override
   public final String getDescription() {
      return null;
   }
}
