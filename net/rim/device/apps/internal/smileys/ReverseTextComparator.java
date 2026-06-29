package net.rim.device.apps.internal.smileys;

import net.rim.device.api.util.Comparator;

class ReverseTextComparator implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      String s1 = (String)o1;
      String s2 = (String)o2;
      int i1 = s1.length() - 1;

      int i2;
      for (i2 = s2.length() - 1; i1 >= 0 && i2 >= 0; i2--) {
         int c1 = s1.charAt(i1);
         int c2 = s2.charAt(i2);
         if (c1 < c2) {
            return -1;
         }

         if (c1 > c2) {
            return 1;
         }

         i1--;
      }

      return i1 - i2;
   }
}
