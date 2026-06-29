package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class EventCollection$SummaryHandler implements ObjectFieldHandler {
   private EventCollection$SummaryHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      return !(item instanceof Event) ? null : ((Event)item).getSubject();
   }

   EventCollection$SummaryHandler(EventCollection$1 x0) {
      this();
   }
}
