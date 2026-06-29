package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.utility.props.LongProp;
import net.rim.wica.runtime.access.internal.data.handlers.LongFieldHandler;

final class EventCollection$AlarmHandler implements LongFieldHandler {
   private EventCollection$AlarmHandler() {
   }

   @Override
   public final long getValue(Object item) {
      if (item instanceof Event) {
         LongProp reminderObject = (LongProp)((Event)item).get(813899564474876953L);
         if (reminderObject != null) {
            long reminder = reminderObject.getLong();
            if (reminder != -1) {
               return ((Event)item).getTrueStartDate() - reminder;
            }
         }
      }

      return -1;
   }

   EventCollection$AlarmHandler(EventCollection$1 x0) {
      this();
   }
}
