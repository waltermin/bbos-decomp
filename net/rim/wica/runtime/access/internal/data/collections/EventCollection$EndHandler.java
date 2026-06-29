package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.wica.runtime.access.internal.data.handlers.LongFieldHandler;

final class EventCollection$EndHandler implements LongFieldHandler {
   private EventCollection$EndHandler() {
   }

   @Override
   public final long getValue(Object item) {
      if (!(item instanceof Object)) {
         return -1;
      }

      Event event = (Event)item;
      long endDate = event.getTrueEndDate();
      return event.isAllDay() ? endDate - 1 : endDate;
   }

   EventCollection$EndHandler(EventCollection$1 x0) {
      this();
   }
}
