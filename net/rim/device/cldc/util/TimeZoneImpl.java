package net.rim.device.cldc.util;

import java.util.TimeZone;
import net.rim.vm.Memory;

final class TimeZoneImpl extends TimeZone {
   private String ID;
   private int startMonth;
   private int startDay;
   private int startDayOfWeek;
   private int startTime;
   private int endMonth;
   private int endDay;
   private int endDayOfWeek;
   private int endTime;
   private int rawOffset;
   private boolean useDaylight = false;
   private int startMode;
   private int endMode;
   private int dstSavings;
   private int _c_year;
   private int _c_ruleDayStart = -1;
   private int _c_ruleDayEnd = -1;
   private int serialSyncID;
   static final short NO_MODE;
   static final short DOW_WIM_MODE;
   static final short DOM_MODE;
   static final short DOW_GE_DOM_MODE;
   static final short DOW_LE_DOM_MODE;
   private static final int MILLIS_IN_MINUTE;
   private static final int ONE_MINUTE;
   private static final int ONE_HOUR;
   private static final int ONE_DAY;
   private static final int millisPerHour;
   private static final int millisPerDay;
   private static final byte[] staticMonthLength = new byte[]{31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

   TimeZoneImpl(
      String initZoneID,
      int initGMTOffset,
      int initDSTAmount,
      int initStartMode,
      int initStartMonth,
      int initStartDayOfWeek,
      int initStartDay,
      int initStartTime,
      int initEndMode,
      int initEndMonth,
      int initEndDayOfWeek,
      int initEndDay,
      int initEndTime,
      int initSerialSyncID,
      boolean observeDST
   ) {
      this.ID = initZoneID;
      Memory.createGroup(this.ID);
      this.rawOffset = initGMTOffset * 60000;
      this.dstSavings = initDSTAmount * 60000;
      this.useDaylight = initDSTAmount > 0;
      this.startMode = initStartMode;
      this.startMonth = initStartMonth;
      this.startDay = initStartDay;
      this.startDayOfWeek = initStartDayOfWeek;
      this.startTime = initStartTime * 60000;
      this.endMode = initEndMode;
      this.endMonth = initEndMonth;
      this.endDay = initEndDay;
      this.endDayOfWeek = initEndDayOfWeek;
      this.endTime = initEndTime * 60000;
      this.serialSyncID = initSerialSyncID;
      if (!observeDST) {
         this.useDaylight = false;
      }
   }

   @Override
   public final boolean equals(Object other) {
      return !(other instanceof TimeZoneImpl) ? false : ((TimeZoneImpl)other).rawOffset == this.rawOffset;
   }

   @Override
   public final String toString() {
      return this.ID;
   }

   @Override
   public final synchronized int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis) {
      int monthLength;
      try {
         monthLength = staticMonthLength[month];
      } catch (ArrayIndexOutOfBoundsException e) {
         throw new IllegalArgumentException("Illegal month");
      }

      if ((era == 0 || era == 1) && day >= 1 && day <= monthLength && dayOfWeek >= 1 && dayOfWeek <= 7 && millis >= 0 && millis < 86400000) {
         int result = this.rawOffset;
         if (this.useDaylight && year >= 0 && era == 1) {
            if (year != this._c_year) {
               this._c_ruleDayStart = -1;
               this._c_ruleDayEnd = -1;
               this._c_year = year;
            }

            boolean southern = this.startMonth > this.endMonth;
            int startCompare = this.compareToRule(
               month, monthLength, day, dayOfWeek, millis, this.startMode, this.startMonth, this.startDayOfWeek, this.startDay, this.startTime, !southern
            );
            int endCompare = 0;
            if (southern != startCompare >= 0) {
               millis += this.dstSavings;

               while (millis >= 86400000) {
                  millis -= 86400000;
                  day++;
                  dayOfWeek = 1 + dayOfWeek % 7;
                  if (day > monthLength) {
                     day = 1;
                     month++;
                  }
               }

               endCompare = this.compareToRule(
                  month, monthLength, day, dayOfWeek, millis, this.endMode, this.endMonth, this.endDayOfWeek, this.endDay, this.endTime, southern
               );
            }

            if (!southern && startCompare >= 0 && endCompare < 0 || southern && (startCompare >= 0 || endCompare < 0)) {
               result += this.dstSavings;
            }

            return result;
         } else {
            return result;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final int compareToRule(
      int month,
      int monthLen,
      int dayOfMonth,
      int dayOfWeek,
      int millis,
      int ruleMode,
      int ruleMonth,
      int ruleDayOfWeek,
      int ruleDay,
      int ruleMillis,
      boolean start
   ) {
      if (month < ruleMonth) {
         return -1;
      }

      if (month > ruleMonth) {
         return 1;
      }

      int ruleDayOfMonth;
      if (start) {
         ruleDayOfMonth = this._c_ruleDayStart;
      } else {
         ruleDayOfMonth = this._c_ruleDayEnd;
      }

      if (ruleDayOfMonth == -1) {
         switch (ruleMode) {
            case 0:
               break;
            case 1:
               if (ruleDay > 0) {
                  ruleDayOfMonth = 1 + (ruleDay - 1) * 7 + (7 + ruleDayOfWeek - (dayOfWeek - dayOfMonth + 1)) % 7;
               } else {
                  ruleDayOfMonth = monthLen + (ruleDay + 1) * 7 - (7 + (dayOfWeek + monthLen - dayOfMonth) - ruleDayOfWeek) % 7;
               }
               break;
            case 2:
            default:
               ruleDayOfMonth = ruleDay;
               break;
            case 3:
               ruleDayOfMonth = ruleDay + (49 + ruleDayOfWeek - ruleDay - dayOfWeek + dayOfMonth) % 7;
               break;
            case 4:
               ruleDayOfMonth = ruleDay - (49 - ruleDayOfWeek + ruleDay + dayOfWeek - dayOfMonth) % 7;
         }

         if (start) {
            this._c_ruleDayStart = ruleDayOfMonth;
         } else {
            this._c_ruleDayEnd = ruleDayOfMonth;
         }
      }

      if (dayOfMonth < ruleDayOfMonth) {
         return -1;
      } else if (dayOfMonth > ruleDayOfMonth) {
         return 1;
      } else if (millis < ruleMillis) {
         return -1;
      } else {
         return millis > ruleMillis ? 1 : 0;
      }
   }

   @Override
   public final int getRawOffset() {
      return this.rawOffset;
   }

   @Override
   public final boolean useDaylightTime() {
      return this.useDaylight;
   }

   @Override
   public final String getID() {
      return this.ID;
   }

   public final int getSerialSyncID() {
      return this.serialSyncID;
   }
}
