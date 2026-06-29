package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.system.Clipboard;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.reminders.ReminderManager;

final class CalendarOptionsEntryScreen$CopyReminderLogVerb extends Verb {
   CalendarOptionsEntryScreen$CopyReminderLogVerb() {
      super(196672, CalendarApp._rb.getFamily(), 639);
   }

   @Override
   public final Object invoke(Object parameter) {
      ReminderManager rm = ReminderManager.getInstance();
      if (rm != null) {
         String[] reminderLog = rm.getReminderLog();
         if (reminderLog != null) {
            StringBuffer sb = (StringBuffer)(new Object());

            for (int i = 0; i < reminderLog.length; i++) {
               sb.append(reminderLog[i]);
               sb.append('\n');
            }

            Clipboard.getClipboard().put(sb);
         }
      }

      return null;
   }
}
