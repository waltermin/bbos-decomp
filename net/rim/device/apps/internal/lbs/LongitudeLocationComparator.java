package net.rim.device.apps.internal.lbs;

import net.rim.device.api.util.Comparator;

final class LongitudeLocationComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      int diff = ((Location)o2)._longitude - ((Location)o1)._longitude;
      if (diff == 0) {
         diff = ((Location)o2)._latitude - ((Location)o1)._latitude;
         if (diff == 0) {
            diff = o2.hashCode() - o1.hashCode();
         }
      }

      return diff;
   }
}
