package javax.microedition.pim;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.blackberry.api.pdap.EventImpl;
import net.rim.blackberry.api.pdap.RepeatPatternEnumeration;
import net.rim.blackberry.api.pdap.RepeatRuleUtil;
import net.rim.vm.Array;

public class RepeatRule {
   private int _frequency;
   private int _interval;
   private int _count;
   private long _endDate;
   private int _dayInWeek;
   private int _month;
   private int _weekInMonth;
   private int _dayInMonth;
   private int _dayInYear;
   Vector _exDates = (Vector)(new Object());
   public static final int COUNT;
   public static final int DAY_IN_MONTH;
   public static final int DAY_IN_WEEK;
   public static final int DAY_IN_YEAR;
   public static final int END;
   public static final int FREQUENCY;
   public static final int INTERVAL;
   public static final int MONTH_IN_YEAR;
   public static final int WEEK_IN_MONTH;
   public static final int DAILY;
   public static final int WEEKLY;
   public static final int MONTHLY;
   public static final int YEARLY;
   public static final int FIRST;
   public static final int SECOND;
   public static final int THIRD;
   public static final int FOURTH;
   public static final int FIFTH;
   public static final int LAST;
   public static final int SECONDLAST;
   public static final int THIRDLAST;
   public static final int FOURTHLAST;
   public static final int FIFTHLAST;
   public static final int SATURDAY;
   public static final int FRIDAY;
   public static final int THURSDAY;
   public static final int WEDNESDAY;
   public static final int TUESDAY;
   public static final int MONDAY;
   public static final int SUNDAY;
   public static final int JANUARY;
   public static final int FEBRUARY;
   public static final int MARCH;
   public static final int APRIL;
   public static final int MAY;
   public static final int JUNE;
   public static final int JULY;
   public static final int AUGUST;
   public static final int SEPTEMBER;
   public static final int OCTOBER;
   public static final int NOVEMBER;
   public static final int DECEMBER;
   private static final int ALL_DAYS;
   private static final int ALL_MONTHS;
   private static final int ALL_WEEKS;

   public Enumeration dates(long startDate, long subsetBeginning, long subsetEnding) {
      if (subsetBeginning > subsetEnding) {
         throw new Object();
      }

      try {
         Event event = ((EventList)PIM.getInstance().openPIMList(2, 3)).createEvent();
         event.addDate(106, 0, startDate);
         event.setRepeat(this);
         return new RepeatPatternEnumeration(((EventImpl)event).getRimEvent(), startDate, subsetBeginning, subsetEnding, this._count);
      } catch (PIMException e) {
         System.out.println(e.toString());
         return ((Vector)(new Object())).elements();
      }
   }

   public void addExceptDate(long date) {
      Date newdate = (Date)(new Object(date));
      if (!this._exDates.contains(newdate)) {
         this._exDates.addElement(newdate);
      }
   }

   public void removeExceptDate(long date) {
      this._exDates.removeElement(new Object(date));
   }

   public Enumeration getExceptDates() {
      return this._exDates.elements();
   }

   public int getInt(int field) {
      int value;
      switch (field) {
         case 0:
            value = this._frequency;
            break;
         case 1:
            value = this._dayInMonth;
            break;
         case 2:
            value = this._dayInWeek;
            break;
         case 4:
            value = this._dayInYear;
            break;
         case 8:
            value = this._month;
            break;
         case 16:
            value = this._weekInMonth;
            break;
         case 32:
            value = this._count;
            break;
         case 128:
            value = this._interval;
            break;
         default:
            throw new Object("Invalid field ID");
      }

      if (value <= 0) {
         throw new FieldEmptyException();
      } else {
         return value;
      }
   }

   public void setInt(int field, int value) {
      switch (field) {
         case 0:
            if (value < 0) {
               throw new Object();
            }

            this._frequency = value;
            return;
         case 1:
            if (value > 0 && value < 32) {
               this._dayInMonth = value;
               return;
            }

            throw new Object();
         case 2:
            if ((value & -130049) != 0) {
               throw new Object();
            }

            this._dayInWeek = value;
            return;
         case 4:
            if (value > 0 && value < 367) {
               this._dayInYear = value;
               return;
            }

            throw new Object();
         case 8:
            if ((value & -536739841) != 0) {
               throw new Object();
            }

            this._month = value;
            return;
         case 16:
            if ((value & -1024) != 0) {
               throw new Object();
            }

            this._weekInMonth = value;
            return;
         case 32:
            if (value < 0) {
               throw new Object();
            }

            this._count = value;
            return;
         case 128:
            if (value <= 0) {
               throw new Object();
            }

            this._interval = value;
            return;
         default:
            throw new Object();
      }
   }

   public long getDate(int field) {
      switch (field) {
         case 64:
            long value = this._endDate;
            if (value <= 0) {
               throw new FieldEmptyException();
            }

            return value;
         default:
            throw new Object();
      }
   }

   public void setDate(int field, long value) {
      switch (field) {
         case 64:
            if (value >= 0) {
               this._endDate = value;
               return;
            }

            throw new Object();
         default:
            throw new Object();
      }
   }

   public int[] getFields() {
      int[] fields = new int[9];
      int index = 0;
      if (this._frequency > 0) {
         fields[index++] = 0;
      }

      if (this._interval > 0) {
         fields[index++] = 128;
      }

      if (this._count > 0) {
         fields[index++] = 32;
      }

      if (this._endDate > 0) {
         fields[index++] = 64;
      }

      if (this._dayInWeek > 0) {
         fields[index++] = 2;
      }

      if (this._month > 0) {
         fields[index++] = 8;
      }

      if (this._weekInMonth > 0) {
         fields[index++] = 16;
      }

      if (this._dayInMonth > 0) {
         fields[index++] = 1;
      }

      if (this._dayInYear > 0) {
         fields[index++] = 4;
      }

      Array.resize(fields, index);
      return fields;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof RepeatRule)) {
         return false;
      }

      RepeatRule rr = (RepeatRule)obj;
      int[] fields = rr.getFields();
      if (fields.length != this.getFields().length) {
         return false;
      }

      for (int i = fields.length - 1; i >= 0; i--) {
         int field = fields[i];

         try {
            switch (field) {
               case 0:
               case 1:
               case 2:
               case 4:
               case 8:
               case 16:
               case 32:
               case 128:
                  if (rr.getInt(field) != this.getInt(field)) {
                     return false;
                  }
                  break;
               case 64:
                  if (!RepeatRuleUtil.areDatesOnSameDay((Date)(new Object(rr.getDate(field))), (Date)(new Object(this.getDate(field))))) {
                     return false;
                  }
            }
         } catch (FieldEmptyException fee) {
            return false;
         }
      }

      return RepeatRuleUtil.compareExceptionDates(this.getExceptDates(), rr.getExceptDates());
   }
}
