package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.wica.runtime.access.internal.data.enumeration.FreeBusyEnumConverter;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;

final class EventCollection$FreeBusyHandler implements IntFieldHandler {
   private EventCollection$FreeBusyHandler() {
   }

   @Override
   public final int getValue(Object item) {
      return !(item instanceof Event) ? -1 : FreeBusyEnumConverter.deviceToCommon(((Event)item).getFreeBusy());
   }

   EventCollection$FreeBusyHandler(EventCollection$1 x0) {
      this();
   }
}
