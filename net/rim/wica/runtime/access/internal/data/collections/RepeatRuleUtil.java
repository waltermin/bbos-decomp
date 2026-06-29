package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.Recur$Modifier;
import net.rim.device.apps.api.utility.framework.RecurUtil;

final class RepeatRuleUtil {
   static final int MONTH_IN_YEAR_SHAMT = 17;
   static final int DAY_IN_WEEK_SHAMT = 10;
   static final int WEEK_IN_MONTH_SHAMT = 0;

   static final int getRecurMonth(Recur recur) {
      int result = 0;
      int count = recur.numModifierValues(2);
      Recur$Modifier modifier = (Recur$Modifier)(new Object());

      for (int i = 0; i < count; i++) {
         recur.getModifierAt(2, i, modifier);
         result |= 1 << modifier.parm1 + 17;
      }

      return result;
   }

   static final int dayInWeekRecurToRepeat(Recur item) {
      int recurDIW = RecurUtil.getBitmapDaysOfWeek(item);
      int dayResult = 0;

      for (int i = 0; recurDIW != 0 && i < 7; i++) {
         if ((recurDIW & 1) != 0) {
            dayResult |= 1 << 6 - i + 10;
         }

         recurDIW >>= 1;
      }

      return dayResult;
   }

   static final int getRecurWeekInMonth(Recur recur) {
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
}
