package net.rim.blackberry.api.pim;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.utility.framework.RecurUtil;

public class RepeatRule {
   private EventFieldsModel _eventFieldsModel;
   Recur _recur;
   public static final int COUNT = 0;
   public static final int DAY_IN_MONTH = 1;
   public static final int DAY_IN_WEEK = 2;
   public static final int DAY_IN_YEAR = 3;
   public static final int END = 4;
   public static final int FREQUENCY = 5;
   public static final int INTERVAL = 6;
   public static final int MONTH_IN_YEAR = 7;
   public static final int WEEK_IN_MONTH = 8;
   public static final int DAILY = 16;
   public static final int WEEKLY = 17;
   public static final int MONTHLY = 18;
   public static final int YEARLY = 19;
   public static final int FIRST = 1;
   public static final int SECOND = 2;
   public static final int THIRD = 4;
   public static final int FOURTH = 8;
   public static final int FIFTH = 16;
   public static final int LAST = 32;
   public static final int SECONDLAST = 64;
   public static final int THIRDLAST = 128;
   public static final int FOURTHLAST = 256;
   public static final int FIFTHLAST = 512;
   public static final int SATURDAY = 1024;
   public static final int FRIDAY = 2048;
   public static final int THURSDAY = 4096;
   public static final int WEDNESDAY = 8192;
   public static final int TUESDAY = 16384;
   public static final int MONDAY = 32768;
   public static final int SUNDAY = 65536;
   public static final int JANUARY = 131072;
   public static final int FEBRUARY = 262144;
   public static final int MARCH = 524288;
   public static final int APRIL = 1048576;
   public static final int MAY = 2097152;
   public static final int JUNE = 4194304;
   public static final int JULY = 8388608;
   public static final int AUGUST = 16777216;
   public static final int SEPTEMBER = 33554432;
   public static final int OCTOBER = 67108864;
   public static final int NOVEMBER = 134217728;
   public static final int DECEMBER = 268435456;
   private static final int ALL_DAYS = 130048;
   private static final int ALL_MONTHS = 536739840;
   private static final int ALL_WEEKS = 1023;
   static final int MONTH_IN_YEAR_SHAMT = 17;
   static final int DAY_IN_WEEK_SHAMT = 10;
   static final int WEEK_IN_MONTH_SHAMT = 0;

   EventFieldsModel getFieldsModel() {
      return this._eventFieldsModel;
   }

   public RepeatRule() {
      net.rim.device.apps.api.calendar.modelcontrollerinterface.Event newRimEvent = (net.rim.device.apps.api.calendar.modelcontrollerinterface.Event)EventStatics._eventFactory
         .createInstance(null);
      this._recur = newRimEvent.getRecurrenceCopy();
      this._recur.setRecurType((byte)1);
      this._eventFieldsModel = new EventFieldsModel();
   }

   RepeatRule(net.rim.device.apps.api.calendar.modelcontrollerinterface.Event event, Recur recur, EventFieldsModel model) {
      if (recur == null) {
         this._recur = event.getRecurrenceCopy();
      } else {
         this._recur = recur;
      }

      if (model == null) {
         this._eventFieldsModel = new EventFieldsModel(event);
      } else {
         this._eventFieldsModel = model.copy();
         int oldFreq = model.getFrequency();
         this._eventFieldsModel.setFrequency(RepeatRuleUtil.freqRecurToRepeat(this._recur.getRecurType()));
         this._eventFieldsModel.setInterval(this._recur.getRecurPeriod());
         if (this._eventFieldsModel.getMonth() == 0) {
            this.setInt(7, 0);
         }

         boolean recurFinite = this._recur.isFinite();
         long recurEndDate = this._recur.getEndDate();
         long endDate = this._eventFieldsModel.getEndDate();
         int count = this._eventFieldsModel.getRepeatCount();
         if (!this.isEndDateCorrect(count, recurFinite, recurEndDate, event) && recurEndDate != this._eventFieldsModel.getStartDate() - 1) {
            this._eventFieldsModel.setRepeatCount(0);
            endDate = recurFinite ? recurEndDate : 0;
         }

         if (endDate == 0) {
            this.setDate(4, 0);
            if (count == 0) {
               this._recur.setAsFinite(false);
            }
         } else {
            this.setDate(4, endDate);
         }

         int dow = RecurUtil.getBitmapDaysOfWeek(this._recur);
         int pimDIW = this._eventFieldsModel.getDayInWeek();
         if (pimDIW == 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(this._eventFieldsModel.getStartDate()));
            int dayInWeek = 1 << cal.get(7) - 1;
            if ((dow & dayInWeek) != 0 && (dow & ~dayInWeek) == 0) {
               this.setInt(2, 0);
            } else {
               this._eventFieldsModel.setDayInWeek(RepeatRuleUtil.dayInWeekRecurToRepeat(dow));
            }
         } else {
            boolean wasRelative = false;
            switch (oldFreq) {
               case 15:
                  break;
               case 16:
               case 17:
               default:
                  wasRelative = true;
                  break;
               case 18:
                  wasRelative = this._eventFieldsModel.getDayNumber() == 0;
                  break;
               case 19:
                  wasRelative = this._eventFieldsModel.getDayNumber() == 0 && this._eventFieldsModel.getDayInYear() != 0;
            }

            int numModifiers = this._recur.numModifierValues(1);
            if (wasRelative && numModifiers == 0) {
               this._eventFieldsModel.setDayInWeek(0);
               this._eventFieldsModel.setWeekInMonth(0);
            } else {
               if (oldFreq == 16 || oldFreq == 17) {
                  int newFreq = this._eventFieldsModel.getFrequency();
                  if (newFreq == 16 || newFreq == 17) {
                     this._eventFieldsModel.setDayInWeek(RepeatRuleUtil.dayInWeekRecurToRepeat(dow));
                  }
               }
            }
         }
      }
   }

   private boolean isEndDateCorrect(int count, boolean recurFinite, long recurEndDate, net.rim.device.apps.api.calendar.modelcontrollerinterface.Event event) {
      long startDate = event.getStartDate(null);
      if (count == 0) {
         return false;
      } else if (startDate != 0) {
         long eventEndDate = RepeatPatternEnumeration.findLastCount(event, startDate, count);
         return eventEndDate == 0 ? recurEndDate == startDate - 1 : recurFinite && eventEndDate == recurEndDate;
      } else {
         return true;
      }
   }

   public Enumeration dates(long startDate, long subsetBeginning, long subsetEnding) {
      try {
         Event event = ((EventList)PIM.getInstance().openPIMList(2, 3)).createEvent();
         event.addDate(106, 0, startDate);
         event.setRepeat(this);
         return new RepeatPatternEnumeration(
            ((EventImpl)event).getRimEvent(), startDate, subsetBeginning, subsetEnding, this._eventFieldsModel.getRepeatCount()
         );
      } catch (PIMException e) {
         System.out.println(e.toString());
         return new Vector().elements();
      }
   }

   public void addExceptDate(long date) {
      this._recur.addExclusion(date);
   }

   public void removeExceptDate(long date) {
      this._recur.removeExclusion(date);
   }

   public long[] getExceptDates() {
      return this._recur.getExclusions(null);
   }

   public int getInt(int field) {
      int value;
      switch (field) {
         case -1:
         case 4:
            throw new IllegalArgumentException("Invalid field ID");
         case 0:
         default:
            value = this._eventFieldsModel.getRepeatCount();
            break;
         case 1:
            value = this._eventFieldsModel.getDayNumber();
            break;
         case 2:
            value = this._eventFieldsModel.getDayInWeek();
            break;
         case 3:
            value = this._eventFieldsModel.getDayInYear();
            break;
         case 5:
            value = this._eventFieldsModel.getFrequency();
            break;
         case 6:
            value = this._eventFieldsModel.getInterval();
            break;
         case 7:
            value = this._eventFieldsModel.getMonth();
            break;
         case 8:
            value = this._eventFieldsModel.getWeekInMonth();
      }

      if (value <= 0) {
         throw new FieldEmptyException();
      } else {
         return value;
      }
   }

   public void setInt(int field, int value) {
      switch (field) {
         case -1:
         case 4:
            throw new IllegalArgumentException();
         case 0:
         default:
            if (value < 0) {
               throw new IllegalArgumentException();
            }

            this._eventFieldsModel.setRepeatCount(value);
            return;
         case 1:
            if (value >= 0 && value < 32) {
               this._eventFieldsModel.setDayNumber(value);
               return;
            }

            throw new IllegalArgumentException();
         case 2:
            if ((value & -130049) != 0) {
               throw new IllegalArgumentException();
            }

            this._eventFieldsModel.setDayInWeek(value);
            RepeatRuleUtil.setDayInWeekToRecur(this._recur, value);
            return;
         case 3:
            if (value >= 0 && value < 367) {
               this._eventFieldsModel.setDayInYear(value);
               return;
            }

            throw new IllegalArgumentException();
         case 5:
            byte recurFreq = RepeatRuleUtil.freqRepeatToRecur(value);
            if (recurFreq < 0) {
               throw new IllegalArgumentException();
            }

            this._recur.setRecurType(recurFreq);
            this._eventFieldsModel.setFrequency(value);
            return;
         case 6:
            if (value <= 0) {
               throw new IllegalArgumentException();
            }

            this._recur.setRecurPeriod(value);
            this._eventFieldsModel.setInterval(value);
            return;
         case 7:
            if ((value & -536739841) != 0) {
               throw new IllegalArgumentException();
            }

            this._eventFieldsModel.setMonth(value);
            return;
         case 8:
            if ((value & -1024) != 0) {
               throw new IllegalArgumentException();
            } else {
               this._eventFieldsModel.setWeekInMonth(value);
            }
      }
   }

   public long getDate(int field) {
      switch (field) {
         case 4:
            long value = this._recur.getEndDate();
            if (value <= 0) {
               throw new FieldEmptyException();
            }

            return value;
         default:
            throw new IllegalArgumentException();
      }
   }

   public void setDate(int field, long value) {
      switch (field) {
         case 4:
            if (value >= 0) {
               this._recur.setEndDate(value);
               this._eventFieldsModel.setEndDate(value);
               return;
            }

            throw new IllegalArgumentException();
         default:
            throw new IllegalArgumentException();
      }
   }

   public int[] getFields() {
      int[] fields = new int[9];
      int index = 0;
      if (this._eventFieldsModel.getFrequency() > 0) {
         fields[index++] = 5;
      }

      if (this._eventFieldsModel.getInterval() > 0) {
         fields[index++] = 6;
      }

      if (this._eventFieldsModel.getRepeatCount() > 0) {
         fields[index++] = 0;
      }

      if (this._eventFieldsModel.getEndDate() > 0) {
         fields[index++] = 4;
      }

      if (this._eventFieldsModel.getDayInWeek() > 0) {
         fields[index++] = 2;
      }

      if (this._eventFieldsModel.getMonth() > 0) {
         fields[index++] = 7;
      }

      if (this._eventFieldsModel.getWeekInMonth() > 0) {
         fields[index++] = 8;
      }

      if (this._eventFieldsModel.getDayNumber() > 0) {
         fields[index++] = 1;
      }

      if (this._eventFieldsModel.getDayInYear() > 0) {
         fields[index++] = 3;
      }

      if (index != fields.length) {
         fields = Arrays.copy(fields, 0, index);
      }

      return fields;
   }

   RepeatRule copy() {
      net.rim.device.apps.api.calendar.modelcontrollerinterface.Event _eventCopy = (net.rim.device.apps.api.calendar.modelcontrollerinterface.Event)EventStatics._eventFactory
         .createInstance(null);
      _eventCopy.setRecurrence(this._recur);
      RepeatRule aCopy = new RepeatRule();
      aCopy._recur = _eventCopy.getRecurrenceCopy();
      aCopy._eventFieldsModel = this._eventFieldsModel.copy();
      return aCopy;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof RepeatRule)) {
         return false;
      }

      RepeatRule rr = (RepeatRule)obj;
      int[] fields = this.getFields();
      int length = fields.length;
      if (length != rr.getFields().length) {
         return false;
      }

      for (int i = 0; i < length; i++) {
         try {
            if (this.getInt(fields[i]) != rr.getInt(fields[i])) {
               return false;
            }
         } catch (FieldEmptyException e) {
            return false;
         }
      }

      long[] d1 = this.getExceptDates();
      long[] d2 = rr.getExceptDates();
      if (d1.length != d2.length) {
         return false;
      }

      for (int i = d1.length - 1; i >= 0; i--) {
         if (Math.abs(d1[i] - d2[i]) >= 1000) {
            return false;
         }
      }

      return true;
   }
}
