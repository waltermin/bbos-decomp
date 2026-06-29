package net.rim.device.apps.api.calendar.caldb;

import net.rim.device.api.util.CRC16;
import net.rim.device.api.util.Persistable;

public class CalendarKey implements Persistable {
   private long _calendarServiceID;
   private long _calendarFolderID;
   private int _hashCode;

   public CalendarKey(long calendarServiceID, long calendarFolderID) {
      this._calendarServiceID = calendarServiceID;
      this._calendarFolderID = calendarFolderID;
      this.generateHashCode();
   }

   public long getCalendarFolderID() {
      return this._calendarFolderID;
   }

   public long getCalendarServiceID() {
      return this._calendarServiceID;
   }

   private void generateHashCode() {
      String calendarServiceIDString = Long.toString(this._calendarServiceID);
      String calendarFolderIDString = Long.toString(this._calendarFolderID);
      int hashServiceID = CRC16.update(65535, calendarServiceIDString.getBytes());
      int hashFolderID = CRC16.update(65535, calendarFolderIDString.getBytes());
      int highPart = hashServiceID & 65535;
      int lowPart = hashFolderID & 65535;
      this._hashCode = highPart << 16 | lowPart;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof CalendarKey) {
         CalendarKey key = (CalendarKey)obj;
         if (key.getCalendarServiceID() == this._calendarServiceID && key.getCalendarFolderID() == this._calendarFolderID) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int hashCode() {
      return this._hashCode;
   }
}
