package net.rim.device.apps.internal.calendar.eventprovider;

import net.rim.device.apps.api.calendar.caldb.CalendarKey;

class CalendarChoice {
   private CalendarKey _calendarKey;
   private String _calendarDescription;
   private int _color;

   CalendarChoice(CalendarKey calendarKey, String calendarDescription, int color) {
      this._calendarKey = calendarKey;
      this._calendarDescription = calendarDescription;
      this._color = color;
   }

   CalendarKey getCalendarKey() {
      return this._calendarKey;
   }

   String getCalendarDescription() {
      return this._calendarDescription;
   }

   int getColor() {
      return this._color;
   }

   @Override
   public String toString() {
      return this.getCalendarDescription();
   }
}
