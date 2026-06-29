package net.rim.device.api.i18n;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import net.rim.device.cldc.util.CalendarExtensions;

public class DateFormat extends Format {
   public static final long GUID_DATE_FORMAT_CHANGED;
   private static Calendar _cal = Calendar.getInstance();
   private static CalendarExtensions _calEx = (CalendarExtensions)_cal;
   static final int DATE;
   static final int TIME;
   public static final int DATE_FULL;
   public static final int DATE_LONG;
   public static final int DATE_MEDIUM;
   public static final int DATE_SHORT;
   public static final int DATE_DEFAULT;
   public static final int TIME_FULL;
   public static final int TIME_LONG;
   public static final int TIME_MEDIUM;
   public static final int TIME_SHORT;
   public static final int TIME_DEFAULT;
   public static final int DATETIME_DEFAULT;
   public static final int MILLISECOND_FIELD;
   public static final int SECOND_FIELD;
   public static final int MINUTE_FIELD;
   public static final int HOUR_FIELD;
   public static final int HOUR_OF_DAY_FIELD;
   public static final int AM_PM_FIELD;
   public static final int DATE_FIELD;
   public static final int MONTH_FIELD;
   public static final int YEAR_FIELD;
   public static final int ERA_FIELD;
   public static final int DAY_OF_WEEK_FIELD;
   public static final int TIMEZONE_FIELD;

   protected DateFormat() {
   }

   public StringBuffer format(Calendar _1, StringBuffer _2, FieldPosition _3) {
      throw null;
   }

   @Override
   public StringBuffer format(Object obj, StringBuffer toAppendTo_o, FieldPosition pos_io) {
      if (!(obj instanceof Date)) {
         return this.format((Calendar)obj, toAppendTo_o, pos_io);
      }

      Date date = (Date)obj;
      synchronized (_cal) {
         _cal.setTime(date);
         return this.format(_cal, toAppendTo_o, pos_io);
      }
   }

   public static final DateFormat getInstance(int style) {
      return new SimpleDateFormat(style);
   }

   public static long alignToMidnight(long date) {
      return alignToHourMinute(date, 23, 59);
   }

   public static long alignToHourMinute(long date, int hour, int minute) {
      synchronized (_cal) {
         _cal.setTimeZone(TimeZone.getDefault());
         _calEx.setTimeLong(date);
         _cal.set(11, hour);
         _cal.set(12, minute);
         _cal.set(13, 0);
         _cal.set(14, 0);
         return _calEx.getTimeLong();
      }
   }

   public final StringBuffer formatLocal(StringBuffer sb, long date) {
      if (sb == null) {
         sb = new StringBuffer(32);
      }

      synchronized (_cal) {
         _cal.setTimeZone(TimeZone.getDefault());
         _calEx.setTimeLong(date);
         this.format(_cal, sb, null);
         return sb;
      }
   }

   public final String formatLocal(long date) {
      synchronized (_cal) {
         _cal.setTimeZone(TimeZone.getDefault());
         _calEx.setTimeLong(date);
         return this.format(_cal);
      }
   }
}
