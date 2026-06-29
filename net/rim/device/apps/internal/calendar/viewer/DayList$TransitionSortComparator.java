package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.calendar.controller.Duration;

public final class DayList$TransitionSortComparator implements Comparator {
   private final DayList this$0;

   protected DayList$TransitionSortComparator(DayList _1) {
      this.this$0 = _1;
   }

   @Override
   public final int compare(Object o1, Object o2) {
      DayList$Transition t1 = (DayList$Transition)o1;
      DayList$Transition t2 = (DayList$Transition)o2;
      byte tt1 = t1._transitionType;
      byte tt2 = t2._transitionType;
      if (t1._timeInMillis > t2._timeInMillis) {
         return 1;
      }

      if (t1._timeInMillis != t2._timeInMillis) {
         return -1;
      }

      if (tt1 == 1 && tt2 != 1) {
         return -1;
      }

      if (tt1 != 1 && tt2 == 1) {
         return 1;
      }

      if (tt1 == 2 && tt2 != 2) {
         return -1;
      }

      if (tt1 != 2 && tt2 == 2) {
         return 1;
      }

      if (tt1 == 3 && tt2 != 3) {
         return 1;
      }

      if (tt1 != 3 && tt2 == 3) {
         return -1;
      }

      if ((tt1 & 16) != 0 && (tt2 & 16) != 0) {
         int c1 = tt1 & 2;
         int c2 = tt2 & 2;
         if (t1._calElement == t2._calElement) {
            return c1 - c2;
         }

         if (c1 != c2) {
            return c2 - c1;
         }
      }

      if (t1._calElement != null && t2._calElement != null) {
         Duration e1 = (Duration)t1._calElement;
         Duration e2 = (Duration)t2._calElement;
         long s1 = e1.getStart(this.this$0._tz);
         long s2 = e2.getStart(this.this$0._tz);
         if (s1 < s2) {
            return -1;
         }

         if (s1 > s2) {
            return 1;
         }

         long d1 = e1.getDuration(this.this$0._tz);
         long d2 = e2.getDuration(this.this$0._tz);
         if (d1 < d2) {
            if (d1 == 0) {
               return -1;
            }

            return 1;
         }

         if (d1 > d2) {
            if (d2 == 0) {
               return 1;
            }

            return -1;
         }
      }

      return t1._sequenceNumber - t2._sequenceNumber;
   }
}
