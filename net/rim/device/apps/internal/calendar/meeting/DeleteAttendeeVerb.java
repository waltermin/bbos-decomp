package net.rim.device.apps.internal.calendar.meeting;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;

class DeleteAttendeeVerb extends Verb {
   private Field _attendeeField;

   DeleteAttendeeVerb(Field attendeeField) {
      super(16860160);
      this._attendeeField = attendeeField;
   }

   @Override
   public String toString() {
      return ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA").getString(501);
   }

   @Override
   public Object invoke(Object parameter) {
      ResourceBundle resources = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA");
      if (Dialog.ask(2, resources.getString(1001), 3) == 3) {
         Manager manager = this._attendeeField.getManager();
         if (manager != null) {
            manager.setDirty(true);
         }

         this._attendeeField.getManager().delete(this._attendeeField);
      }

      return null;
   }
}
