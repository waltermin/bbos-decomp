package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.cldc.util.CalendarExtensions;

final class TimeStringCache {
   private static final int MAX_HOURS;
   private static String[] _cache = new Object[48];
   private static DateFormat _timeFormat = DateFormat.getInstance(7);
   private static Calendar _gmtCal = Calendar.getInstance(TimeZone.getTimeZone(DateTimeUtilities.GMT));

   public static final void clearCache() {
      synchronized (_cache) {
         _cache = new Object[48];
      }
   }

   public static final String getString(Calendar cal) {
      int offset = 0;
      synchronized (_cache) {
         switch (cal.get(12)) {
            case 30:
               offset = 1;
            case 0:
               offset += cal.get(11) << 1;
               String result = _cache[offset];
               if (result == null) {
                  result = cacheTimeString(cal, offset);
               }

               return result;
            default:
               return _timeFormat.format(cal);
         }
      }
   }

   public static final String getString(int hour) {
      if (hour >= 0 && hour <= 24) {
         hour %= 24;
         String hourString = null;
         synchronized (_cache) {
            int offset = hour << 1;
            hourString = _cache[offset];
            if (hourString == null) {
               ((CalendarExtensions)_gmtCal).setTimeLong(hour * 3600000);
               hourString = cacheTimeString(_gmtCal, offset);
            }

            return hourString;
         }
      } else {
         return null;
      }
   }

   private static final String cacheTimeString(Calendar cal, int offset) {
      synchronized (_cache) {
         _cache[offset] = _timeFormat.format(cal);
         return _cache[offset];
      }
   }
}
