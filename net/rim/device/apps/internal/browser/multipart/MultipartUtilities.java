package net.rim.device.apps.internal.browser.multipart;

import net.rim.device.apps.internal.browser.stack.CacheResult;

public final class MultipartUtilities {
   public static final void addToCacheIfMultipart(CacheResult cacheResult) {
      String mainContentType = cacheResult.getResponseHeaders().getPropertyValue("content-type");
      if (MultipartConverter.getMultipartType(mainContentType) != 0) {
         try {
            MultipartConverter.convert(cacheResult.getStream(), mainContentType, cacheResult.getURLWithoutFragment(), true, null, cacheResult, null);
         } finally {
            return;
         }
      }
   }
}
