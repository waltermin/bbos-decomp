package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.wica.runtime.access.internal.data.handlers.BooleanFieldHandler;

final class EventCollection$AllDayHandler implements BooleanFieldHandler {
   private EventCollection$AllDayHandler() {
   }

   @Override
   public final boolean getValue(Object item) {
      return !(item instanceof Object) ? false : ((Event)item).getAllDayFlag();
   }

   EventCollection$AllDayHandler(EventCollection$1 x0) {
      this();
   }
}
