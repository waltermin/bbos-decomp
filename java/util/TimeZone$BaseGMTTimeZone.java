package java.util;

final class TimeZone$BaseGMTTimeZone extends TimeZone {
   private static String GMT_DESC = "(GMT)";

   private TimeZone$BaseGMTTimeZone() {
   }

   @Override
   public final int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis) {
      return 0;
   }

   public final int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis, int monthLength) {
      return 0;
   }

   @Override
   public final int getRawOffset() {
      return 0;
   }

   @Override
   public final boolean useDaylightTime() {
      return false;
   }

   @Override
   public final String getID() {
      return TimeZone.GMT_STRING;
   }

   @Override
   public final String toString() {
      return GMT_DESC;
   }

   @Override
   public final boolean equals(Object obj) {
      return obj instanceof TimeZone$BaseGMTTimeZone;
   }

   TimeZone$BaseGMTTimeZone(TimeZone$1 x0) {
      this();
   }
}
