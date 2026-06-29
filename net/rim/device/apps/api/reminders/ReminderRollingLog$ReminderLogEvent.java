package net.rim.device.apps.api.reminders;

import java.util.TimeZone;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.vm.Persistable;

final class ReminderRollingLog$ReminderLogEvent implements Persistable {
   private int _currentTime;
   private int _code;
   private long _providerID;
   private int _reminderTime;
   private int _reminderFiredFor;
   private long _reminderID;
   private int _state;
   private String _extraInfo;
   private String _providerName;
   private final ReminderRollingLog this$0;

   public ReminderRollingLog$ReminderLogEvent(
      ReminderRollingLog _1,
      int code,
      long providerID,
      String providerName,
      long reminderTime,
      long reminderFiredFor,
      long reminderID,
      int state,
      String extraInfo
   ) {
      this.this$0 = _1;
      this._currentTime = -1;
      this._code = -1;
      this._providerID = -1;
      this._reminderTime = -1;
      this._reminderFiredFor = -1;
      this._reminderID = -1;
      this._state = -1;
      this._extraInfo = null;
      this._providerName = null;
      this._currentTime = (int)(System.currentTimeMillis() / 1000);
      this._code = code;
      this._providerID = providerID;
      if (reminderTime == -1) {
         reminderTime = -1;
      } else {
         this._reminderTime = (int)(reminderTime / 1000);
      }

      if (reminderFiredFor == -1) {
         this._reminderFiredFor = -1;
      } else {
         this._reminderFiredFor = (int)(reminderFiredFor / 1000);
      }

      this._reminderID = reminderID;
      this._state = state;
      this._extraInfo = extraInfo;
      this._providerName = providerName;
   }

   @Override
   public final String toString() {
      synchronized (ReminderRollingLog._cal) {
         ReminderRollingLog._cal.setTimeZone(TimeZone.getDefault());
         CalendarExtensions calEx = (CalendarExtensions)ReminderRollingLog._cal;
         calEx.setTimeLong((long)this._currentTime * 1000);
         ReminderRollingLog._scratchBuffer.setLength(0);
         ReminderRollingLog._currentTimeFormat.format(ReminderRollingLog._cal, ReminderRollingLog._scratchBuffer, null);
         this.codeToString(this._code, ReminderRollingLog._scratchBuffer);
         String msg = ((StringBuffer)(new Object())).append(ReminderRollingLog._scratchBuffer.toString()).append(":").toString();
         if (this._providerName != null) {
            msg = ((StringBuffer)(new Object())).append(msg).append(this._providerName).toString();
         }

         if (this._reminderTime != -1) {
            ReminderRollingLog._scratchBuffer.setLength(0);
            calEx.setTimeLong((long)this._reminderTime * 1000);
            ReminderRollingLog._eventTimeFormat.format(ReminderRollingLog._cal, ReminderRollingLog._scratchBuffer, null);
            msg = ((StringBuffer)(new Object())).append(msg).append(":").append(ReminderRollingLog._scratchBuffer.toString()).toString();
         }

         if (this._reminderFiredFor != -1) {
            ReminderRollingLog._scratchBuffer.setLength(0);
            calEx.setTimeLong((long)this._reminderFiredFor * 1000);
            ReminderRollingLog._eventTimeFormat.format(ReminderRollingLog._cal, ReminderRollingLog._scratchBuffer, null);
            msg = ((StringBuffer)(new Object())).append(msg).append(":").append(ReminderRollingLog._scratchBuffer.toString()).toString();
         }

         if (this._state != -1) {
            msg = ((StringBuffer)(new Object())).append(msg).append(":").append(this._state).toString();
         }

         if (this._reminderID != -1) {
            msg = ((StringBuffer)(new Object())).append(msg).append(":").append(this._reminderID).toString();
         }

         if (this._extraInfo != null) {
            msg = ((StringBuffer)(new Object())).append(msg).append(":").append(this._extraInfo).toString();
         }

         return msg;
      }
   }

   private final void codeToString(int code, StringBuffer buffer) {
      int mask = -16777216;
      char ch = '\u0000';

      for (int i = 3; i >= 0; i--) {
         ch = (char)((code & mask) >> i * 8 & 0xFF);
         mask >>= 8;
         buffer.append(ch);
      }
   }
}
