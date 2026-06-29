package net.rim.device.apps.internal.browser.cookie;

import net.rim.device.api.util.Comparator;

final class CookieCache$CookieComparator implements Comparator {
   private CookieCache$CookieComparator() {
   }

   @Override
   public final int compare(Object o1, Object o2) {
      Cookie c1 = (Cookie)o1;
      Cookie c2 = (Cookie)o2;
      int path1 = this.getPathSpecificity(c1.getPath());
      int path2 = this.getPathSpecificity(c2.getPath());
      if (path1 > path2) {
         return -1;
      } else {
         return path1 < path2 ? 1 : 0;
      }
   }

   private final int getPathSpecificity(String path) {
      int numberOfSlashes = 0;

      for (int slashIndex = path.indexOf(47); slashIndex >= 0; slashIndex = path.indexOf(47)) {
         numberOfSlashes++;
         path = path.substring(slashIndex + 1);
      }

      return numberOfSlashes;
   }

   CookieCache$CookieComparator(CookieCache$1 x0) {
      this();
   }
}
