package net.rim.device.apps.internal.browser.stack;

public interface RawDataCacheListener {
   int ENTRY_ADDED = 0;
   int ENTRIES_REMOVED = 1;
   int CACHE_CLEARED = 2;

   void cacheChanged(int var1, Object var2);
}
