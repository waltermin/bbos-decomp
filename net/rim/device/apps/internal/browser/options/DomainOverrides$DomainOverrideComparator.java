package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringTokenizer;

final class DomainOverrides$DomainOverrideComparator implements Comparator {
   public DomainOverrides$DomainOverrideComparator() {
   }

   @Override
   public final int compare(Object o1, Object o2) {
      DomainOverrides$DomainOverride d1 = (DomainOverrides$DomainOverride)o1;
      DomainOverrides$DomainOverride d2 = (DomainOverrides$DomainOverride)o2;
      int type1 = d1._configType;
      int type2 = d2._configType;
      if (type1 != type2) {
         if (type1 == 1) {
            return -1;
         }

         if (type2 == 1) {
            return 1;
         }

         if (type1 == 4) {
            return -1;
         }

         if (type2 == 4) {
            return 1;
         }

         if (type1 == 8) {
            return -1;
         }

         if (type2 == 8) {
            return 1;
         }

         if (type1 == 2) {
            return -1;
         }

         if (type2 == 2) {
            return 1;
         }

         if (type1 == 7) {
            return -1;
         }

         if (type2 == 7) {
            return 1;
         }

         if (type1 == 0) {
            return -1;
         }

         if (type2 == 0) {
            return 1;
         }

         if (type1 == 3) {
            return -1;
         }

         if (type2 == 3) {
            return 1;
         }
      }

      int authority1 = this.getAuthoritySpecificity(d1._authority);
      int authority2 = this.getAuthoritySpecificity(d2._authority);
      if (authority1 > authority2) {
         return -1;
      } else if (authority1 < authority2) {
         return 1;
      } else {
         int path1 = this.getPathSpecificity(d1._path);
         int path2 = this.getPathSpecificity(d2._path);
         if (path1 > path2) {
            return -1;
         } else {
            return path1 < path2 ? 1 : 0;
         }
      }
   }

   private final int getAuthoritySpecificity(String authority) {
      return new StringTokenizer(authority, (char)46).countTokens();
   }

   private final int getPathSpecificity(String path) {
      int numberOfSlashes = 0;

      for (int slashIndex = path.indexOf(47); slashIndex >= 0; slashIndex = path.indexOf(47, slashIndex + 1)) {
         numberOfSlashes++;
      }

      return numberOfSlashes;
   }
}
