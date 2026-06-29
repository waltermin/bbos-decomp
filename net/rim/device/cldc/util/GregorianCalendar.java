package net.rim.device.cldc.util;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.i18n.DateFormatSymbols;

public final class GregorianCalendar extends Calendar implements CalendarExtensions {
   private int _eraVal;
   private int _eraStamp;
   private int _yearVal;
   private int _yearStamp;
   private int _monthVal;
   private int _monthStamp;
   private int _dateVal;
   private int _dateStamp;
   private int _doyVal;
   private int _doyStamp;
   private int _dowVal;
   private int _dowStamp;
   private int _ampmVal;
   private int _ampmStamp;
   private int _hourVal;
   private int _hourStamp;
   private int _hourOfDayVal;
   private int _hourOfDayStamp;
   private int _minVal;
   private int _minStamp;
   private int _secVal;
   private int _secStamp;
   private int _milliVal;
   private int _milliStamp;
   private boolean _isTimeSet;
   private boolean _areFieldsSet;
   private int _zoneOffset;
   private int _dstOffset;
   private int _nextStamp = 2;
   private static final int ACTUAL_MINIMUM_MASK = 226;
   private static final int UNSET = 0;
   private static final int INTERNALLY_SET = 1;
   private static final int MINIMUM_USER_STAMP = 2;
   private static final int GREGORIAN_CUTOVER_YEAR = 1582;
   private static final int[] MONTH_LENGTH = new int[]{
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
      -804650996,
      31,
      29,
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
      -805044212,
      505355551,
      522133023,
      522067742,
      51,
      4408146,
      4801362,
      5391186,
      5526098,
      -805044096,
      1234518860,
      2049212631,
      1777570406,
      2140414811,
      1298472420,
      -521147027,
      1418454475,
      1438191017,
      1118189862,
      1955083787,
      -1645491194,
      -1786283218,
      759841631
   };
   private static final int[] LEAP_MONTH_LENGTH = new int[]{
      31,
      29,
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
      -805044212,
      505355551,
      522133023,
      522067742,
      51,
      4408146,
      4801362,
      5391186,
      5526098,
      -805044096,
      1234518860,
      2049212631,
      1777570406,
      2140414811,
      1298472420,
      -521147027,
      1418454475,
      1438191017,
      1118189862,
      1955083787,
      -1645491194,
      -1786283218,
      759841631,
      1860705112,
      -1994617363,
      -1618316449,
      1309619627,
      1199574529,
      -466156065,
      -21253724,
      460597610,
      1748995976,
      -1486544065,
      -175135758,
      1933597168,
      -790102144
   };
   private static final int ONE_SECOND = 1000;
   private static final int ONE_MINUTE = 60000;
   private static final int ONE_HOUR = 3600000;
   private static final long ONE_DAY = 86400000L;
   private static final long ONE_WEEK = 604800000L;
   private static final int[] MIN_VALUES = new int[]{
      0,
      1,
      0,
      1,
      0,
      1,
      1,
      1,
      -1,
      0,
      0,
      0,
      0,
      0,
      0,
      -43200000,
      0,
      -804781991,
      1114112,
      3080223,
      5177408,
      7405666,
      9568383,
      12189863,
      14090440,
      16711914,
      18940173,
      20971825,
      22806862,
      24838508,
      27066763,
      29295019,
      31392208,
      34013684,
      35193366,
      36897318,
      38928963,
      41878113,
      43516558,
      45286053,
      46858943,
      48431830,
      50135794,
      51839755,
      53478180,
      54919995,
      56296271,
      57803624,
      59769726,
      61408157,
      62915510,
      64422859,
      66257891,
      68289537,
      69993505,
      71435320,
      72877132,
      75105384,
      77071500,
      79168680,
      81003720,
      82838752,
      1281,
      -804781288,
      -47185920,
      65536,
      0,
      1
   };
   private static final int[] MAX_VALUES = new int[]{
      1,
      292278994,
      11,
      53,
      6,
      31,
      366,
      7,
      6,
      1,
      11,
      23,
      59,
      59,
      999,
      43200000,
      3600000,
      -805044221,
      65537,
      -804650986,
      20,
      14,
      12,
      23,
      22,
      10,
      13,
      5,
      21,
      15,
      3,
      2,
      16,
      7,
      17,
      8,
      11,
      25,
      19,
      27,
      29,
      31,
      -804650996,
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
      -804650996,
      31,
      29,
      31,
      30,
      31,
      30,
      31,
      31,
      30,
      31,
      30,
      31
   };

   public final int internalGet(int field, boolean performComplete) {
      if (performComplete) {
         this.complete();
      }

      switch (field) {
         case -1:
         case 3:
         case 4:
         case 8:
         case 15:
            if (performComplete) {
               throw new ArrayIndexOutOfBoundsException(field);
            }

            return 0;
         case 0:
         default:
            return this._eraVal;
         case 1:
            return this._yearVal;
         case 2:
            return this._monthVal;
         case 5:
            return this._dateVal;
         case 6:
            return this._doyVal;
         case 7:
            return this._dowVal;
         case 9:
            return this._ampmVal;
         case 10:
            return this._hourVal;
         case 11:
            return this._hourOfDayVal;
         case 12:
            return this._minVal;
         case 13:
            return this._secVal;
         case 14:
            return this._milliVal;
         case 16:
            return this._dstOffset;
      }
   }

   public final void internalSet(int field, int value) {
      this._isTimeSet = false;
      this._areFieldsSet = false;
      int nextStamp = this._nextStamp;
      switch (field) {
         case -1:
         case 3:
         case 4:
         case 8:
            throw new ArrayIndexOutOfBoundsException(field);
         case 0:
         default:
            this._eraVal = value;
            this._eraStamp = nextStamp;
            break;
         case 1:
            this._yearVal = value;
            this._yearStamp = nextStamp;
            break;
         case 2:
            this._monthVal = value;
            this._monthStamp = nextStamp;
            break;
         case 5:
            this._dateVal = value;
            this._dateStamp = nextStamp;
            break;
         case 6:
            this._doyVal = value;
            this._doyStamp = nextStamp;
            break;
         case 7:
            this._dowVal = value;
            this._dowStamp = nextStamp;
            break;
         case 9:
            this._ampmVal = value;
            this._ampmStamp = nextStamp;
            break;
         case 10:
            this._hourVal = value;
            this._hourStamp = nextStamp;
            break;
         case 11:
            this._hourOfDayVal = value;
            this._hourOfDayStamp = nextStamp;
            break;
         case 12:
            this._minVal = value;
            this._minStamp = nextStamp;
            break;
         case 13:
            this._secVal = value;
            this._secStamp = nextStamp;
            break;
         case 14:
            this._milliVal = value;
            this._milliStamp = nextStamp;
      }

      this._nextStamp = nextStamp + 1;
   }

   @Override
   public final int getActualMaximum(int field) {
      int max = 0;
      switch (field) {
         case 1:
         default:
            return 9999;
         case 2:
            return 11;
         case 5:
            if (this.isLeapYear(this._yearVal)) {
               return LEAP_MONTH_LENGTH[this._monthVal];
            }

            return MONTH_LENGTH[this._monthVal];
         case 7:
            return 6;
         case 9:
            return 1;
         case 10:
            return 11;
         case 11:
            return 23;
         case 12:
         case 13:
            return 59;
         case 14:
            max = 1000;
         case 0:
         case 3:
         case 4:
         case 6:
         case 8:
            return max;
      }
   }

   @Override
   public final void add(int field, int amount) {
      if (amount != 0) {
         this.complete();
         if (field == 1) {
            int year = this._yearVal;
            if (this._eraVal == 1) {
               year += amount;
               if (year > 0) {
                  this.set(1, year);
               } else {
                  this.set(1, 1 - year);
                  this.set(0, 0);
               }
            } else {
               year -= amount;
               if (year > 0) {
                  this.set(1, year);
               } else {
                  this.set(1, 1 - year);
                  this.set(0, 1);
               }
            }

            this.pinDayOfMonth();
         } else if (field == 2) {
            int month = this._monthVal + amount;
            if (month >= 0) {
               this.set(1, this._yearVal + month / 12);
               this.set(2, month % 12);
            } else {
               this.set(1, this._yearVal + (month + 1) / 12 - 1);
               month %= 12;
               if (month < 0) {
                  month += 12;
               }

               this.set(2, 0 + month);
            }

            this.pinDayOfMonth();
         } else if (field == 0) {
            int era = this._eraVal + amount;
            if (era < 0) {
               era = 0;
            }

            if (era > 1) {
               era = 1;
            }

            this.set(0, era);
         } else {
            long delta = amount;
            boolean adjustDST = true;
            switch (field) {
               case 4:
               case 8:
                  throw new IllegalArgumentException();
               case 5:
               case 6:
               case 7:
                  delta *= 86400000;
                  break;
               case 9:
               default:
                  delta *= 43200000;
                  break;
               case 10:
               case 11:
                  delta *= 3600000;
                  adjustDST = false;
                  break;
               case 12:
                  delta *= 60000;
                  adjustDST = false;
                  break;
               case 13:
                  delta *= 1000;
                  adjustDST = false;
                  break;
               case 14:
                  adjustDST = false;
            }

            long dst = 0;
            if (adjustDST) {
               dst = this._dstOffset;
            }

            this.setTimeInMillis(super.time + delta);
            if (adjustDST) {
               dst -= this._dstOffset;
               if (dst != 0) {
                  this.setTimeInMillis(super.time + dst);
               }
            }
         }
      }
   }

   @Override
   public final void roll(int field, int amount) {
      if (amount != 0) {
         int min = 0;
         int max = 0;
         if (field >= 0) {
            this.complete();
            min = this.getMinimum(field);
            max = this.getMaximum(field);
         }

         switch (field) {
            case -1:
            case 3:
            case 4:
            case 8:
               throw new IllegalArgumentException();
            case 2:
               int mon = (this._monthVal + amount) % 12;
               if (mon < 0) {
                  mon += 12;
               }

               this.set(2, mon);
               int monthLen = this.monthLength(mon);
               int dom = this._dateVal;
               if (dom > monthLen) {
                  this.set(5, monthLen);
               }

               return;
            case 5:
               max = this.monthLength(this._monthVal);
            case 0:
            case 1:
            case 9:
            case 12:
            case 13:
            case 14:
               int gap = max - min + 1;
               int value = this.internalGet(field, false) + amount;
               value = (value - min) % gap;
               if (value < 0) {
                  value += gap;
               }

               value += min;
               this.set(field, value);
               return;
            case 6:
               long delta = (long)amount * 86400000;
               long min2 = super.time - (long)(this._doyVal - 1) * 86400000;
               int yearLength = this.yearLength();
               super.time = (super.time + delta - min2) % ((long)yearLength * 86400000);
               if (super.time < 0) {
                  super.time += (long)yearLength * 86400000;
               }

               this.setTimeInMillis(super.time + min2);
               return;
            case 7:
               long delta = (long)amount * 86400000;
               int leadDays = this._dowVal - 1;
               if (leadDays < 0) {
                  leadDays += 7;
               }

               long min2 = super.time - (long)leadDays * 86400000;
               super.time = (super.time + delta - min2) % 604800000;
               if (super.time < 0) {
                  super.time += 604800000;
               }

               this.setTimeInMillis(super.time + min2);
               return;
            case 10:
            case 11:
            default:
               long start = this.getTimeInMillis();
               int oldHour = this.internalGet(field, false);
               int newHour = (oldHour + amount) % (max + 1);
               if (newHour < 0) {
                  newHour += max + 1;
               }

               this.setTimeInMillis(start + 3600000 * (newHour - oldHour));
         }
      }
   }

   @Override
   public final int getActualMinimum(int field) {
      return 226 >> field & 1;
   }

   @Override
   public final void setTimeLong(long millis) {
      this.setTimeInMillis(millis);
   }

   @Override
   public final long getTimeLong() {
      return this.getTimeInMillis();
   }

   @Override
   protected final void computeFields() {
      TimeZone tz = this.getTimeZone();
      int rawOffset = tz.getRawOffset();
      long localMillis = super.time + rawOffset;
      if (super.time > 0 && localMillis < 0 && rawOffset > 0) {
         localMillis = Long.MAX_VALUE;
      } else if (super.time < 0 && localMillis > 0 && rawOffset < 0) {
         localMillis = Long.MIN_VALUE;
      }

      int millisInDay = this.timeToDateFields(localMillis);
      int dstOffset = tz.getOffset(this.internalGetEra(), this._yearVal, this._monthVal, this._dateVal, this._dowVal, millisInDay) - rawOffset;
      this._dstOffset = dstOffset;
      this._zoneOffset = rawOffset;
      millisInDay += dstOffset;
      if (millisInDay >= 86400000) {
         long dstMillis = localMillis + dstOffset;
         if (localMillis > 0 && dstMillis < 0 && dstOffset > 0) {
            dstMillis = Long.MAX_VALUE;
         } else if (localMillis < 0 && dstMillis > 0 && dstOffset < 0) {
            dstMillis = Long.MIN_VALUE;
         }

         millisInDay = this.timeToDateFields(dstMillis);
      }

      this.timeToRemainingFields(millisInDay);
   }

   @Override
   protected final void computeTime() {
      TimeZone tz = this.getTimeZone();
      int zoneOffset = tz.getRawOffset();
      int normalizedMillisInDay = this.fieldsToLocalTime();
      int dstOffset = 0;
      dstOffset = tz.getOffset(this._eraVal, this._yearVal, this._monthVal, this._dateVal, this._dowVal, normalizedMillisInDay) - zoneOffset;
      this._dstOffset = dstOffset;
      this._zoneOffset = zoneOffset;
      super.time = super.time - zoneOffset - dstOffset;
   }

   @Override
   protected final long getTimeInMillis() {
      if (!this._isTimeSet) {
         this.updateTime();
      }

      return super.time;
   }

   @Override
   protected final void setTimeInMillis(long millis) {
      this._isTimeSet = true;
      super.time = millis;
      this.computeFields();
      this._areFieldsSet = true;
   }

   @Override
   public final void setTimeZone(TimeZone value) {
      if (this.getTimeZone() != value) {
         super.setTimeZone(value);
         this._areFieldsSet = false;
      }
   }

   private final void updateTime() {
      this.computeTime();
      this._areFieldsSet = false;
      this._isTimeSet = true;
   }

   private final boolean isSet(int field) {
      switch (field) {
         case -1:
         case 3:
         case 4:
         case 8:
            throw new IllegalArgumentException();
         case 0:
         default:
            if (this._eraStamp != 0) {
               return true;
            }

            return false;
         case 1:
            if (this._yearStamp != 0) {
               return true;
            }

            return false;
         case 2:
            if (this._monthStamp != 0) {
               return true;
            }

            return false;
         case 5:
            if (this._dateStamp != 0) {
               return true;
            }

            return false;
         case 6:
            if (this._doyStamp != 0) {
               return true;
            }

            return false;
         case 7:
            if (this._dowStamp != 0) {
               return true;
            }

            return false;
         case 9:
            if (this._ampmStamp != 0) {
               return true;
            }

            return false;
         case 10:
            if (this._hourStamp != 0) {
               return true;
            }

            return false;
         case 11:
            if (this._hourOfDayStamp != 0) {
               return true;
            }

            return false;
         case 12:
            if (this._minStamp != 0) {
               return true;
            }

            return false;
         case 13:
            if (this._secStamp != 0) {
               return true;
            }

            return false;
         case 14:
            return this._milliStamp != 0;
      }
   }

   private final void complete() {
      if (!this._isTimeSet) {
         this.updateTime();
      }

      if (!this._areFieldsSet) {
         this.computeFields();
         this._areFieldsSet = true;
      }
   }

   private final boolean isLeapYear(int year) {
      return year < 1582 ? year % 4 == 0 : year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
   }

   private final int monthLength(int month, int year) {
      return this.isLeapYear(year) ? LEAP_MONTH_LENGTH[month] : MONTH_LENGTH[month];
   }

   private final int monthLength(int month) {
      int year = this._yearVal;
      if (this.internalGetEra() == 0) {
         year = 1 - year;
      }

      return this.monthLength(month, year);
   }

   private final int yearLength() {
      return this.isLeapYear(this._yearVal) ? 366 : 365;
   }

   private final void pinDayOfMonth() {
      int monthLen = this.monthLength(this._monthVal);
      int dom = this._dateVal;
      if (dom > monthLen) {
         this.set(5, monthLen);
      }
   }

   private final int internalGetEra() {
      return this.isSet(0) ? this._eraVal : 1;
   }

   private final int getMinimum(int field) {
      return MIN_VALUES[field];
   }

   private final int getMaximum(int field) {
      return MAX_VALUES[field];
   }

   private final native int fieldsToLocalTime();

   private final native int timeToDateFields(long var1);

   private final native void timeToRemainingFields(int var1);

   public static final String toString(Calendar calendar) {
      if (calendar == null) {
         return "Thu Jan 01 00:00:00 UTC 1970";
      }

      DateFormatSymbols dfs = DateFormatSymbols.getInstance();
      String[] months = dfs.getShortMonths();
      String[] days = dfs.getShortWeekdays();
      int dow = calendar.get(7);
      int month = calendar.get(2);
      int day = calendar.get(5);
      int hour_of_day = calendar.get(11);
      int minute = calendar.get(12);
      int seconds = calendar.get(13);
      int year = calendar.get(1);
      String yr = Integer.toString(year);
      TimeZone zone = calendar.getTimeZone();
      String zoneID = zone.getID();
      if (zoneID == null) {
         zoneID = "";
      }

      StringBuffer sb = new StringBuffer(25 + zoneID.length() + yr.length());
      sb.append(days[dow - 1]).append(' ');
      sb.append(months[month]).append(' ');
      appendTwoDigits(sb, day).append(' ');
      appendTwoDigits(sb, hour_of_day).append(':');
      appendTwoDigits(sb, minute).append(':');
      appendTwoDigits(sb, seconds).append(' ');
      if (zoneID.length() > 0) {
         sb.append(zoneID).append(' ');
      }

      appendFourDigits(sb, year);
      return sb.toString();
   }

   private static final StringBuffer appendFourDigits(StringBuffer sb, int number) {
      if (number >= 0 && number < 1000) {
         sb.append('0');
         if (number < 100) {
            sb.append('0');
         }

         if (number < 10) {
            sb.append('0');
         }
      }

      return sb.append(number);
   }

   private static final StringBuffer appendTwoDigits(StringBuffer sb, int number) {
      if (number < 10) {
         sb.append('0');
      }

      return sb.append(number);
   }
}
