package net.rim.device.apps.api.utility.framework;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.BitSet;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.Recur$Handle;
import net.rim.device.apps.api.framework.model.Recur$Modifier;
import net.rim.device.cldc.util.CalendarExtensions;

public class RecurCalc {
   private Calendar _fixupAndExceptionCalendar = Calendar.getInstance();
   private Calendar _cal = Calendar.getInstance();
   private CalendarExtensions _calEx = (CalendarExtensions)this._cal;
   private long[] _exclusions = new long[0];
   private long[] _inclusions = new long[0];
   private Recur$Modifier _sharedModifier = new Recur$Modifier();
   private BitSet _sharedDOWOfEvent = new BitSet(7);
   private int[] _sharedDaysBetweenEvents = new int[7];
   private static final int NUM_WEEKEND_MODIFIERS = 2;
   private static final int NUM_WEEKDAY_MODIFIERS = 5;
   private static final int NUM_DAY_MODIFIERS = 7;
   private static final int ANY_DAY = 7;
   private static final int ANY_WEEKDAY = 8;
   private static final int ANY_WEEKEND_DAY = 9;

   public synchronized boolean getAnInstance(
      long target, boolean forward, Recur$Handle handle, long eventStartInstant, long eventInstanceDuration, Recur recur, TimeZone tz
   ) {
      boolean validInstance = false;
      if (recur != null) {
         switch (recur.getRecurType()) {
            case 0:
               break;
            case 1:
            default:
               validInstance = this.getAnInstanceDaily(target, forward, handle, eventStartInstant, eventInstanceDuration, recur, tz);
               break;
            case 2:
               validInstance = this.getAnInstanceWeekly(target, forward, handle, eventStartInstant, eventInstanceDuration, recur, tz);
               break;
            case 3:
               validInstance = this.getAnInstanceMonthly(target, forward, handle, eventStartInstant, eventInstanceDuration, recur, tz);
               break;
            case 4:
               validInstance = this.getAnInstanceYearly(target, forward, handle, eventStartInstant, eventInstanceDuration, recur, tz);
         }
      }

      return validInstance;
   }

   private boolean getAnInstanceDaily(
      long target, boolean forward, Recur$Handle handle, long eventStartInstant, long eventInstanceDuration, Recur recur, TimeZone tz
   ) {
      boolean recurFinite = recur.isFinite();
      long recurEndDate = recur.getEndDate();
      long originalTarget = target;
      long[] exclusions = recur.getExclusions(this._exclusions);
      long[] inclusions = recur.getInclusions(this._inclusions);
      Calendar cal = this._cal;
      CalendarExtensions calEx = this._calEx;
      cal.setTimeZone(tz);
      if (recurFinite) {
         recurEndDate = this.timeToCompareForEnd(cal, recurEndDate, tz);
      }

      if (target < eventStartInstant) {
         if (!forward) {
            return this.ScanForValidInclusion(target, forward, inclusions, cal, exclusions, handle);
         }

         target = eventStartInstant;
      }

      if (recurFinite && target > recurEndDate) {
         if (forward) {
            return this.ScanForValidInclusion(target, forward, inclusions, cal, exclusions, handle);
         }

         target = recurEndDate;
      }

      int recurPeriod = recur.getRecurPeriod();
      if (recurPeriod == 0) {
         return false;
      }

      long recurPeriodDuration = (long)86400000 * recurPeriod;
      calEx.setTimeLong(eventStartInstant);
      int desiredHour = cal.get(11);
      int desiredMinute = cal.get(12);
      long recurStartTime = 0;
      long roughStartOffsetLong;
      if (forward) {
         roughStartOffsetLong = (target - eventStartInstant - eventInstanceDuration) / recurPeriodDuration;
      } else {
         roughStartOffsetLong = (target - eventStartInstant) / recurPeriodDuration;
      }

      roughStartOffsetLong *= recurPeriod;

      while (roughStartOffsetLong > Integer.MAX_VALUE || roughStartOffsetLong < Integer.MIN_VALUE) {
         if (roughStartOffsetLong < 0) {
            recurStartTime = this.getAdjustedTime(cal, calEx, 5, Integer.MIN_VALUE, desiredHour, desiredMinute);
            roughStartOffsetLong -= Integer.MIN_VALUE;
         } else {
            recurStartTime = this.getAdjustedTime(cal, calEx, 5, Integer.MAX_VALUE, desiredHour, desiredMinute);
            roughStartOffsetLong -= Integer.MAX_VALUE;
         }
      }

      recurStartTime = this.getAdjustedTime(cal, calEx, 5, (int)roughStartOffsetLong, desiredHour, desiredMinute);
      if (forward) {
         while (recurStartTime + eventInstanceDuration >= target && recurStartTime > eventStartInstant) {
            recurStartTime = this.getAdjustedTime(cal, calEx, 5, -recurPeriod, desiredHour, desiredMinute);
         }

         while (
            recurStartTime + eventInstanceDuration <= target && recurStartTime < target
               || this.isAnException(cal, exclusions, inclusions)
               || recurStartTime < eventStartInstant
         ) {
            recurStartTime = this.getAdjustedTime(cal, calEx, 5, recurPeriod, desiredHour, desiredMinute);
         }
      } else {
         while (recurStartTime <= target) {
            recurStartTime = this.getAdjustedTime(cal, calEx, 5, recurPeriod, desiredHour, desiredMinute);
         }

         while (recurStartTime >= target || this.isAnException(cal, exclusions, inclusions)) {
            recurStartTime = this.getAdjustedTime(cal, calEx, 5, -recurPeriod, desiredHour, desiredMinute);
         }
      }

      if (recurStartTime < eventStartInstant || recurFinite && recurStartTime > recurEndDate) {
         if (!this.getNextInclusion(originalTarget, forward, inclusions, cal)) {
            return false;
         }

         recurStartTime = ((CalendarExtensions)cal).getTimeLong();
      } else {
         recurStartTime = this.checkForInclusionInTimespan(recurStartTime, originalTarget, forward, inclusions, cal, exclusions);
      }

      handle._handle = recurStartTime;
      ((CalendarExtensions)cal).setTimeLong(recurStartTime);
      return !this.isAnException(cal, exclusions, inclusions);
   }

   private boolean getAnInstanceWeekly(
      long target, boolean forward, Recur$Handle handle, long eventStartInstant, long eventInstanceDuration, Recur recur, TimeZone tz
   ) {
      long originalTarget = target;
      boolean recurFinite = recur.isFinite();
      long recurEndDate = recur.getEndDate();
      Calendar cal = this._cal;
      CalendarExtensions calEx = this._calEx;
      cal.setTimeZone(tz);
      long[] inclusions = recur.getInclusions(this._inclusions);
      long[] exclusions = recur.getExclusions(this._exclusions);
      if (recurFinite) {
         recurEndDate = this.timeToCompareForEnd(cal, recurEndDate, tz);
      }

      if (target < eventStartInstant) {
         if (!forward) {
            return this.ScanForValidInclusion(target, forward, inclusions, cal, exclusions, handle);
         }

         target = eventStartInstant;
      }

      if (recurFinite && target > recurEndDate) {
         if (forward) {
            return this.ScanForValidInclusion(target, forward, inclusions, cal, exclusions, handle);
         }

         target = recurEndDate;
      }

      int recurPeriod = recur.getRecurPeriod();
      if (recurPeriod == 0) {
         return false;
      }

      Recur$Modifier modifier = this._sharedModifier;
      BitSet dowOfEvent = this._sharedDOWOfEvent;
      dowOfEvent.reset();
      int startDOW = recur.getFirstDayOfWeek() - 1;
      int max = recur.numModifierValues(1);

      for (int i = 0; i < max; i++) {
         recur.getModifierAt(1, i, modifier);
         modifier.parm1 -= startDOW;
         if (modifier.parm1 < 0) {
            modifier.parm1 += 7;
         }

         dowOfEvent.set(modifier.parm1);
      }

      int numSet = dowOfEvent.getNumSet();
      if (numSet == 0) {
         if (forward) {
            if (target > eventStartInstant + eventInstanceDuration) {
               return false;
            }

            handle._handle = eventStartInstant;
            return true;
         } else {
            if (target < eventStartInstant) {
               return false;
            }

            handle._handle = eventStartInstant;
            return true;
         }
      } else {
         int[] daysBetweenEvents = this._sharedDaysBetweenEvents;
         int firstSetBit = dowOfEvent.getFirstSet();
         int lastSetBit = firstSetBit;

         for (int i = 0; i < numSet - 1; i++) {
            int setBit = dowOfEvent.getNextSet(lastSetBit + 1);
            daysBetweenEvents[i] = setBit - lastSetBit;
            lastSetBit = setBit;
         }

         daysBetweenEvents[numSet - 1] = recurPeriod * 7 - (lastSetBit - firstSetBit);
         int recurIncrement = 7 * recurPeriod;
         long recurPeriodDuration = (long)86400000 * recurIncrement;
         calEx.setTimeLong(eventStartInstant);
         int desiredHour = cal.get(11);
         int desiredMinute = cal.get(12);
         int currentDOW = cal.get(7) - 1;
         currentDOW -= startDOW;
         if (currentDOW < 0) {
            currentDOW += 7;
         }

         long roughStartOffsetLong = (target - this.getAdjustedTime(cal, calEx, 5, dowOfEvent.getFirstSet() - currentDOW, desiredHour, desiredMinute))
            / recurPeriodDuration
            * recurIncrement;
         long recurStartTime = 0;

         while (roughStartOffsetLong > Integer.MAX_VALUE || roughStartOffsetLong < Integer.MIN_VALUE) {
            if (roughStartOffsetLong < 0) {
               recurStartTime = this.getAdjustedTime(cal, calEx, 5, Integer.MIN_VALUE, desiredHour, desiredMinute);
               roughStartOffsetLong -= Integer.MIN_VALUE;
            } else {
               recurStartTime = this.getAdjustedTime(cal, calEx, 5, Integer.MAX_VALUE, desiredHour, desiredMinute);
               roughStartOffsetLong -= Integer.MAX_VALUE;
            }
         }

         recurStartTime = this.getAdjustedTime(cal, calEx, 5, (int)roughStartOffsetLong, desiredHour, desiredMinute);
         int i = 0;
         if (!forward) {
            while (recurStartTime <= target) {
               recurStartTime = this.getAdjustedTime(cal, calEx, 5, daysBetweenEvents[i], desiredHour, desiredMinute);
               if (++i >= numSet) {
                  i = 0;
               }
            }

            for (;
               recurStartTime >= target || this.isAnException(cal, exclusions, inclusions);
               recurStartTime = this.getAdjustedTime(cal, calEx, 5, -daysBetweenEvents[i], desiredHour, desiredMinute)
            ) {
               if (--i < 0) {
                  i = numSet - 1;
               }
            }
         } else {
            for (;
               recurStartTime + eventInstanceDuration >= target && recurStartTime > eventStartInstant;
               recurStartTime = this.getAdjustedTime(cal, calEx, 5, -daysBetweenEvents[i], desiredHour, desiredMinute)
            ) {
               if (--i < 0) {
                  i = numSet - 1;
               }
            }

            while (
               recurStartTime + eventInstanceDuration <= target && recurStartTime < target
                  || this.isAnException(cal, exclusions, inclusions)
                  || recurStartTime < eventStartInstant
            ) {
               recurStartTime = this.getAdjustedTime(cal, calEx, 5, daysBetweenEvents[i], desiredHour, desiredMinute);
               if (++i >= numSet) {
                  i = 0;
               }
            }
         }

         if (recurStartTime < eventStartInstant || recurFinite && recurStartTime >= recurEndDate) {
            if (!this.getNextInclusion(originalTarget, forward, inclusions, cal)) {
               return false;
            }

            recurStartTime = ((CalendarExtensions)cal).getTimeLong();
         } else {
            recurStartTime = this.checkForInclusionInTimespan(recurStartTime, originalTarget, forward, inclusions, cal, exclusions);
         }

         handle._handle = recurStartTime;
         ((CalendarExtensions)cal).setTimeLong(recurStartTime);
         return !this.isAnException(cal, exclusions, inclusions);
      }
   }

   private boolean getAnInstanceMonthly(
      long target, boolean forward, Recur$Handle handle, long eventStartInstant, long eventInstanceDuration, Recur recur, TimeZone tz
   ) {
      long originalTarget = target;
      boolean recurFinite = recur.isFinite();
      long recurEndDate = recur.getEndDate();
      Calendar cal = this._cal;
      CalendarExtensions calEx = this._calEx;
      cal.setTimeZone(tz);
      if (recur.hasModifier(3, 0)) {
         recur.getModifierAt(3, 0, this._sharedModifier);
         calEx.setTimeLong(eventStartInstant);
         int dayOfMonth = cal.get(5);
         int month = cal.get(2);
         if (dayOfMonth != this._sharedModifier.parm1) {
            if (DateTimeUtilities.getNumberOfDaysInMonth(month, cal.get(1)) > this._sharedModifier.parm1) {
               cal.set(5, this._sharedModifier.parm1);
            }

            if (dayOfMonth > this._sharedModifier.parm1) {
               cal.set(2, month + 1);
            }

            eventStartInstant = calEx.getTimeLong();
         }
      }

      long[] inclusions = recur.getInclusions(this._inclusions);
      long[] exclusions = recur.getExclusions(this._exclusions);
      if (recurFinite) {
         recurEndDate = this.timeToCompareForEnd(cal, recurEndDate, tz);
      }

      if (target < eventStartInstant) {
         if (!forward) {
            return this.ScanForValidInclusion(target, forward, inclusions, cal, exclusions, handle);
         }

         target = eventStartInstant;
      }

      if (recurFinite && target > recurEndDate) {
         if (forward) {
            return this.ScanForValidInclusion(target, forward, inclusions, cal, exclusions, handle);
         }

         target = recurEndDate;
      }

      int recurPeriod = recur.getRecurPeriod();
      if (recurPeriod == 0) {
         return false;
      }

      calEx.setTimeLong(target);
      int targetYear = cal.get(1);
      int targetMonth = cal.get(2);
      calEx.setTimeLong(eventStartInstant);
      int desiredHour = cal.get(11);
      int desiredMinute = cal.get(12);
      int originalYear = cal.get(1);
      int originalMonth = cal.get(2);
      int offsetToFirst = (targetYear - originalYear) * 12;
      offsetToFirst += targetMonth - originalMonth;
      offsetToFirst /= recurPeriod;
      this.adjustTime(cal, calEx, 2, recurPeriod * offsetToFirst, desiredHour, desiredMinute);
      int dayOfWeekOffset = 0;
      int positionOfDayOfWeek = 0;
      boolean relativeToDayAndWeek = RecurUtil.isRelative(recur);
      if (relativeToDayAndWeek) {
         recur.getModifierAt(1, 0, this._sharedModifier);
         dayOfWeekOffset = this.getAdjustedDayOfWeekOffset(this._sharedModifier, recur.numModifierValues(1));
         positionOfDayOfWeek = this._sharedModifier.parm2;
         this.fixupRelativeDate(cal, -1, dayOfWeekOffset, positionOfDayOfWeek);
      }

      long recurStartTime = calEx.getTimeLong();
      if (!forward) {
         for (; recurStartTime <= target; recurStartTime = calEx.getTimeLong()) {
            calEx.setTimeLong(eventStartInstant);
            this.adjustTime(cal, calEx, 2, recurPeriod * ++offsetToFirst, desiredHour, desiredMinute);
            if (relativeToDayAndWeek) {
               this.fixupRelativeDate(cal, -1, dayOfWeekOffset, positionOfDayOfWeek);
            } else {
               this.fixupNonRelativeDate(cal, eventStartInstant, recur);
            }
         }

         for (; recurStartTime >= target || this.isAnException(cal, exclusions, inclusions); recurStartTime = calEx.getTimeLong()) {
            calEx.setTimeLong(eventStartInstant);
            this.adjustTime(cal, calEx, 2, recurPeriod * --offsetToFirst, desiredHour, desiredMinute);
            if (relativeToDayAndWeek) {
               this.fixupRelativeDate(cal, -1, dayOfWeekOffset, positionOfDayOfWeek);
            } else {
               this.fixupNonRelativeDate(cal, eventStartInstant, recur);
            }
         }
      } else {
         for (; recurStartTime + eventInstanceDuration >= target && recurStartTime > eventStartInstant; recurStartTime = calEx.getTimeLong()) {
            calEx.setTimeLong(eventStartInstant);
            this.adjustTime(cal, calEx, 2, recurPeriod * --offsetToFirst, desiredHour, desiredMinute);
            if (relativeToDayAndWeek) {
               this.fixupRelativeDate(cal, -1, dayOfWeekOffset, positionOfDayOfWeek);
            } else {
               this.fixupNonRelativeDate(cal, eventStartInstant, recur);
            }
         }

         for (;
            recurStartTime + eventInstanceDuration <= target && recurStartTime < target
               || this.isAnException(cal, exclusions, inclusions)
               || recurStartTime < eventStartInstant;
            recurStartTime = calEx.getTimeLong()
         ) {
            calEx.setTimeLong(eventStartInstant);
            this.adjustTime(cal, calEx, 2, recurPeriod * ++offsetToFirst, desiredHour, desiredMinute);
            if (relativeToDayAndWeek) {
               this.fixupRelativeDate(cal, -1, dayOfWeekOffset, positionOfDayOfWeek);
            } else {
               this.fixupNonRelativeDate(cal, eventStartInstant, recur);
            }
         }
      }

      if (recurStartTime < eventStartInstant || recurFinite && recurStartTime > recurEndDate) {
         if (!this.getNextInclusion(originalTarget, forward, inclusions, cal)) {
            return false;
         }

         recurStartTime = ((CalendarExtensions)cal).getTimeLong();
      } else {
         recurStartTime = this.checkForInclusionInTimespan(recurStartTime, originalTarget, forward, inclusions, cal, exclusions);
      }

      handle._handle = recurStartTime;
      ((CalendarExtensions)cal).setTimeLong(recurStartTime);
      return !this.isAnException(cal, exclusions, inclusions);
   }

   private boolean getAnInstanceYearly(
      long target, boolean forward, Recur$Handle handle, long eventStartInstant, long eventInstanceDuration, Recur recur, TimeZone tz
   ) {
      long originalTarget = target;
      boolean recurFinite = recur.isFinite();
      long recurEndDate = recur.getEndDate();
      Calendar cal = this._cal;
      CalendarExtensions calEx = this._calEx;
      cal.setTimeZone(tz);
      long[] inclusions = recur.getInclusions(this._inclusions);
      long[] exclusions = recur.getExclusions(this._exclusions);
      if (recurFinite) {
         recurEndDate = this.timeToCompareForEnd(cal, recurEndDate, tz);
      }

      if (target < eventStartInstant) {
         if (!forward) {
            return this.ScanForValidInclusion(target, forward, inclusions, cal, exclusions, handle);
         }

         target = eventStartInstant;
      }

      if (recurFinite && target > recurEndDate) {
         if (forward) {
            return this.ScanForValidInclusion(target, forward, inclusions, cal, exclusions, handle);
         }

         target = recurEndDate;
      }

      int recurPeriod = recur.getRecurPeriod();
      if (recurPeriod == 0) {
         return false;
      }

      calEx.setTimeLong(target);
      int targetYear = cal.get(1);
      calEx.setTimeLong(eventStartInstant);
      int originalYear = cal.get(1);
      int desiredHour = cal.get(11);
      int desiredMinute = cal.get(12);
      int offsetToFirst = targetYear - originalYear;
      offsetToFirst /= recurPeriod;
      this.adjustTime(cal, calEx, 1, recurPeriod * offsetToFirst, desiredHour, desiredMinute);
      int monthOfYearOffset = 0;
      int dayOfWeekOffset = 0;
      int positionOfDayOfWeek = 0;
      boolean relativeToDayAndWeek = RecurUtil.isRelative(recur);
      if (relativeToDayAndWeek) {
         recur.getModifierAt(1, 0, this._sharedModifier);
         dayOfWeekOffset = this.getAdjustedDayOfWeekOffset(this._sharedModifier, recur.numModifierValues(1));
         positionOfDayOfWeek = this._sharedModifier.parm2;
         recur.getModifierAt(2, 0, this._sharedModifier);
         monthOfYearOffset = this._sharedModifier.parm1;
         this.fixupRelativeDate(cal, monthOfYearOffset, dayOfWeekOffset, positionOfDayOfWeek);
      }

      long recurStartTime = calEx.getTimeLong();
      if (!forward) {
         for (; recurStartTime <= target; recurStartTime = calEx.getTimeLong()) {
            calEx.setTimeLong(eventStartInstant);
            this.adjustTime(cal, calEx, 1, recurPeriod * ++offsetToFirst, desiredHour, desiredMinute);
            if (relativeToDayAndWeek) {
               this.fixupRelativeDate(cal, monthOfYearOffset, dayOfWeekOffset, positionOfDayOfWeek);
            }
         }

         for (; recurStartTime >= target || this.isAnException(cal, exclusions, inclusions); recurStartTime = calEx.getTimeLong()) {
            calEx.setTimeLong(eventStartInstant);
            this.adjustTime(cal, calEx, 1, recurPeriod * --offsetToFirst, desiredHour, desiredMinute);
            if (relativeToDayAndWeek) {
               this.fixupRelativeDate(cal, monthOfYearOffset, dayOfWeekOffset, positionOfDayOfWeek);
            }
         }
      } else {
         for (; recurStartTime + eventInstanceDuration >= target && recurStartTime > eventStartInstant; recurStartTime = calEx.getTimeLong()) {
            calEx.setTimeLong(eventStartInstant);
            this.adjustTime(cal, calEx, 1, recurPeriod * --offsetToFirst, desiredHour, desiredMinute);
            if (relativeToDayAndWeek) {
               this.fixupRelativeDate(cal, monthOfYearOffset, dayOfWeekOffset, positionOfDayOfWeek);
            }
         }

         for (;
            recurStartTime + eventInstanceDuration <= target && recurStartTime < target
               || this.isAnException(cal, exclusions, inclusions)
               || recurStartTime < eventStartInstant;
            recurStartTime = calEx.getTimeLong()
         ) {
            calEx.setTimeLong(eventStartInstant);
            this.adjustTime(cal, calEx, 1, recurPeriod * ++offsetToFirst, desiredHour, desiredMinute);
            if (relativeToDayAndWeek) {
               this.fixupRelativeDate(cal, monthOfYearOffset, dayOfWeekOffset, positionOfDayOfWeek);
            }
         }
      }

      if (recurStartTime < eventStartInstant || recurFinite && recurStartTime > recurEndDate) {
         if (!this.getNextInclusion(originalTarget, forward, inclusions, cal)) {
            return false;
         }

         recurStartTime = ((CalendarExtensions)cal).getTimeLong();
      } else {
         recurStartTime = this.checkForInclusionInTimespan(recurStartTime, originalTarget, forward, inclusions, cal, exclusions);
      }

      handle._handle = recurStartTime;
      ((CalendarExtensions)cal).setTimeLong(recurStartTime);
      return !this.isAnException(cal, exclusions, inclusions);
   }

   public synchronized boolean getFromKnownInstance(
      long knownInstance, boolean forward, Recur$Handle handle, long eventStartInstant, long eventInstanceDuration, Recur recur, TimeZone tz
   ) {
      boolean validInstance = false;
      if (recur != null) {
         switch (recur.getRecurType()) {
            case 0:
               break;
            case 1:
            default:
               validInstance = this.getFromKnownInstanceDaily(knownInstance, forward, handle, eventStartInstant, eventInstanceDuration, recur, tz);
               break;
            case 2:
               validInstance = this.getFromKnownInstanceWeekly(knownInstance, forward, handle, eventStartInstant, eventInstanceDuration, recur, tz);
               break;
            case 3:
               validInstance = this.getFromKnownInstanceMonthly(knownInstance, forward, handle, eventStartInstant, eventInstanceDuration, recur, tz);
               break;
            case 4:
               validInstance = this.getFromKnownInstanceYearly(knownInstance, forward, handle, eventStartInstant, eventInstanceDuration, recur, tz);
         }
      }

      return validInstance;
   }

   private boolean getFromKnownInstanceDaily(
      long knownInstance, boolean forward, Recur$Handle handle, long eventStartInstant, long eventInstanceDuration, Recur recur, TimeZone tz
   ) {
      long[] exclusions = recur.getExclusions(this._exclusions);
      long[] inclusions = recur.getInclusions(this._inclusions);
      if (inclusions != null && this._inclusions.length > 0 && Arrays.binarySearch(this._inclusions, knownInstance, 0, this._inclusions.length) >= 0) {
         long timeshift = 0;
         if (forward) {
            timeshift = eventInstanceDuration - 1;
         } else {
            timeshift = -(eventInstanceDuration - 1);
         }

         if (eventInstanceDuration == 0) {
            eventInstanceDuration = 1;
            timeshift *= -1;
         }

         return this.getAnInstanceDaily(knownInstance + timeshift, forward, handle, eventStartInstant, eventInstanceDuration - 1, recur, tz);
      } else {
         int recurPeriod = recur.getRecurPeriod();
         if (recurPeriod == 0) {
            return false;
         }

         if (!forward) {
            recurPeriod = -recurPeriod;
         }

         Calendar cal = this._cal;
         CalendarExtensions calEx = this._calEx;
         cal.setTimeZone(tz);
         calEx.setTimeLong(knownInstance);
         int desiredHour = cal.get(11);
         int desiredMinute = cal.get(12);
         if (desiredHour >= 0 && desiredHour <= 4 || desiredHour >= 23 && desiredHour <= 24) {
            calEx.setTimeLong(eventStartInstant);
            desiredHour = cal.get(11);
            desiredMinute = cal.get(12);
            calEx.setTimeLong(knownInstance);
         }

         do {
            this.adjustTime(cal, calEx, 5, recurPeriod, desiredHour, desiredMinute);
         } while (this.isAnException(cal, exclusions, inclusions));

         long recurStartTime = calEx.getTimeLong();
         if (!this.isTimeWithinRecurrenceWindow(recurStartTime, eventStartInstant, recur.getEndDate(), eventInstanceDuration, recur.isFinite(), cal, tz)) {
            if (!this.getNextInclusion(knownInstance, forward, inclusions, cal)) {
               return false;
            }

            recurStartTime = ((CalendarExtensions)cal).getTimeLong();
         } else {
            recurStartTime = this.checkForInclusionInTimespan(recurStartTime, knownInstance, forward, inclusions, cal, exclusions);
         }

         handle._handle = recurStartTime;
         ((CalendarExtensions)cal).setTimeLong(recurStartTime);
         return !this.isAnException(cal, exclusions, inclusions);
      }
   }

   private boolean getFromKnownInstanceWeekly(
      long knownInstance, boolean forward, Recur$Handle handle, long eventStartInstant, long eventInstanceDuration, Recur recur, TimeZone tz
   ) {
      long[] exclusions = recur.getExclusions(this._exclusions);
      long[] inclusions = recur.getInclusions(this._inclusions);
      if (inclusions != null && this._inclusions.length > 0 && Arrays.binarySearch(this._inclusions, knownInstance, 0, this._inclusions.length) >= 0) {
         long timeshift = 0;
         if (forward) {
            timeshift = eventInstanceDuration - 1;
         } else {
            timeshift = -(eventInstanceDuration - 1);
         }

         if (eventInstanceDuration == 0) {
            eventInstanceDuration = 1;
            timeshift *= -1;
         }

         return this.getAnInstanceWeekly(knownInstance + timeshift, forward, handle, eventStartInstant, eventInstanceDuration - 1, recur, tz);
      } else {
         int recurPeriod = recur.getRecurPeriod();
         if (recurPeriod == 0) {
            return false;
         }

         Recur$Modifier modifier = this._sharedModifier;
         BitSet dowOfEvent = this._sharedDOWOfEvent;
         dowOfEvent.reset();
         Calendar cal = this._cal;
         CalendarExtensions calEx = this._calEx;
         cal.setTimeZone(tz);
         int startDOW = recur.getFirstDayOfWeek() - 1;
         int max = recur.numModifierValues(1);

         for (int i = 0; i < max; i++) {
            recur.getModifierAt(1, i, modifier);
            modifier.parm1 -= startDOW;
            if (modifier.parm1 < 0) {
               modifier.parm1 += 7;
            }

            dowOfEvent.set(modifier.parm1);
         }

         if (dowOfEvent.getNumSet() == 0) {
            return false;
         }

         calEx.setTimeLong(knownInstance);
         int desiredHour = cal.get(11);
         int desiredMinute = cal.get(12);
         if (desiredHour >= 0 && desiredHour <= 4 || desiredHour >= 23 && desiredHour <= 24) {
            calEx.setTimeLong(eventStartInstant);
            desiredHour = cal.get(11);
            desiredMinute = cal.get(12);
            calEx.setTimeLong(knownInstance);
         }

         do {
            int lastDOW = cal.get(7) - 1;
            lastDOW -= startDOW;
            if (lastDOW < 0) {
               lastDOW += 7;
            }

            int nextDOW;
            if (forward) {
               nextDOW = dowOfEvent.getNextSet(lastDOW + 1);
               if (nextDOW < 0) {
                  nextDOW = recurPeriod * 7 + dowOfEvent.getFirstSet();
               }
            } else {
               nextDOW = dowOfEvent.getPreviousSet(lastDOW - 1);
               if (nextDOW < 0) {
                  nextDOW = -recurPeriod * 7 + dowOfEvent.getLastSet();
               }
            }

            this.adjustTime(cal, calEx, 5, nextDOW - lastDOW, desiredHour, desiredMinute);
         } while (this.isAnException(cal, exclusions, inclusions));

         long recurStartTime = calEx.getTimeLong();
         if (!this.isTimeWithinRecurrenceWindow(recurStartTime, eventStartInstant, recur.getEndDate(), eventInstanceDuration, recur.isFinite(), cal, tz)) {
            if (!this.getNextInclusion(knownInstance, forward, inclusions, cal)) {
               return false;
            }

            recurStartTime = ((CalendarExtensions)cal).getTimeLong();
         } else {
            recurStartTime = this.checkForInclusionInTimespan(recurStartTime, knownInstance, forward, inclusions, cal, exclusions);
         }

         handle._handle = recurStartTime;
         ((CalendarExtensions)cal).setTimeLong(recurStartTime);
         return !this.isAnException(cal, exclusions, inclusions);
      }
   }

   private boolean getFromKnownInstanceMonthly(
      long knownInstance, boolean forward, Recur$Handle handle, long eventStartInstant, long eventInstanceDuration, Recur recur, TimeZone tz
   ) {
      long[] exclusions = recur.getExclusions(this._exclusions);
      long[] inclusions = recur.getInclusions(this._inclusions);
      if (inclusions != null && this._inclusions.length > 0 && Arrays.binarySearch(this._inclusions, knownInstance, 0, this._inclusions.length) >= 0) {
         long timeshift = 0;
         if (forward) {
            timeshift = eventInstanceDuration - 1;
         } else {
            timeshift = -(eventInstanceDuration - 1);
         }

         if (eventInstanceDuration == 0) {
            eventInstanceDuration = 1;
            timeshift *= -1;
         }

         return this.getAnInstanceMonthly(knownInstance + timeshift, forward, handle, eventStartInstant, eventInstanceDuration - 1, recur, tz);
      } else {
         int recurPeriod = recur.getRecurPeriod();
         if (recurPeriod == 0) {
            return false;
         }

         if (!forward) {
            recurPeriod = -recurPeriod;
         }

         Calendar cal = this._cal;
         CalendarExtensions calEx = this._calEx;
         cal.setTimeZone(tz);
         calEx.setTimeLong(knownInstance);
         int desiredHour = cal.get(11);
         int desiredMinute = cal.get(12);
         if (desiredHour >= 0 && desiredHour <= 4 || desiredHour >= 23 && desiredHour <= 24) {
            calEx.setTimeLong(eventStartInstant);
            desiredHour = cal.get(11);
            desiredMinute = cal.get(12);
            calEx.setTimeLong(knownInstance);
         }

         int dayOfWeekOffset = 0;
         int positionOfDayOfWeek = 0;
         boolean relativeToDayAndWeek = RecurUtil.isRelative(recur);
         if (relativeToDayAndWeek) {
            recur.getModifierAt(1, 0, this._sharedModifier);
            dayOfWeekOffset = this.getAdjustedDayOfWeekOffset(this._sharedModifier, recur.numModifierValues(1));
            positionOfDayOfWeek = this._sharedModifier.parm2;
         }

         do {
            this.adjustTime(cal, calEx, 2, recurPeriod, desiredHour, desiredMinute);
            if (relativeToDayAndWeek) {
               this.fixupRelativeDate(cal, -1, dayOfWeekOffset, positionOfDayOfWeek);
            } else {
               this.fixupNonRelativeDate(cal, eventStartInstant, recur);
            }
         } while (this.isAnException(cal, exclusions, inclusions));

         long recurStartTime = calEx.getTimeLong();
         if (!this.isTimeWithinRecurrenceWindow(recurStartTime, eventStartInstant, recur.getEndDate(), eventInstanceDuration, recur.isFinite(), cal, tz)) {
            if (!this.getNextInclusion(knownInstance, forward, inclusions, cal)) {
               return false;
            }

            recurStartTime = ((CalendarExtensions)cal).getTimeLong();
         } else {
            recurStartTime = this.checkForInclusionInTimespan(recurStartTime, knownInstance, forward, inclusions, cal, exclusions);
         }

         handle._handle = recurStartTime;
         ((CalendarExtensions)cal).setTimeLong(recurStartTime);
         return !this.isAnException(cal, exclusions, inclusions);
      }
   }

   private boolean getFromKnownInstanceYearly(
      long knownInstance, boolean forward, Recur$Handle handle, long eventStartInstant, long eventInstanceDuration, Recur recur, TimeZone tz
   ) {
      long[] exclusions = recur.getExclusions(this._exclusions);
      long[] inclusions = recur.getInclusions(this._inclusions);
      if (inclusions != null && this._inclusions.length > 0 && Arrays.binarySearch(this._inclusions, knownInstance, 0, this._inclusions.length) >= 0) {
         long timeshift = 0;
         if (forward) {
            timeshift = eventInstanceDuration - 1;
         } else {
            timeshift = -(eventInstanceDuration - 1);
         }

         if (eventInstanceDuration == 0) {
            eventInstanceDuration = 1;
            timeshift *= -1;
         }

         return this.getAnInstanceYearly(knownInstance + timeshift, forward, handle, eventStartInstant, eventInstanceDuration - 1, recur, tz);
      } else {
         int recurPeriod = recur.getRecurPeriod();
         if (recurPeriod == 0) {
            return false;
         }

         if (!forward) {
            recurPeriod = -recurPeriod;
         }

         Calendar cal = this._cal;
         CalendarExtensions calEx = this._calEx;
         cal.setTimeZone(tz);
         calEx.setTimeLong(knownInstance);
         int desiredHour = cal.get(11);
         int desiredMinute = cal.get(12);
         if (desiredHour >= 0 && desiredHour <= 4 || desiredHour >= 23 && desiredHour <= 24) {
            calEx.setTimeLong(eventStartInstant);
            desiredHour = cal.get(11);
            desiredMinute = cal.get(12);
            calEx.setTimeLong(knownInstance);
         }

         int monthOfYearOffset = 0;
         int dayOfWeekOffset = 0;
         int positionOfDayOfWeek = 0;
         boolean relativeToDayAndWeek = RecurUtil.isRelative(recur);
         if (relativeToDayAndWeek) {
            recur.getModifierAt(1, 0, this._sharedModifier);
            dayOfWeekOffset = this.getAdjustedDayOfWeekOffset(this._sharedModifier, recur.numModifierValues(1));
            positionOfDayOfWeek = this._sharedModifier.parm2;
            recur.getModifierAt(2, 0, this._sharedModifier);
            monthOfYearOffset = this._sharedModifier.parm1;
         }

         do {
            this.adjustTime(cal, calEx, 1, recurPeriod, desiredHour, desiredMinute);
            if (relativeToDayAndWeek) {
               this.fixupRelativeDate(cal, monthOfYearOffset, dayOfWeekOffset, positionOfDayOfWeek);
            } else {
               this.fixupNonRelativeDate(cal, eventStartInstant, recur);
            }
         } while (this.isAnException(cal, exclusions, inclusions));

         long recurStartTime = calEx.getTimeLong();
         if (!this.isTimeWithinRecurrenceWindow(recurStartTime, eventStartInstant, recur.getEndDate(), eventInstanceDuration, recur.isFinite(), cal, tz)) {
            if (!this.getNextInclusion(knownInstance, forward, inclusions, cal)) {
               return false;
            }

            recurStartTime = ((CalendarExtensions)cal).getTimeLong();
         } else {
            recurStartTime = this.checkForInclusionInTimespan(recurStartTime, knownInstance, forward, inclusions, cal, exclusions);
         }

         handle._handle = recurStartTime;
         ((CalendarExtensions)cal).setTimeLong(recurStartTime);
         return !this.isAnException(cal, exclusions, inclusions);
      }
   }

   private void fixupRelativeDate(Calendar cal, int monthOfYearOffset, int dayOfWeekOffset, int positionOfDayOfWeek) {
      if (positionOfDayOfWeek < 0) {
         positionOfDayOfWeek += 6;
      }

      if (positionOfDayOfWeek < 1) {
         positionOfDayOfWeek = 1;
      } else if (positionOfDayOfWeek > 5) {
         positionOfDayOfWeek = 5;
      }

      CalendarExtensions calEx = (CalendarExtensions)cal;
      if (monthOfYearOffset >= 0) {
         cal.set(2, monthOfYearOffset + 0);
      }

      if (dayOfWeekOffset <= 6) {
         int date = cal.get(5) - 1;
         int dow = cal.get(7) - 1;
         int targetDate = date + (7 + dayOfWeekOffset - dow) % 7;
         int numWeeksIn = targetDate / 7 + 1;
         targetDate += (positionOfDayOfWeek - numWeeksIn) * 7;
         cal.set(5, targetDate + 1);
         if (cal.get(5) != targetDate + 1) {
            targetDate -= 7;
            calEx.add(2, -1);
            cal.set(5, targetDate + 1);
         }
      } else {
         cal.set(5, 1);
         if (positionOfDayOfWeek == 5) {
            calEx.add(2, 1);
            calEx.add(5, -1);
            int dayOfWeek = cal.get(7);
            if (dayOfWeekOffset != 8 || dayOfWeek != 1 && dayOfWeek != 7) {
               if (dayOfWeekOffset == 9 && dayOfWeek != 1 && dayOfWeek != 7) {
                  calEx.add(5, 1 - dayOfWeek);
               }
            } else {
               calEx.add(5, dayOfWeek == 1 ? -2 : -1);
            }
         } else {
            int currentPosition = 0;

            while (currentPosition != positionOfDayOfWeek) {
               int dayOfWeek = cal.get(7);
               if (dayOfWeekOffset == 7) {
                  currentPosition++;
               } else if (dayOfWeekOffset == 8 && dayOfWeek != 1 && dayOfWeek != 7) {
                  currentPosition++;
               } else if (dayOfWeekOffset == 9 && (dayOfWeek == 1 || dayOfWeek == 7)) {
                  currentPosition++;
               }

               if (currentPosition != positionOfDayOfWeek) {
                  calEx.add(5, 1);
               }
            }
         }
      }
   }

   private void fixupNonRelativeDate(Calendar occurrence, long recurrenceStart, Recur recur) {
      Calendar cal = this._fixupAndExceptionCalendar;
      CalendarExtensions calEx = (CalendarExtensions)cal;
      cal.setTimeZone(occurrence.getTimeZone());
      long occurrenceDate = ((CalendarExtensions)occurrence).getTimeLong();
      calEx.setTimeLong(recurrenceStart);
      int occurrenceDay = occurrence.get(5);
      int recurrenceStartDay = RecurUtil.getDayOfMonth(recur);
      if (recurrenceStartDay == -1) {
         recurrenceStartDay = cal.get(5);
      }

      if (occurrenceDay != recurrenceStartDay) {
         calEx.setTimeLong(occurrenceDate);
         int monthBefore = cal.get(2);
         cal.set(5, recurrenceStartDay);
         int monthAfter = cal.get(2);
         if (monthBefore != monthAfter) {
            cal.set(5, 1);
            cal.set(11, 0);
            cal.set(12, 0);
            cal.set(13, 0);
            cal.set(14, 0);
            calEx.add(14, -1);
            recurrenceStartDay = cal.get(5);
         }

         occurrence.set(5, recurrenceStartDay);
      }
   }

   public boolean isAnException(Calendar occurrence, long[] exclusions, long[] inclusions) {
      if (exclusions != null && exclusions.length > 0) {
         long timeToCheck = ((CalendarExtensions)occurrence).getTimeLong();
         if (Arrays.binarySearch(inclusions, timeToCheck, 0, inclusions.length) >= 0) {
            return false;
         }

         int exclusionIndex = Arrays.binarySearch(exclusions, timeToCheck, 0, exclusions.length);
         if (exclusionIndex >= 0) {
            return true;
         }

         int year = occurrence.get(1);
         int month = occurrence.get(2);
         int day = occurrence.get(5);
         Calendar cal = this._fixupAndExceptionCalendar;
         cal.setTimeZone(occurrence.getTimeZone());
         exclusionIndex = -exclusionIndex - 1;
         if (exclusionIndex < exclusions.length) {
            ((CalendarExtensions)cal).setTimeLong(exclusions[exclusionIndex]);
            if (cal.get(1) == year && cal.get(2) == month && cal.get(5) == day) {
               return true;
            }
         }

         if (exclusionIndex > 0) {
            ((CalendarExtensions)cal).setTimeLong(exclusions[exclusionIndex - 1]);
            if (cal.get(1) == year && cal.get(2) == month && cal.get(5) == day) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean isAnInclusion(Calendar occurrence, long[] inclusions) {
      if (inclusions != null && inclusions.length > 0) {
         long timeToCheck = ((CalendarExtensions)occurrence).getTimeLong();
         return Arrays.binarySearch(inclusions, timeToCheck, 0, inclusions.length) >= 0;
      } else {
         return false;
      }
   }

   private long timeToCompareForEnd(Calendar cal, long endDate, TimeZone tz) {
      CalendarExtensions calEx = (CalendarExtensions)cal;
      TimeZone recurTimeZone = tz;
      TimeZone oldTimeZone = cal.getTimeZone();
      cal.setTimeZone(recurTimeZone);
      calEx.setTimeLong(endDate);
      calEx.add(5, 1);
      cal.set(11, 0);
      cal.set(12, 0);
      cal.set(13, 0);
      cal.set(14, 0);
      int month = cal.get(2);
      int day = cal.get(5);
      int year = cal.get(1);
      cal.setTimeZone(oldTimeZone);
      cal.set(2, month);
      cal.set(5, day);
      cal.set(1, year);
      return calEx.getTimeLong() - 1;
   }

   private boolean isTimeWithinRecurrenceWindow(
      long time, long recurrenceStart, long recurrenceEnd, long occurrenceDuration, boolean recurrenceIsFinite, Calendar cal, TimeZone tz
   ) {
      if (time < recurrenceStart) {
         return false;
      }

      if (recurrenceIsFinite) {
         recurrenceEnd = this.timeToCompareForEnd(cal, recurrenceEnd, tz);
         if (time >= recurrenceEnd) {
            return false;
         }
      }

      return true;
   }

   private int getAdjustedDayOfWeekOffset(Recur$Modifier modifier, int numModifiers) {
      int dayOfWeekOffset = modifier.parm1;
      if (numModifiers == 2) {
         return 9;
      }

      if (numModifiers == 5) {
         return 8;
      }

      if (numModifiers == 7) {
         dayOfWeekOffset = 7;
      }

      return dayOfWeekOffset;
   }

   private long getAdjustedTime(Calendar cal, CalendarExtensions calEx, int fieldToAdjust, int adjustmentAmount, int desiredHour, int desiredMinute) {
      this.adjustTime(cal, calEx, fieldToAdjust, adjustmentAmount, desiredHour, desiredMinute);
      return calEx.getTimeLong();
   }

   private void adjustTime(Calendar cal, CalendarExtensions calEx, int fieldToAdjust, int adjustmentAmount, int desiredHour, int desiredMinute) {
      calEx.add(fieldToAdjust, adjustmentAmount);
      int currentHour = cal.get(11);
      int currentMinute = cal.get(12);
      if (currentHour != desiredHour) {
         if (currentHour == 23 && desiredHour == 0) {
            calEx.add(10, 1);
         } else if (currentHour == 0 && desiredHour == 23) {
            calEx.add(10, -1);
         }

         cal.set(11, desiredHour);
      }

      if (currentMinute != desiredMinute) {
         cal.set(12, desiredMinute);
      }
   }

   private boolean getNextInclusion(long startTime, boolean forward, long[] inclusions, Calendar cal) {
      int indexToUse = 0;
      boolean exactmatch = false;
      if (inclusions != null && inclusions.length > 0) {
         int inclusionIndex = Arrays.binarySearch(inclusions, startTime, 0, inclusions.length);
         if (inclusionIndex >= 0) {
            exactmatch = true;
         } else {
            inclusionIndex = -inclusionIndex - 1;
         }

         if (forward && exactmatch) {
            indexToUse = inclusionIndex + 1;
         } else if (exactmatch) {
            indexToUse = inclusionIndex - 1;
         } else {
            indexToUse = inclusionIndex;
         }

         if (indexToUse < 0) {
            if (!forward) {
               return false;
            }

            indexToUse = 0;
         }

         if (indexToUse > inclusions.length - 1) {
            if (forward) {
               return false;
            }

            indexToUse = inclusions.length - 1;
         }

         if (forward) {
            if (inclusions[indexToUse] <= startTime) {
               if (exactmatch) {
                  return false;
               }

               return this.getNextInclusion(inclusions[indexToUse], forward, inclusions, cal);
            }
         } else if (inclusions[indexToUse] >= startTime) {
            if (exactmatch) {
               return false;
            }

            return this.getNextInclusion(inclusions[indexToUse], forward, inclusions, cal);
         }

         ((CalendarExtensions)cal).setTimeLong(inclusions[indexToUse]);
         return true;
      } else {
         return false;
      }
   }

   private boolean ScanForValidInclusion(long startTime, boolean forward, long[] inclusions, Calendar cal, long[] exclusions, Recur$Handle handle) {
      if (inclusions.length > 0) {
         if (!this.getNextInclusion(startTime, forward, inclusions, cal)) {
            return false;
         }

         handle._handle = ((CalendarExtensions)cal).getTimeLong();
         return true;
      } else {
         return false;
      }
   }

   private long checkForInclusionInTimespan(long knownInstance, long endtime, boolean forward, long[] inclusions, Calendar cal, long[] exclusions) {
      long returnTime = knownInstance;
      if (this.getNextInclusion(endtime, forward, inclusions, cal)) {
         long inclusionTime = ((CalendarExtensions)cal).getTimeLong();
         if (forward) {
            if (inclusionTime < knownInstance) {
               returnTime = inclusionTime;
            }
         } else if (inclusionTime > knownInstance) {
            returnTime = inclusionTime;
         }
      }

      return returnTime;
   }
}
