package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.wica.runtime.access.internal.data.handlers.LongFieldHandler;

final class EventCollection$StartHandler implements LongFieldHandler {
   private EventCollection$StartHandler() {
   }

   @Override
   public final long getValue(Object item) {
      return !(item instanceof Event) ? -1 : ((Event)item).getTrueStartDate();
   }

   EventCollection$StartHandler(EventCollection$1 x0) {
      this();
   }
}
