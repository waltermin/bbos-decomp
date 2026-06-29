package net.rim.device.apps.api.pim;

import net.rim.device.api.util.Comparator;

public class TimeBasedObjectProviderComparator implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      TimeBasedObjectProvider tbop1 = (TimeBasedObjectProvider)o1;
      TimeBasedObjectProvider tbop2 = (TimeBasedObjectProvider)o2;
      int size1 = tbop1.size();
      int size2 = tbop2.size();
      if (size1 < size2) {
         return 1;
      } else {
         return size1 > size2 ? -1 : 0;
      }
   }
}
