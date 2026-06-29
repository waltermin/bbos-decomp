package net.rim.device.apps.internal.calendar.meeting;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.verb.Verb;

class ChangeAttendeeVerb extends Verb {
   private Field _attendeeField;

   ChangeAttendeeVerb(Field attendeeField) {
      super(16859904);
      this._attendeeField = attendeeField;
   }

   @Override
   public String toString() {
      return ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA").getString(500);
   }

   @Override
   public Object invoke(Object parameter) {
      Manager manager = this._attendeeField.getManager();
      Object objectToReturn = MeetingUtilities.pickAttendee(manager, this._attendeeField);
      if (objectToReturn != null) {
         manager.setDirty(true);
      }

      return objectToReturn;
   }
}
