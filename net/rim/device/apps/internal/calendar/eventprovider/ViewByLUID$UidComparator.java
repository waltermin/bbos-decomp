package net.rim.device.apps.internal.calendar.eventprovider;

import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;

class ViewByLUID$UidComparator implements Comparator {
   @Override
   public int compare(Object o1, Object o2) {
      Event e1 = (Event)o1;
      Event e2 = (Event)o2;
      if (e1.getUID() == e2.getUID()) {
         return 0;
      } else {
         return e1.getUID() < e2.getUID() ? -1 : 1;
      }
   }

   @Override
   public boolean equals(Object obj) {
      return false;
   }
}
