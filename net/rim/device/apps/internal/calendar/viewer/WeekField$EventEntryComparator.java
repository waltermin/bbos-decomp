package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.util.Comparator;

final class WeekField$EventEntryComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      WeekField$EventEntry e1 = (WeekField$EventEntry)o1;
      WeekField$EventEntry e2 = (WeekField$EventEntry)o2;
      if (e1._start < e2._start) {
         return -1;
      } else {
         return e1._start > e2._start ? 1 : 0;
      }
   }
}
