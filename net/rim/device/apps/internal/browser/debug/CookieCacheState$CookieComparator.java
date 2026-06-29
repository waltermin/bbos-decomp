package net.rim.device.apps.internal.browser.debug;

import net.rim.device.api.util.Comparator;

final class CookieCacheState$CookieComparator implements Comparator {
   private CookieCacheState$CookieComparator() {
   }

   @Override
   public final int compare(Object o1, Object o2) {
      CookieDetail c1 = (CookieDetail)o1;
      CookieDetail c2 = (CookieDetail)o2;
      return c1.getCookie().getDomain().compareTo(c2.getCookie().getDomain());
   }

   CookieCacheState$CookieComparator(CookieCacheState$1 x0) {
      this();
   }
}
