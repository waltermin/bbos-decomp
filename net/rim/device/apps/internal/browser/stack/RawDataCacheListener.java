package net.rim.device.apps.internal.browser.stack;

public interface RawDataCacheListener {
   int ENTRY_ADDED;
   int ENTRIES_REMOVED;
   int CACHE_CLEARED;

   void cacheChanged(int var1, Object var2);
}
