package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;
import net.rim.wica.runtime.access.internal.data.enumeration.AttendeeTypeEnumConverter;
import net.rim.wica.runtime.access.internal.data.handlers.IntFieldHandler;

final class AttendeeCollection$TypeHandler implements IntFieldHandler {
   private AttendeeCollection$TypeHandler() {
   }

   @Override
   public final int getValue(Object item) {
      return !(item instanceof Attendee) ? 0 : AttendeeTypeEnumConverter.deviceToCommon(((Attendee)item).getType());
   }

   AttendeeCollection$TypeHandler(AttendeeCollection$1 x0) {
      this();
   }
}
