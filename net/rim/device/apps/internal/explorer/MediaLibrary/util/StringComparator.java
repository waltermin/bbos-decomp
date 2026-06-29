package net.rim.device.apps.internal.explorer.MediaLibrary.util;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

public class StringComparator implements Comparator {
   private static final StringComparator _instance = new StringComparator();

   private StringComparator() {
   }

   public static StringComparator getInstance() {
      return _instance;
   }

   @Override
   public int compare(Object o1, Object o2) {
      if (o1 != null && o2 != null) {
         String str1 = o1.toString();
         String str2 = o2.toString();
         return str1 != null && str2 != null ? StringUtilities.compareToIgnoreCase(o1.toString(), o2.toString()) : 0;
      } else {
         return 0;
      }
   }

   @Override
   public boolean equals(Object o) {
      return false;
   }
}
