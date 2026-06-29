package net.rim.device.apps.api.reminders;

import java.util.Calendar;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.vm.Array;
import net.rim.vm.Persistable;

class ReminderRollingLog implements Persistable {
   private ReminderRollingLog$ReminderLogEvent[] _eventLog;
   private int _position;
   private static SimpleDateFormat _currentTimeFormat = new SimpleDateFormat("dd/MM hh:mm ");
   private static SimpleDateFormat _eventTimeFormat = new SimpleDateFormat("hh:mm dd/MM/yy");
   private static StringBuffer _scratchBuffer = new StringBuffer();
   private static Calendar _cal = Calendar.getInstance();

   ReminderRollingLog(int size) {
      this._eventLog = new ReminderRollingLog$ReminderLogEvent[size];
      this._position = size;
   }

   public void addEvent(int code, long providerID, String providerName, long reminderTime, long reminderFiredFor, long reminderID, int state, String extraInfo) {
      synchronized (this._eventLog) {
         ReminderRollingLog$ReminderLogEvent event = new ReminderRollingLog$ReminderLogEvent(
            this, code, providerID, providerName, reminderTime, reminderFiredFor, reminderID, state, extraInfo
         );
         if (this._position > this._eventLog.length - 1) {
            this._position = 0;
         }

         this._eventLog[this._position] = event;
         this._position++;
      }
   }

   public String[] getEvents() {
      String[] result = null;
      synchronized (this._eventLog) {
         if (this._eventLog.length > 0) {
            int num = this._eventLog.length;
            result = new String[num];
            int count = 0;
            int index = this._position - 1;

            for (int loop = 0; loop < num; loop++) {
               if (index < 0) {
                  index = num - 1;
               }

               if (this._eventLog[index] != null) {
                  result[count++] = this._eventLog[index].toString();
               }

               index--;
            }

            Array.resize(result, count);
         }

         return result;
      }
   }
}
