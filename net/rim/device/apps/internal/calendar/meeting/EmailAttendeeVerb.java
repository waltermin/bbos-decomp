package net.rim.device.apps.internal.calendar.meeting;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.verb.Verb;

class EmailAttendeeVerb extends Verb {
   private VerticalFieldManager _vfm;
   private Field _attendeeField;
   private String _emailSubject;
   private ServiceRecord _cmimeServiceRecord;

   EmailAttendeeVerb(ServiceRecord cmimeServiceRecord, String emailSubject, VerticalFieldManager vfm, Field attendeeField) {
      super(attendeeField == null ? 16860672 : 16860416);
      this._attendeeField = attendeeField;
      this._emailSubject = emailSubject;
      this._vfm = vfm;
      this._cmimeServiceRecord = cmimeServiceRecord;
   }

   @Override
   public String toString() {
      return ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA")
         .getString(this._attendeeField == null ? 609 : 610);
   }

   @Override
   public Object invoke(Object parameter) {
      MeetingUtilities.composeEmailToAttendees(this._cmimeServiceRecord, this._emailSubject, this._vfm, this._attendeeField, parameter);
      return null;
   }
}
