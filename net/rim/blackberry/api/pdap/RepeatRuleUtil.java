package net.rim.blackberry.api.pdap;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import javax.microedition.pim.FieldEmptyException;
import javax.microedition.pim.RepeatRule;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.Recur$Modifier;
import net.rim.device.apps.api.utility.framework.RecurUtil;

public class RepeatRuleUtil {
   static final int MONTH_IN_YEAR_SHAMT = 17;
   static final int DAY_IN_WEEK_SHAMT = 10;
   static final int WEEK_IN_MONTH_SHAMT = 0;
   private static final int RECUR_ANY_DAY = 7;

   public static RepeatRule createRepeatRule(Recur recur) {
      RepeatRule repeat = new RepeatRule();
      repeat.setInt(0, freqRecurToRepeat(recur.getRecurType()));
      repeat.setInt(128, recur.getRecurPeriod());
      if (recur.isFinite()) {
         long endDate = recur.getEndDate();
         if (endDate > 0) {
            repeat.setDate(64, endDate);
         } else {
            repeat.setInt(32, 0);
         }
      }

      int recurValue = dayInWeekRecurToRepeat(RecurUtil.getBitmapDaysOfWeek(recur));
      repeat.setInt(2, recurValue);
      recurValue = RecurUtil.getDayOfMonth(recur);
      if (recurValue > 0) {
         repeat.setInt(1, recurValue);
      }

      recurValue = getRecurWeekInMonth(recur);
      if (recurValue > 0) {
         repeat.setInt(16, recurValue);
      }

      recurValue = getRecurMonth(recur);
      if (recurValue > 0) {
         repeat.setInt(8, recurValue);
      }

      long[] exdates = recur.getExclusions(new long[0]);

      for (int i = recur.getExclusionCount() - 1; i >= 0; i--) {
         repeat.addExceptDate(exdates[i]);
      }

      return repeat;
   }

   public static Recur createRecur(Recur recur, RepeatRule value) {
      if (value == null) {
         return null;
      }

      recur.setRecurType((byte)1);
      recur.setRecurPeriod(1);
      int[] fields = value.getFields();

      for (int i = fields.length - 1; i >= 0; i--) {
         int field = fields[i];
         switch (field) {
            case 0:
               byte recurFreq = freqRepeatToRecur(getRepeatInt(value, field));
               recur.setRecurType(recurFreq);
               break;
            case 1:
               RecurUtil.setDayOfMonth(recur, getRepeatInt(value, field));
               break;
            case 2:
               int daysInWeek = getRepeatInt(value, field);
               int weekInMonth = getRepeatInt(value, 16);
               if (weekInMonth == 0) {
                  RecurUtil.addDaysModifier(recur, dayInWeekRepeatToRecur(daysInWeek), 0);
               } else {
                  int[] weeks = getWeekArray(weekInMonth);

                  for (int j = weeks.length - 1; j >= 0; j--) {
                     RecurUtil.addDaysModifier(recur, dayInWeekRepeatToRecur(daysInWeek), weeks[j]);
                  }
               }
               break;
            case 8:
               int[] months = getMonthArray(getRepeatInt(value, field));

               for (int j = months.length - 1; j >= 0; j--) {
                  RecurUtil.addMonthModifier(recur, months[j]);
               }
               break;
            case 64:
               recur.setEndDate(getRepeatDate(value, field));
               break;
            case 128:
               recur.setRecurPeriod(getRepeatInt(value, field));
         }
      }

      Enumeration exDates = value.getExceptDates();

      while (exDates.hasMoreElements()) {
         Date nextDate = (Date)exDates.nextElement();
         recur.addExclusion(nextDate.getTime());
      }

      return recur;
   }

   public static boolean areDatesOnSameDay(Date date1, Date date2) {
      Calendar cal1 = Calendar.getInstance();
      Calendar cal2 = Calendar.getInstance();
      cal1.setTime(date1);
      cal2.setTime(date2);
      return !(cal1.get(1) != cal2.get(1) | cal1.get(2) != cal2.get(2) | cal1.get(5) != cal2.get(5));
   }

   public static boolean compareExceptionDates(Enumeration d1, Enumeration d2) {
      while (d1.hasMoreElements()) {
         if (!d2.hasMoreElements()) {
            return false;
         }

         Date date1 = (Date)d1.nextElement();
         Date date2 = (Date)d2.nextElement();
         if (!areDatesOnSameDay(date1, date2)) {
            return false;
         }
      }

      return !d2.hasMoreElements();
   }

   static int[] getMonthArray(int months) {
      months >>= 17;
      int[] result = new int[12];
      int numMonths = 0;

      for (int i = 0; months != 0 && i < 12; i++) {
         if ((months & 1) != 0) {
            result[numMonths++] = i;
         }

         months >>= 1;
      }

      return Arrays.copy(result, 0, numMonths);
   }

   static int[] getWeekArray(int weeksInMonth) {
      weeksInMonth >>= 0;
      int[] result = new int[10];
      int numWeeks = 0;

      for (int i = 1; weeksInMonth != 0 && i <= 5; i++) {
         if ((weeksInMonth & 1) != 0) {
            result[numWeeks++] = i;
         }

         weeksInMonth >>= 1;
      }

      for (int i = -1; weeksInMonth != 0 && i >= -5; i--) {
         if ((weeksInMonth & 1) != 0) {
            result[numWeeks++] = i;
         }

         weeksInMonth >>= 1;
      }

      return Arrays.copy(result, 0, numWeeks);
   }

   static int[] getDayInWeekArray(int daysInWeek) {
      daysInWeek >>= 10;
      int[] result = new int[7];
      int numDays = 0;

      for (int i = 0; i < 7; i++) {
         if ((daysInWeek & 1 << 6 - i) != 0) {
            result[numDays++] = i;
         }
      }

      return Arrays.copy(result, 0, numDays);
   }

   static int getRecurMonth(Recur recur) {
      int result = 0;
      int count = recur.numModifierValues(2);
      Recur$Modifier modifier = (Recur$Modifier)(new Object());

      for (int i = 0; i < count; i++) {
         recur.getModifierAt(2, i, modifier);
         result |= 1 << modifier.parm1 + 17;
      }

      return result;
   }

   static int getRecurWeekInMonth(Recur recur) {
      int result = 0;
      int count = recur.numModifierValues(1);
      Recur$Modifier modifier = (Recur$Modifier)(new Object());

      for (int i = 0; i < count; i++) {
         recur.getModifierAt(1, i, modifier);
         int modifierWeek = modifier.parm2;
         if (modifierWeek == -1) {
            result |= 32;
         } else if (modifierWeek > 0 && modifierWeek < 6) {
            result |= 1 << modifierWeek - 1 + 0;
         }
      }

      return result;
   }

   static int dayInWeekRecurToRepeat(int recurDIW) {
      int dayResult = 0;

      for (int i = 0; recurDIW != 0 && i < 7; i++) {
         if ((recurDIW & 1) != 0) {
            dayResult |= 1 << 6 - i + 10;
         }

         recurDIW >>= 1;
      }

      return dayResult;
   }

   public static int freqRecurToRepeat(byte recurFreq) {
      switch (recurFreq) {
         case 0:
            return -1;
         case 1:
         default:
            return 16;
         case 2:
            return 17;
         case 3:
            return 18;
         case 4:
            return 19;
      }
   }

   public static byte freqRepeatToRecur(int repeatFreq) {
      switch (repeatFreq) {
         case 15:
            return -1;
         case 16:
         default:
            return 1;
         case 17:
            return 2;
         case 18:
            return 3;
         case 19:
            return 4;
      }
   }

   public static int dayInWeekRepeatToRecur(int repeatDIW) {
      repeatDIW &= 130048;
      int Days = repeatDIW >> 10;
      int dayResult = 0;
      if (Days != 0) {
         for (int i = 0; i < 7; i++) {
            if ((Days & 1 << i) != 0) {
               dayResult |= 1 << 6 - i;
            }
         }
      }

      return dayResult;
   }

   static void setDayInWeekToRecur(Recur recur, int value) {
      value &= 130048;
      int Days = value >> 10;
      int dayResult = 0;
      if (Days != 0) {
         for (int i = 0; i < 7; i++) {
            if ((Days & 1 << i) != 0) {
               dayResult |= 1 << 6 - i;
            }
         }
      }

      int i = recur.numModifierValues(1) - 1;
      int weeks = 0;
      int weekInMonth = 0;
      Recur$Modifier modifier = (Recur$Modifier)(new Object());

      while (i >= 0) {
         recur.getModifierAt(1, i, modifier);
         recur.removeModifier(1, i);
         weekInMonth = modifier.parm2;
         if (weekInMonth == -1) {
            weeks |= 1;
         } else if (weekInMonth > 0 && weekInMonth < 6) {
            weeks |= 1 << weekInMonth;
         }

         i--;
      }

      if (weeks == 0 && dayResult != 0) {
         RecurUtil.addDaysModifier(recur, dayResult, 0);
      } else {
         if ((weeks & 1) != 0) {
            RecurUtil.addDaysModifier(recur, dayResult, -1);
         }

         weeks >>= 1;

         for (int var10 = 1; weeks != 0; var10++) {
            if ((weeks & 1) != 0) {
               if (dayResult == 0) {
                  RecurUtil.addDayOfClassModifier(recur, 7, var10);
               } else {
                  RecurUtil.addDaysModifier(recur, dayResult, var10);
               }
            }

            weeks >>= 1;
         }
      }
   }

   public static int getRepeatInt(RepeatRule repeat, int field) {
      try {
         return repeat.getInt(field);
      } catch (FieldEmptyException fee) {
         return 0;
      }
   }

   public static long getRepeatDate(RepeatRule repeat, int field) {
      try {
         return repeat.getDate(field);
      } catch (FieldEmptyException fee) {
         return 0;
      }
   }
}
