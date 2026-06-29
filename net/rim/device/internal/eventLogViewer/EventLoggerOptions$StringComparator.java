package net.rim.device.internal.eventLogViewer;

import net.rim.device.api.util.Comparator;

final class EventLoggerOptions$StringComparator implements Comparator {
   private EventLoggerOptions$StringComparator() {
   }

   @Override
   public final int compare(Object o1, Object o2) {
      return ((String)o1).toLowerCase().compareTo(((String)o2).toLowerCase());
   }

   EventLoggerOptions$StringComparator(EventLoggerOptions$1 x0) {
      this();
   }
}
