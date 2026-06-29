package net.rim.device.apps.api.calendar.caldb;

import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;

public class UserCalendarServiceKey implements Persistable {
   String _calendarName;

   public UserCalendarServiceKey(String calendarName) {
      this._calendarName = calendarName;
   }

   @Override
   public boolean equals(Object o) {
      boolean result = false;
      if (o instanceof UserCalendarServiceKey) {
         UserCalendarServiceKey ucs = (UserCalendarServiceKey)o;
         String calname = ucs._calendarName;
         if (calname != null && this._calendarName != null) {
            result = StringUtilities.strEqualIgnoreCase(this._calendarName, calname, 1701707776);
         }
      }

      return result;
   }

   @Override
   public String toString() {
      return this._calendarName;
   }
}
