package net.rim.device.apps.internal.browser.debug;

import net.rim.device.api.util.Comparator;

final class RawDataCacheState$CacheUrlComparator implements Comparator {
   private RawDataCacheState$CacheUrlComparator() {
   }

   @Override
   public final int compare(Object o1, Object o2) {
      CachedUrlDetail c1 = (CachedUrlDetail)o1;
      CachedUrlDetail c2 = (CachedUrlDetail)o2;
      return c1.getLabel().compareTo(c2.getLabel());
   }

   RawDataCacheState$CacheUrlComparator(RawDataCacheState$1 x0) {
      this();
   }
}
