package net.rim.device.api.ui.component;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import net.rim.device.api.i18n.DateFormat;

class DateField$FormattedDate extends Date {
   private DateFormat _format;
   private Calendar _calendar;

   public DateField$FormattedDate(long date, DateFormat format, TimeZone zone) {
      super(date);
      this._format = format;
      this._calendar = Calendar.getInstance(zone);
      this._calendar.setTime(this);
   }

   @Override
   public void setTime(long time) {
      super.setTime(time);
      this._calendar.setTime(this);
   }

   @Override
   public String toString() {
      return this._format.format(this._calendar);
   }
}
