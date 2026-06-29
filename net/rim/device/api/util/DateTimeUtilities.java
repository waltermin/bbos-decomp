package net.rim.device.api.util;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.vm.Array;

public final class DateTimeUtilities {
   public static final long GUID_DATE_CHANGED;
   public static final long GUID_TIMEZONE_CHANGED;
   public static final int ONESECOND;
   public static final int ONEMINUTE;
   public static final int ONEHOUR;
   public static final int ONEDAY;
   public static String GMT = "GMT";
   private static final int[] DAYS_IN_MONTH = new int[]{
      31,
      28,
      31,
      30,
      31,
      30,
      31,
      31,
      30,
      31,
      30,
      31,
      -804913151,
      3342368,
      1129447424,
      1129447491,
      1129447497,
      1381105746,
      84,
      -805044220,
      1681002668,
      -804650992,
      2805,
      2923,
      2948,
      2949,
      5502,
      5503,
      5508,
      5511,
      5512,
      9200,
      9201,
      9202,
      9203,
      9207,
      49996,
      49999,
      555417856,
      1661009927,
      529165580,
      1916883543,
      -1370457650,
      16803175,
      -345171610,
      1929445491,
      7618858,
      12956929
   };

   private DateTimeUtilities() {
   }

   public static final boolean isWeekend() {
      return isWeekend(System.currentTimeMillis());
   }

   public static final boolean isWeekend(long date) {
      if (date < 0) {
         throw new IllegalArgumentException();
      }

      Calendar cal = Calendar.getInstance();
      ((CalendarExtensions)cal).setTimeLong(date);
      return isWeekend(cal);
   }

   public static final boolean isWeekend(Calendar cal) {
      int day = cal.get(7);
      return day == 7 || day == 1;
   }

   public static final Calendar getNextDate(int millisSinceMidnight) {
      if (millisSinceMidnight < 0) {
         throw new IllegalArgumentException();
      }

      Calendar c = getDate(millisSinceMidnight);
      long time = ((CalendarExtensions)c).getTimeLong();
      if (time < System.currentTimeMillis()) {
         ((CalendarExtensions)c).add(5, 1);
      }

      return c;
   }

   public static final Calendar getDate(int millisSinceMidnight) {
      if (millisSinceMidnight < 0) {
         throw new IllegalArgumentException();
      }

      int hoursSinceMidnight = millisSinceMidnight / 3600000;
      int minutesSinceMidnight = (millisSinceMidnight - hoursSinceMidnight * 3600000) / 60000;
      Calendar c = Calendar.getInstance();
      c.set(11, hoursSinceMidnight);
      c.set(12, minutesSinceMidnight);
      c.set(13, 0);
      c.set(14, 0);
      return c;
   }

   public static final void formatElapsedTime(long time, StringBuffer sb, boolean reset) {
      if (time < 0) {
         throw new IllegalArgumentException();
      }

      if (reset) {
         sb.setLength(0);
      }

      int seconds = (int)(time % 60);
      time /= 60;
      int minutes = (int)(time % 60);
      time /= 60;
      int hours = (int)(time % 24);
      time /= 24;
      int days = (int)time;
      if (days != 0) {
         sb.append(days);
         sb.append(':');
         if (hours < 10) {
            sb.append('0');
         }
      }

      if (days != 0 || hours != 0) {
         sb.append(hours);
         sb.append(':');
         if (minutes < 10) {
            sb.append('0');
         }
      }

      sb.append(minutes);
      sb.append(':');
      if (seconds < 10) {
         sb.append('0');
      }

      sb.append(seconds);
   }

   public static final int[] getCalendarFields(Calendar cal, int[] fields) {
      if (fields == null) {
         fields = new int[7];
      }

      if (fields.length < 7) {
         Array.resize(fields, 7);
      }

      fields[0] = cal.get(1);
      fields[1] = cal.get(2);
      fields[2] = cal.get(5);
      fields[3] = cal.get(11);
      fields[4] = cal.get(12);
      fields[5] = cal.get(13);
      fields[6] = cal.get(14);
      return fields;
   }

   public static final void setCalendarFields(Calendar cal, int[] fields) {
      if (fields != null && fields.length >= 7) {
         cal.set(1, fields[0]);
         cal.set(2, fields[1]);
         cal.set(5, fields[2]);
         cal.set(11, fields[3]);
         cal.set(12, fields[4]);
         cal.set(13, fields[5]);
         cal.set(14, fields[6]);
      }
   }

   public static final boolean isSameDate(long date1, long date2, TimeZone tz1, TimeZone tz2) {
      return isSameDate(date1, date2, tz1, tz2, null);
   }

   public static final boolean isSameDate(long date1, long date2) {
      TimeZone tzDefault = TimeZone.getDefault();
      return isSameDate(date1, date2, tzDefault, tzDefault, null);
   }

   public static final boolean isSameDate(long date1, long date2, TimeZone tz1, TimeZone tz2, Calendar cal) {
      boolean quickCheck = tz1 == null || tz2 == null;
      if (tz1 != null && tz2 != null) {
         quickCheck = tz1 == tz2 || tz1.getID().equals(tz2.getID());
      }

      if (quickCheck) {
         long diff = date1 - date2;
         if (diff < 0) {
            diff *= -1;
         }

         if (diff > 90000000) {
            return false;
         }
      }

      if (tz1 == null) {
         tz1 = TimeZone.getDefault();
      }

      Calendar calendar = cal;
      if (calendar == null) {
         calendar = Calendar.getInstance();
      }

      calendar.setTimeZone(tz1);
      ((CalendarExtensions)calendar).setTimeLong(date1);
      int date = calendar.get(5);
      int month = calendar.get(2);
      int year = calendar.get(1);
      if (tz2 == null) {
         tz2 = tz1;
      }

      calendar.setTimeZone(tz2);
      ((CalendarExtensions)calendar).setTimeLong(date2);
      return calendar.get(5) == date && calendar.get(2) == month && calendar.get(1) == year;
   }

   public static final long convertEpochToMilliseconds(int syncTime) {
      return ((long)syncTime - 36816480) * 60000;
   }

   public static final int convertMillisecondsToEpoch(long epochTime) {
      return (int)(epochTime / 60000 + 36816480);
   }

   public static final long copyCalendar(Calendar source, Calendar destination) {
      destination.set(1, source.get(1));
      destination.set(2, source.get(2));
      destination.set(5, source.get(5));
      destination.set(11, source.get(11));
      destination.set(12, source.get(12));
      destination.set(13, source.get(13));
      destination.set(14, source.get(14));
      return ((CalendarExtensions)destination).getTimeLong();
   }

   public static final void zeroCalendarTime(Calendar calendar) {
      calendar.set(11, 0);
      calendar.set(12, 0);
      calendar.set(13, 0);
      calendar.set(14, 0);
   }

   public static final int getWeekOfYear(Calendar calendar, long date, int firstDayofWeek) {
      ((CalendarExtensions)calendar).setTimeLong(date);
      int dayOfYear = calendar.get(6);
      int year = calendar.get(1);
      boolean isLeapYear = year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
      boolean isPreviousYearLeapYear = (year - 1) % 4 == 0 && (year - 1) % 100 != 0 || (year - 1) % 400 == 0;
      int YY = (year - 1) % 100;
      int C = year - 1 - YY;
      int G = YY + YY / 4;
      int jan1Weekday = 1 + (C / 100 % 4 * 5 + G) % 7;
      int H = dayOfYear + (jan1Weekday - 1);
      int weekday = 1 + (H - 1) % 7;
      int yearNumber = year;
      int weekNumber = 0;
      if (dayOfYear <= 8 - jan1Weekday && jan1Weekday > 4) {
         yearNumber = year - 1;
         if (jan1Weekday != 5 && (jan1Weekday != 6 || !isPreviousYearLeapYear)) {
            weekNumber = 52;
         } else {
            weekNumber = 53;
         }
      }

      if (yearNumber == year) {
         int I = 365;
         if (isLeapYear) {
            I = 366;
         }

         if (I - dayOfYear < 4 - weekday) {
            yearNumber = year + 1;
            weekNumber = 1;
         }
      }

      if (yearNumber == year) {
         int J = dayOfYear + (7 - weekday) + (jan1Weekday - 1);
         weekNumber = J / 7;
         if (jan1Weekday > 4) {
            weekNumber--;
         }
      }

      return weekNumber;
   }

   public static final int getNumberOfDaysInMonth(int month, int year) {
      int days = -1;
      boolean isLeapYear = year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
      if (month == 1) {
         return isLeapYear ? 29 : 28;
      }

      if (month >= 0 && month < DAYS_IN_MONTH.length) {
         days = DAYS_IN_MONTH[month];
      }

      return days;
   }

   public static final boolean verifyStartOfDay(Calendar cal, CalendarExtensions calEx) {
      if (cal.get(11) == 23) {
         calEx.add(10, 1);
         return true;
      } else if (cal.get(11) == 1) {
         zeroCalendarTime(cal);
         return true;
      } else {
         return false;
      }
   }

   public static final boolean calendarAddWithDst(Calendar cal, CalendarExtensions calEx, int field, int amount) {
      boolean wasModified = false;
      switch (field) {
         case 1:
         case 3:
         case 4:
            calEx.add(field, amount);
            break;
         case 2:
         case 5:
         case 6:
         case 7:
         default:
            int originalHour = cal.get(11);
            calEx.add(field, amount);
            if (originalHour == 0 && cal.get(11) == 23 && amount > 0) {
               calEx.add(10, 1);
               wasModified = true;
            }
      }

      return wasModified;
   }

   public static final boolean calendarSetWithDst(Calendar cal, CalendarExtensions calEx, int field, int value) {
      boolean wasModified = false;
      switch (field) {
         case 2:
         case 5:
         case 6:
         case 7:
            int originalHour = cal.get(11);
            cal.set(field, value);
            if (originalHour == 0 && cal.get(11) == 23) {
               calEx.add(11, 1);
               wasModified = true;
            }
            break;
         case 11:
            cal.set(field, value);
            int newHour = cal.get(11);
            if (value % 24 == 0 && newHour == 23) {
               calEx.add(11, 1);
               wasModified = true;
            }
            break;
         default:
            cal.set(field, value);
      }

      return wasModified;
   }
}
