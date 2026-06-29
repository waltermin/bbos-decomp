package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;

final class EventCollection$UIDHandler implements IntFieldHandler {
   private EventCollection$UIDHandler() {
   }

   @Override
   public final int getValue(Object item) {
      return !(item instanceof Event) ? -1 : ((Event)item).getUID();
   }

   EventCollection$UIDHandler(EventCollection$1 x0) {
      this();
   }
}
