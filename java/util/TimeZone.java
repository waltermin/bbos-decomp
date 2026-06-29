package java.util;

import net.rim.device.cldc.util.TimeService;

public class TimeZone {
   private static String GMT_STRING = "GMT";
   private static TimeZone$BaseGMTTimeZone _gmtZone = new TimeZone$BaseGMTTimeZone(null);

   public int getOffset(int _1, int _2, int _3, int _4, int _5, int _6) {
      throw null;
   }

   public int getRawOffset() {
      throw null;
   }

   public boolean useDaylightTime() {
      throw null;
   }

   public String getID() {
      return null;
   }

   public static TimeZone getTimeZone(String ID) {
      if (ID.equals(GMT_STRING)) {
         return _gmtZone;
      }

      TimeZone tz = null;
      TimeService ts = TimeService.getTimeService();
      if (ts != null) {
         tz = ts.getTimeZone(ID);
      }

      if (tz == null) {
         tz = _gmtZone;
      }

      return tz;
   }

   public static TimeZone getDefault() {
      TimeService ts = TimeService.getTimeService();
      if (ts == null) {
         return _gmtZone;
      }

      TimeZone tz = ts.getTimeZone(ts.getDefaultTimeZoneID());
      return tz == null ? _gmtZone : tz;
   }

   public static String[] getAvailableIDs() {
      TimeService ts = TimeService.getTimeService();
      return ts == null ? new String[]{GMT_STRING} : ts.getTimeZoneIDs();
   }
}
