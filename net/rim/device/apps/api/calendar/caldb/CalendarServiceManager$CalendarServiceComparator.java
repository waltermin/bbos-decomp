package net.rim.device.apps.api.calendar.caldb;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;

class CalendarServiceManager$CalendarServiceComparator implements Comparator {
   private CalendarServiceManager$CalendarServiceComparator() {
   }

   @Override
   public int compare(Object o1, Object o2) {
      return o1 instanceof CalendarService && o2 instanceof CalendarService
         ? StringUtilities.compareToIgnoreCase(((CalendarService)o1).getServiceName(), ((CalendarService)o2).getServiceName())
         : 0;
   }

   CalendarServiceManager$CalendarServiceComparator(CalendarServiceManager$1 x0) {
      this();
   }
}
