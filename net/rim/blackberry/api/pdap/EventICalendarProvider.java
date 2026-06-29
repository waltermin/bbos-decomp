package net.rim.blackberry.api.pdap;

import java.util.Date;
import java.util.Enumeration;
import javax.microedition.pim.FieldEmptyException;
import javax.microedition.pim.RepeatRule;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.util.Arrays;

class EventICalendarProvider extends PIMICalendarProvider {
   private EventImpl _event;
   private RepeatRule _repeat;
   private int[] _months;
   private int[] _weekDays;
   private int[] _monthOrdWk;

   public EventICalendarProvider(EventImpl event) {
      this._event = event;
      this._repeat = this._event.getRepeat();
      this._months = null;
      this._weekDays = null;
      this._monthOrdWk = null;
   }

   @Override
   public int getCalendarComponent() {
      return -1766506524;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected String getUIDString() {
      StringBuffer uid = new StringBuffer();
      boolean var4 = false /* VF: Semaphore variable */;

      label20:
      try {
         var4 = true;
         uid.append(this._event.getDate(106, 0));
         var4 = false;
      } finally {
         if (var4) {
            uid.append('0');
            break label20;
         }
      }

      uid.append('-');
      uid.append(new Date().getTime());
      uid.append("@rim.net");
      return uid.toString();
   }

   private String getStringField(int field) {
      return this._event.countValues(field) > 0 ? this._event.getString(field, 0) : null;
   }

   @Override
   public void setDescription(int type, String paramValue, String description) {
      this._event.addString(104, 0, description);
   }

   @Override
   public String getDescriptionValue() {
      return this.getStringField(104);
   }

   @Override
   public void setSummary(int type, String paramValue, String value) {
      this._event.addString(107, 0, value);
   }

   @Override
   public String getSummaryValue() {
      return this.getStringField(107);
   }

   @Override
   public void setLocation(int type, String paramValue, String value) {
      this._event.addString(103, 0, value);
   }

   @Override
   public String getLocationValue() {
      return this.getStringField(103);
   }

   @Override
   public void setDateTimeStart(int type, int paramType, Date dateTimeStartValue) {
      long dtsv = dateTimeStartValue == null ? 0 : dateTimeStartValue.getTime();
      this._event.addDate(106, 0, dtsv);
   }

   @Override
   public int getDateTimeStartType() {
      return 81434961;
   }

   @Override
   public int getDateTimeStartParamType() {
      return -1773854324;
   }

   @Override
   public Date getDateTimeStartValue() {
      return this._event.countValues(106) > 0 ? new Date(this._event.getDate(106, 0)) : null;
   }

   @Override
   public void setDateTimeEnd(int type, int paramType, Date dateTimeEndValue) {
      long dtev = dateTimeEndValue == null ? 0 : dateTimeEndValue.getTime();
      this._event.addDate(102, 0, dtev);
   }

   @Override
   public int getDateTimeEndType() {
      return 81434961;
   }

   @Override
   public int getDateTimeEndParamType() {
      return -1773854324;
   }

   @Override
   public Date getDateTimeEndValue() {
      return this._event.countValues(102) > 0 ? new Date(this._event.getDate(102, 0)) : null;
   }

   @Override
   public int getAction() {
      return -1905220446;
   }

   @Override
   public void setTrigger(int type, int paramType, Date date) {
      int offset = 0;
      if (date != null && this._event.countValues(106) > 0) {
         long dt = this._event.getDate(106, 0) - date.getTime();
         offset = (int)dt / 1000;
      }

      this._event.addInt(100, 0, offset);
   }

   @Override
   public Date getTrigger() {
      return this._event.countValues(100) > 0 ? new Date(this._event.getDate(106, 0) - this._event.getInt(100, 0) * 1000) : null;
   }

   @Override
   public void setRelativeTrigger(int type, int paramType, int offset) {
      this._event.addInt(100, 0, offset);
   }

   @Override
   public int getRelativeTrigger() {
      return this._event.countValues(100) > 0 ? this._event.getInt(100, 0) : 0;
   }

   @Override
   public int getTriggerType() {
      return super._version == 1 ? 81434961 : 0;
   }

   @Override
   public int getTriggerParamType() {
      return super._version == 1 ? -1773854324 : 0;
   }

   @Override
   public String getAlarmDescriptionValue() {
      StringBuffer desc = new StringBuffer();
      if (this._event.countValues(107) > 0) {
         desc.append(this._event.getString(107, 0));
         desc.append('\r');
      }

      if (this._event.countValues(106) > 0 && this._event.countValues(102) > 0) {
         DateFormat format = DateFormat.getInstance(54);
         desc.append(format.formatLocal(this._event.getDate(106, 0)));
         desc.append('\r');
         desc.append(this._event.getDate(102, 0));
      }

      return desc.toString();
   }

   @Override
   public void setExceptionDateTime(int type, int paramType, Date[] date, int dateLength) {
      if (this._repeat == null) {
         this._repeat = new RepeatRule();
      }

      Enumeration exDates = this._repeat.getExceptDates();

      while (exDates.hasMoreElements()) {
         this._repeat.removeExceptDate(((Date)exDates.nextElement()).getTime());
      }

      for (int i = 0; i < dateLength; i++) {
         long dt = date[i] == null ? 0 : date[i].getTime();
         this._repeat.addExceptDate(dt);
      }
   }

   @Override
   public void setExceptionDateTime(int type, int paramType, Date date) {
      if (this._repeat == null) {
         this._repeat = new RepeatRule();
      }

      long dt = date == null ? 0 : date.getTime();
      this._repeat.addExceptDate(dt);
   }

   @Override
   public int getExceptionDateTimeType() {
      return 81434961;
   }

   @Override
   public int getExceptionDateTimeParamType() {
      return -1773854324;
   }

   @Override
   public Date[] getExceptionDateTimeValue() {
      Date[] dates = null;
      if (this._repeat != null) {
         Enumeration exceptTimes = this._repeat.getExceptDates();
         dates = new Date[this.getExceptionDateLength()];

         for (int i = 0; exceptTimes.hasMoreElements(); i++) {
            dates[i] = (Date)exceptTimes.nextElement();
         }
      }

      return dates;
   }

   @Override
   public int getExceptionDateLength() {
      if (this._repeat == null) {
         return 0;
      }

      int count = 0;

      for (Enumeration exDates = this._repeat.getExceptDates(); exDates.hasMoreElements(); count++) {
         exDates.nextElement();
      }

      return count;
   }

   @Override
   public void setFreq(int freqType) {
      if (freqType == -746168210) {
         this._repeat = null;
      } else {
         if (this._repeat == null) {
            this._repeat = new RepeatRule();
         }

         switch (freqType) {
            case -1738378111:
               this._repeat.setInt(0, 17);
               return;
            case -1681232246:
               this._repeat.setInt(0, 19);
               return;
            case -565154143:
            case 1726084353:
            case 2136870513:
               this._repeat = null;
               return;
            case 64808441:
               this._repeat.setInt(0, 16);
               return;
            case 1954618349:
               this._repeat.setInt(0, 18);
               return;
            default:
               this._repeat = null;
               throw new IllegalArgumentException("invalid value");
         }
      }
   }

   @Override
   public int getFreq() {
      if (this._repeat != null) {
         try {
            int repeatFreq = this._repeat.getInt(0);
            switch (repeatFreq) {
               case 15:
                  break;
               case 16:
               default:
                  return 64808441;
               case 17:
                  return -1738378111;
               case 18:
                  return 1954618349;
               case 19:
                  return -1681232246;
            }
         } catch (FieldEmptyException var2) {
         }
      }

      return -746168210;
   }

   @Override
   public boolean isFreqSpecified() {
      return this._repeat != null;
   }

   @Override
   public void setUntil(int untilEndDateType, Date untilEndDate) {
      if (this._repeat == null) {
         this._repeat = new RepeatRule();
      }

      long ued = untilEndDate == null ? 0 : untilEndDate.getTime();
      this._repeat.setDate(64, ued);
   }

   @Override
   public Date getUntil() {
      if (this._repeat != null) {
         try {
            return new Date(this._repeat.getDate(64));
         } catch (FieldEmptyException var2) {
         }
      }

      return null;
   }

   @Override
   public int getUntilEndDateType() {
      return -1773854324;
   }

   @Override
   public boolean isUntilSpecified() {
      if (this._repeat != null) {
         try {
            long repeatEnd = this._repeat.getDate(64);
            if (repeatEnd != 0) {
               if (super._version == 1) {
                  return this._event.getRimEvent().getRecurrenceCopy().getEndDate() == repeatEnd;
               }

               return true;
            }
         } catch (FieldEmptyException var3) {
         }
      }

      return false;
   }

   @Override
   public void setCount(int count) {
      if (this._repeat == null) {
         this._repeat = new RepeatRule();
      }

      this._repeat.setInt(32, count);
   }

   @Override
   public int getCount() {
      if (this._repeat != null) {
         try {
            return this._repeat.getInt(32);
         } catch (FieldEmptyException var2) {
         }
      }

      return 0;
   }

   @Override
   public boolean isCountSpecified() {
      if (this._repeat == null) {
         return false;
      }

      try {
         return super._version == 1 ? this._repeat.getInt(32) != 0 && !this.isUntilSpecified() : this._repeat.getInt(32) != 0 || !this.isUntilSpecified();
      } catch (FieldEmptyException fee) {
         return false;
      }
   }

   @Override
   public void setInterval(int interval) {
      if (this._repeat == null) {
         this._repeat = new RepeatRule();
      }

      this._repeat.setInt(128, interval);
   }

   @Override
   public int getInterval() {
      if (this._repeat != null) {
         try {
            return this._repeat.getInt(128);
         } catch (FieldEmptyException var2) {
         }
      }

      return 0;
   }

   @Override
   public boolean isIntervalSpecified() {
      return this._repeat != null;
   }

   @Override
   public void setByDay(int[] orderWeek, int[] weekDay, int dayLength) {
      this._monthOrdWk = orderWeek;
      this._weekDays = weekDay;
      int weeks = 0;
      int days = 0;

      for (int i = 0; i < dayLength; i++) {
         int thisWkDay = weekDay[i];
         int thisOrdWk = orderWeek[i];
         int maxOrderWeek = 5;
         if (super._version == 1) {
            maxOrderWeek = 53;
         }

         if (thisWkDay < 1 || thisWkDay > 7 || thisOrdWk < -maxOrderWeek || thisOrdWk > maxOrderWeek) {
            throw new IllegalArgumentException("invalid value");
         }

         try {
            if (this._repeat.getInt(0) == 19 || thisOrdWk < -5 || thisOrdWk > 5) {
               thisOrdWk = 0;
            }
         } catch (FieldEmptyException var11) {
         }

         days |= 1 << 7 - thisWkDay + 10;
         if (thisOrdWk != 0) {
            if (thisOrdWk < 0) {
               thisOrdWk = -thisOrdWk + 5;
            }

            weeks |= 1 << thisOrdWk - 1 + 0;
         }
      }

      if (this._repeat == null) {
         this._repeat = new RepeatRule();
      }

      this._repeat.setInt(2, days);
      this._repeat.setInt(16, weeks);
   }

   @Override
   public void setByDay(int orderWeek, int weekDay) {
      if (this._repeat == null) {
         this._repeat = new RepeatRule();
      }

      int days;
      try {
         days = this._repeat.getInt(2);
      } catch (FieldEmptyException fee) {
         days = 0;
      }

      int weeks;
      try {
         weeks = this._repeat.getInt(16);
      } catch (FieldEmptyException fee) {
         weeks = 0;
      }

      int maxOrderWeek = 5;
      if (super._version == 1) {
         maxOrderWeek = 53;
      }

      if (weekDay >= 1 && weekDay <= 7 && orderWeek >= -maxOrderWeek && orderWeek <= maxOrderWeek) {
         try {
            if (this._repeat.getInt(0) == 19 || orderWeek < -5 || orderWeek > 5) {
               orderWeek = 0;
            }
         } catch (FieldEmptyException var9) {
         }

         days |= 1 << 7 - weekDay + 10;
         if (orderWeek != 0) {
            if (orderWeek < 0) {
               orderWeek = -orderWeek + 5;
            }

            weeks |= 1 << orderWeek - 1 + 0;
         }

         this._repeat.setInt(2, days);
         this._repeat.setInt(16, weeks);
      } else {
         throw new IllegalArgumentException("invalid value");
      }
   }

   @Override
   public int[] getByDayOrderWeek() {
      if (this._monthOrdWk == null || this._weekDays == null) {
         this.calcWeekArray();
      }

      return this._monthOrdWk;
   }

   @Override
   public int[] getByDayWeekDay() {
      if (this._monthOrdWk == null || this._weekDays == null) {
         this.calcWeekArray();
      }

      return this._weekDays;
   }

   @Override
   public int getByDayLength() {
      if (this._monthOrdWk == null || this._weekDays == null) {
         this.calcWeekArray();
      }

      return this._monthOrdWk == null ? 0 : this._monthOrdWk.length;
   }

   @Override
   public boolean isDaysSpecified() {
      try {
         return this._repeat != null && this._repeat.getInt(2) != 0;
      } catch (FieldEmptyException fee) {
         return false;
      }
   }

   @Override
   public void setByMonthDay(int[] monthDay, int monthDayLength) {
      if (monthDayLength > 0) {
         this.setByMonthDay(monthDay[0]);
      } else {
         if (this._repeat != null) {
            this._repeat.setInt(1, 0);
         }
      }
   }

   @Override
   public void setByMonthDay(int monthDay) {
      if (this._repeat == null) {
         this._repeat = new RepeatRule();
      }

      if (monthDay <= 0) {
         if (monthDay >= -31 && monthDay <= -1) {
            this._repeat.setInt(1, 32 + monthDay);
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         this._repeat.setInt(1, monthDay);
      }
   }

   @Override
   public int[] getByMonthDay() {
      try {
         int day = this._repeat.getInt(1);
         if (day != 0) {
            return new int[]{day};
         }
      } catch (FieldEmptyException var3) {
      }

      return null;
   }

   @Override
   public int getByMonthDayLength() {
      try {
         if (this._repeat.getInt(1) != 0) {
            return 1;
         }
      } catch (FieldEmptyException var2) {
      }

      return 0;
   }

   @Override
   public boolean isMonthDaysSpecified() {
      try {
         return this._repeat != null && this._repeat.getInt(1) != 0;
      } catch (FieldEmptyException fee) {
         return false;
      }
   }

   @Override
   public void setByYearDay(int[] yearDay, int yearDayLength) {
      if (yearDayLength > 0) {
         this.setByYearDay(yearDay[0]);
      } else {
         if (this._repeat != null) {
            this._repeat.setInt(4, 0);
         }
      }
   }

   @Override
   public void setByYearDay(int yearDay) {
      if (this._repeat == null) {
         this._repeat = new RepeatRule();
      }

      if (yearDay <= 0 && super._version == 1) {
         if (yearDay < -366 || yearDay > -1) {
            throw new IllegalArgumentException();
         }
      } else {
         this._repeat.setInt(4, yearDay);
      }
   }

   @Override
   public int[] getByYearDay() {
      try {
         int day = this._repeat.getInt(4);
         if (day != 0) {
            return new int[]{day};
         }
      } catch (FieldEmptyException var3) {
      }

      return null;
   }

   @Override
   public int getByYearDayLength() {
      try {
         if (this._repeat.getInt(4) != 0) {
            return 1;
         }
      } catch (FieldEmptyException var2) {
      }

      return 0;
   }

   @Override
   public boolean isYearDaysSpecified() {
      try {
         return this._repeat != null && this._repeat.getInt(4) != 0;
      } catch (FieldEmptyException fee) {
         return false;
      }
   }

   @Override
   public void setByMonth(int[] month, int monthLength) {
      this._months = month;
      int intMonths = 0;

      for (int i = 0; i < monthLength; i++) {
         int thisMonth = month[i];
         if (thisMonth <= 0 || thisMonth >= 13) {
            throw new IllegalArgumentException("invalid value");
         }

         intMonths |= 1 << thisMonth - 1 + 17;
      }

      if (this._repeat == null) {
         this._repeat = new RepeatRule();
      }

      this._repeat.setInt(8, intMonths);
   }

   @Override
   public void setByMonth(int month) {
      if (this._repeat == null) {
         this._repeat = new RepeatRule();
      }

      if (month > 0 && month < 13) {
         int intMonths;
         try {
            intMonths = this._repeat.getInt(8);
         } catch (FieldEmptyException fee) {
            intMonths = 0;
         }

         intMonths |= 1 << month - 1 + 17;
         this._repeat.setInt(8, intMonths);
      } else {
         throw new IllegalArgumentException("invalid value");
      }
   }

   @Override
   public int[] getByMonth() {
      if (this._months == null) {
         this._months = this.getMonthArray();
      }

      return this._months;
   }

   @Override
   public int getByMonthLength() {
      if (this._months == null) {
         this._months = this.getMonthArray();
      }

      return this._months.length;
   }

   @Override
   public boolean isMonthsSpecified() {
      try {
         return this._repeat != null && this._repeat.getInt(8) != 0;
      } catch (FieldEmptyException fee) {
         return false;
      }
   }

   private int[] getMonthArray() {
      int monthinyear;
      try {
         monthinyear = this._repeat.getInt(8);
      } catch (FieldEmptyException fee) {
         monthinyear = 0;
      }

      int[] months = RepeatRuleUtil.getMonthArray(monthinyear);

      for (int i = months.length - 1; i >= 0; i--) {
         months[i]++;
      }

      return months;
   }

   private void calcWeekArray() {
      int[] weeks = null;
      int numWeeks = 0;

      try {
         if (this._repeat.getInt(0) != 19) {
            weeks = RepeatRuleUtil.getWeekArray(this._repeat.getInt(16));
            numWeeks = weeks.length;
         }
      } catch (FieldEmptyException var11) {
      }

      int dayinweek;
      try {
         dayinweek = this._repeat.getInt(2);
      } catch (FieldEmptyException fee) {
         dayinweek = 0;
      }

      int[] days = RepeatRuleUtil.getDayInWeekArray(dayinweek);
      int numDays = days.length;
      if (numDays == 0) {
         this._monthOrdWk = null;
         this._weekDays = null;
      } else {
         if (numWeeks == 0) {
            this._monthOrdWk = new int[numDays];
            Arrays.fill(this._monthOrdWk, 0);
            this._weekDays = new int[numDays];

            for (int i = 0; i < numDays; i++) {
               this._weekDays[i] = days[i] + 1;
            }
         } else {
            int length = numWeeks * numDays;
            this._monthOrdWk = new int[length];
            this._weekDays = new int[length];
            int index = 0;

            for (int i = 0; i < numDays; i++) {
               for (int j = 0; j < numWeeks; j++) {
                  this._weekDays[index] = days[i] + 1;
                  this._monthOrdWk[index] = weeks[j];
                  index++;
               }
            }
         }
      }
   }

   public void setRepeat() {
      this._event.setRepeat(this._repeat);
   }
}
