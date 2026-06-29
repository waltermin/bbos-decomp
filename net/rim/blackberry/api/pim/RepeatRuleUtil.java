package net.rim.blackberry.api.pim;

import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.Recur$Modifier;
import net.rim.device.apps.api.utility.framework.RecurUtil;

class RepeatRuleUtil {
   private static final int RECUR_ANY_DAY;

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

   static int freqRecurToRepeat(byte recurFreq) {
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

   static byte freqRepeatToRecur(int repeatFreq) {
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
}
