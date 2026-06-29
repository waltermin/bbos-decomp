package net.rim.device.apps.internal.calendar.meeting;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.verb.Verb;

class InviteAttendeeVerb extends Verb {
   private VerticalFieldManager _vfm;

   InviteAttendeeVerb(VerticalFieldManager vfm) {
      super(16859648);
      this._vfm = vfm;
   }

   @Override
   public String toString() {
      return ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA").getString(502);
   }

   @Override
   public Object invoke(Object parameter) {
      Object objectToReturn = MeetingUtilities.pickAttendee(this._vfm, null);
      if (objectToReturn != null) {
         this._vfm.setDirty(true);
      }

      return objectToReturn;
   }
}
