package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class EventCollection$LocationHandler implements ObjectFieldHandler {
   private EventCollection$LocationHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      return !(item instanceof Object) ? null : ((Event)item).getLocation();
   }

   EventCollection$LocationHandler(EventCollection$1 x0) {
      this();
   }
}
