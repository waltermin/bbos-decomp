package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class AttendeeCollection$AddressHandler implements ObjectFieldHandler {
   private AttendeeCollection$AddressHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      return !(item instanceof Object) ? null : ((Attendee)item).getAddress();
   }

   AttendeeCollection$AddressHandler(AttendeeCollection$1 x0) {
      this();
   }
}
