package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.ui.component.SimpleInputDialog;

public final class CreateNewCalendarVerb extends Verb {
   public CreateNewCalendarVerb(int ordering, ResourceBundleFamily rb, int rbKey) {
   }

   @Override
   public final Object invoke(Object parameter) {
      SimpleInputDialog calendarNameDialog = (SimpleInputDialog)(new Object(11, CalendarApp._rb.getString(11)));
      calendarNameDialog.show();
      String name = calendarNameDialog.getText();
      if (name != null && name.trim().length() != 0) {
         throw new Object("The creation of new calendars in this way is unsupported");
      }

      Dialog.alert(CalendarApp._rb.getString(10));
      return null;
   }
}
