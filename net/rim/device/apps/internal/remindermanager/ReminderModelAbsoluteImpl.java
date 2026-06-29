package net.rim.device.apps.internal.remindermanager;

import java.util.TimeZone;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.reminders.ReminderModelAbsolute;
import net.rim.device.apps.api.ui.CommonResources;

public final class ReminderModelAbsoluteImpl extends ReminderModelImpl implements PersistableRIMModel, ReminderModelAbsolute {
   @Override
   public final byte getType() {
      return 2;
   }

   @Override
   public final Object copy() {
      ReminderModelAbsoluteImpl that = new ReminderModelAbsoluteImpl();
      that.setTime(this.getTime());
      return that;
   }

   @Override
   public final Field getField(Object context) {
      String label = CommonResources.getString(9052);
      if (context instanceof Object) {
         ContextObject co = (ContextObject)context;
         if (co.getFlag(1)) {
            label = "";
         }
      }

      DateField reminderDateField = (DateField)(new Object(label, this.getTime(), DateFormat.getInstance(46)));
      reminderDateField.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
      return reminderDateField;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      if (!(field instanceof Object)) {
         return false;
      }

      long time = ((DateField)field).getDate();
      this.setTime(time);
      this.setState(1);
      this.setReminderFiredFor(Long.MIN_VALUE);
      return true;
   }
}
