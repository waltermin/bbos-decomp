package net.rim.device.apps.internal.browser.push;

import java.io.InputStream;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.multipart.MultipartUtilities;
import net.rim.device.apps.internal.browser.stack.CacheNode;
import net.rim.device.apps.internal.browser.stack.CacheResult;
import net.rim.device.apps.internal.browser.stack.RawDataCache;
import net.rim.vm.Array;

final class BrowserPushUtilities {
   static final byte[] readContent(InputStream inputStream) {
      EventLogger.logEvent(1907089860548946979L, 1347449443, 5);

      try {
         byte[] content = new byte[0];
         int offset = 0;
         int bytesRead = 0;

         while (bytesRead >= 0) {
            Array.resize(content, content.length + 1024);
            bytesRead = inputStream.read(content, offset, 1024);
            if (bytesRead > 0) {
               offset += bytesRead;
            }
         }

         inputStream.close();
         Array.resize(content, offset);
         return content;
      } finally {
         ;
      }
   }

   static final void addToCache(CacheResult cacheResult, boolean sticky) {
      String url = cacheResult.getURLWithoutFragment();
      RawDataCache rawDataCache = BrowserDaemonRegistry.getInstance().getRawDataCache();
      CacheNode node = null;
      synchronized (rawDataCache.getLock()) {
         rawDataCache.remove(url, true);
         rawDataCache.put(url, cacheResult, false);
         node = rawDataCache.getCacheNode(url);
      }

      MultipartUtilities.addToCacheIfMultipart(cacheResult);
      if (node != null && sticky) {
         rawDataCache.moveNodeToPeristentCache(node);
         rawDataCache.commit();
      }
   }
}
