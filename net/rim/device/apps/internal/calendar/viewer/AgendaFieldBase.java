package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.controller.Duration;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.ui.IconCollection;
import net.rim.vm.Array;

class AgendaFieldBase extends DayList {
   protected int _moreEvents;
   private static final IconCollection STATUS_ICONS = IconCollection.get("net_rim_Calendar_MeetingConflict", 1);
   private static final long MIN_EVENT_TIME_THRESHOLD = 1209600000L;
   private static final long MAX_EVENT_TIME_THRESHOLD = 1382400000L;

   protected AgendaFieldBase() {
      super(false);
      super._splitDaysThatSpanMidnight = true;
      this.setSummaryLines(2);
      this.setDisplayTimeBars(false);
      this.setDisplayEndTimes(false);
      this.setDisplayLines(false);
      this.setClampTimes(false);
      this.setDisplayInformationIcons(true);
   }

   public boolean eventsExistBeforeFirst() {
      return (this._moreEvents & 2) != 0;
   }

   public boolean eventsExistAfterLast() {
      return (this._moreEvents & 1) != 0;
   }

   @Override
   protected void addFreeTimeToAllDays(long loadTime) {
      if (!CalendarOptions.getOptions().useLegacyAgendaView()) {
         long startTime = this.getLongTimeFromStartFromEventAndTime(System.currentTimeMillis(), CalendarOptions.getOptions().getDayStart());
         this.addFreeTimeTransitionsForTimeRange(Math.max(this.getStartOfList(), startTime), 0, this.getEndOfList(), loadTime);
      }
   }

   @Override
   protected long addFreeTimeTransition(DayList$TransitionList list, int index, int sequenceNumber, long loadTime) {
      long endOfLastFreeBlock = 0;
      long currentTime = System.currentTimeMillis();
      TimeZone tz = super._tz;
      Duration currentEvent = list.getEventAt(index);
      long startRange = this.getLongTimeFromStartFromEventAndTime(Math.max(currentTime, this.getStartOfList()), CalendarOptions.getOptions().getDayStart());
      if (!list.shouldIgnore(index) && !list.canSkipFreeTimeProcessing(currentEvent) && currentEvent.getStart(tz) + currentEvent.getDuration(tz) >= startRange) {
         Duration previousEvent = list.getPreviousEvent(index);
         Duration nextEvent = list.getNextEvent(index, true);
         long startOfCurrentAppointment = currentEvent.getStart(tz);
         long durationOfCurrentAppointment = currentEvent.getDuration(tz);
         long endOfCurrentAppointment = startOfCurrentAppointment + durationOfCurrentAppointment;
         long startOfNextAppointment = 0;

         while (nextEvent != null) {
            startOfNextAppointment = nextEvent.getStart(tz);
            if (startOfNextAppointment >= endOfCurrentAppointment) {
               break;
            }

            list.addConflict(index);
            list.addConflict(index + 1);
            endOfCurrentAppointment = Math.max(endOfCurrentAppointment, nextEvent.getStart(tz) + nextEvent.getDuration(tz));
            nextEvent = list.getNextEvent(++index, true);
         }

         if (!CalendarOptions.getOptions().useLegacyAgendaView()) {
            if (this.timeOutOfBounds(startOfCurrentAppointment, endOfCurrentAppointment, loadTime)) {
               return 0;
            }

            if (previousEvent == null && startOfCurrentAppointment > currentTime) {
               endOfLastFreeBlock = this.addFreeTimeTransitionsForTimeRange(startRange, sequenceNumber, startOfCurrentAppointment, loadTime);
            } else if (previousEvent != null
               && previousEvent.getStart(tz) + previousEvent.getDuration(tz) < startRange
               && startOfCurrentAppointment > currentTime) {
               long freeTimeStart = Math.max(previousEvent.getStart(tz) + previousEvent.getDuration(tz), startRange);
               endOfLastFreeBlock = this.addFreeTimeTransitionsForTimeRange(freeTimeStart, sequenceNumber, startOfCurrentAppointment, loadTime);
            }

            if (nextEvent == null) {
               startOfNextAppointment = this.getLongTimeFromStartFromEventAndTime(this.getEndOfList(), CalendarOptions.getOptions().getDayEnd());
            }

            if (startOfNextAppointment < currentTime) {
               endOfLastFreeBlock = 0;
            } else {
               endOfLastFreeBlock = this.addFreeTimeTransitionsForTimeRange(endOfCurrentAppointment, sequenceNumber, startOfNextAppointment, loadTime);
            }
         }

         return endOfLastFreeBlock;
      } else {
         return 0;
      }
   }

   private boolean timeOutOfBounds(long startOfCurrentAppointment, long endOfCurrentAppointment, long loadTime) {
      return startOfCurrentAppointment > this.getEndOfList() ? true : endOfCurrentAppointment < this.getStartOfList();
   }

   private long addFreeTimeTransitionsForTimeRange(long nextFreeTimeBlockStart, int sequenceNumber, long nextFreeTimeBlockEnd, long loadTime) {
      CalendarExtensions calEx = (CalendarExtensions)super._cal;
      long endOfLastFreeBlock = nextFreeTimeBlockStart;
      long threshhold = CalendarOptions.getOptions().getFreeTimeBlockThreshhold() * 60000;
      int preferredStartHour = CalendarOptions.getOptions().getDayStart() / 3600000 % 24;
      int preferredEndHour = CalendarOptions.getOptions().getDayEnd() / 3600000 % 24;
      long startOfDay = this.getLongTimeFromStartFromEventAndTime(nextFreeTimeBlockStart, CalendarOptions.getOptions().getDayStart());
      long endOfDay = this.getLongTimeFromStartFromEventAndTime(nextFreeTimeBlockStart, CalendarOptions.getOptions().getDayEnd());
      nextFreeTimeBlockStart = Math.max(this.getStartOfList(), nextFreeTimeBlockStart);
      nextFreeTimeBlockEnd = Math.min(this.getEndOfList(), nextFreeTimeBlockEnd);
      if (nextFreeTimeBlockStart < startOfDay) {
         nextFreeTimeBlockStart = startOfDay;
      }

      long currentFreeTimeBlockEnd = 0;
      long duration = nextFreeTimeBlockEnd - nextFreeTimeBlockStart;

      while (duration >= threshhold) {
         currentFreeTimeBlockEnd = nextFreeTimeBlockStart + duration;
         if (nextFreeTimeBlockStart < endOfDay) {
            if (currentFreeTimeBlockEnd >= endOfDay) {
               currentFreeTimeBlockEnd = endOfDay;
               calEx.setTimeLong(endOfDay);
               DateTimeUtilities.calendarAddWithDst(super._cal, calEx, 5, 1);
               if (super._cal.get(11) != preferredEndHour) {
                  DateTimeUtilities.calendarSetWithDst(super._cal, calEx, 11, preferredEndHour);
               }

               endOfDay = calEx.getTimeLong();
               calEx.setTimeLong(startOfDay);
               DateTimeUtilities.calendarAddWithDst(super._cal, calEx, 5, 1);
               if (super._cal.get(11) != preferredStartHour) {
                  DateTimeUtilities.calendarSetWithDst(super._cal, calEx, 11, preferredStartHour);
               }

               startOfDay = calEx.getTimeLong();
            }

            super._list.addTransition((byte)3, nextFreeTimeBlockStart, currentFreeTimeBlockEnd, null, null, sequenceNumber);
            duration -= currentFreeTimeBlockEnd - nextFreeTimeBlockStart;
            nextFreeTimeBlockStart = startOfDay;
            endOfLastFreeBlock = currentFreeTimeBlockEnd;
            long advanceAmount = nextFreeTimeBlockStart - endOfLastFreeBlock;
            if (advanceAmount > 0) {
               duration -= advanceAmount;
            }
         } else {
            calEx.setTimeLong(endOfDay);
            DateTimeUtilities.calendarAddWithDst(super._cal, calEx, 5, 1);
            if (super._cal.get(11) != preferredEndHour) {
               DateTimeUtilities.calendarSetWithDst(super._cal, calEx, 11, preferredEndHour);
            }

            endOfDay = calEx.getTimeLong();
            calEx.setTimeLong(startOfDay);
            DateTimeUtilities.calendarAddWithDst(super._cal, calEx, 5, 1);
            if (super._cal.get(11) != preferredStartHour) {
               DateTimeUtilities.calendarSetWithDst(super._cal, calEx, 11, preferredStartHour);
            }

            startOfDay = calEx.getTimeLong();
            duration -= startOfDay - nextFreeTimeBlockStart;
            nextFreeTimeBlockStart = startOfDay;
         }
      }

      return endOfLastFreeBlock;
   }

   @Override
   protected void getIconsForTransition(DayList$Transition curTransition) {
      if (curTransition.conflicts()) {
         int numIcons = super._icons.length;
         Array.resize(super._icons, numIcons + 1);
         Array.resize(super._indices, numIcons + 1);
         super._icons[numIcons] = STATUS_ICONS;
         super._indices[numIcons] = new int[]{0, -805044220, 0, -804651006};
      }
   }

   @Override
   protected boolean supportAdvancedThemeing() {
      return true;
   }

   protected void addDateTimeTransitions(boolean initialSortRequired) {
      Calendar cal = super._cal;
      CalendarExtensions calEx = super._calEx;
      int maxEvents = super._numTransitions;
      int insertionOffset = maxEvents;
      if (maxEvents <= 0) {
         calEx.setTimeLong(System.currentTimeMillis());
         DateTimeUtilities.zeroCalendarTime(cal);
         super._list.setTransition(0, (byte)1, calEx.getTimeLong(), -1);
         super._numTransitions = 1;
      } else {
         if (initialSortRequired) {
            this.sortTransitions();
         }

         int prevYear = 0;
         int prevDay = -1;

         for (int i = 0; i < maxEvents; i++) {
            DayList$Transition transition = super._list.getAt(i);
            calEx.setTimeLong(transition._timeInMillis);
            int currYear = cal.get(1);
            int currDay = cal.get(6);
            if (currYear != prevYear || currDay != prevDay) {
               DateTimeUtilities.zeroCalendarTime(cal);
               DateTimeUtilities.verifyStartOfDay(cal, calEx);
               transition = super._list.getAt(insertionOffset);
               super._list.setTransition(insertionOffset, (byte)1, calEx.getTimeLong(), -1);
               insertionOffset++;
               prevYear = currYear;
               prevDay = currDay;
            }
         }

         super._numTransitions = insertionOffset;
         super._list.clearTransitions(insertionOffset);
         this.sortTransitions();
      }
   }

   @Override
   long getTimeFromOffset(int offset) {
      int size = super._list.getLength();
      if (offset < 0 || offset > size) {
         offset = 0;
      }

      long time = 0;
      if (offset <= size) {
         DayList$Transition transition = super._list.getAt(offset);
         time = transition._timeInMillis;
         if (transition._transitionType == 1) {
            time = this.getLongTimeFromStartFromEventAndTime(time, CalendarOptions.getOptions().getDayStart());
         }
      }

      return time;
   }

   @Override
   long getSelectedEndTime() {
      return this.getSelectedStartTime() + 3600000;
   }

   int getInitialFocusOffset(long targetTime) {
      if (targetTime >= this.getStartOfList() && targetTime < this.getEndOfList()) {
         int max = super._numTransitions;
         int minimumIndex = 0;
         long startOfTargetDay = this.getLongTimeFromStartFromEventAndTime(targetTime, 0);
         boolean isToday = startOfTargetDay == this.getLongTimeFromStartFromEventAndTime(System.currentTimeMillis(), 0);
         boolean targetDayExists = false;

         for (int i = 0; i < max; i++) {
            DayList$Transition curr = super._list.getAt(i);
            if (curr._transitionType == 1 && curr._timeInMillis >= startOfTargetDay) {
               minimumIndex = i;
               targetDayExists = curr._timeInMillis == startOfTargetDay;
               break;
            }
         }

         int offset = -1;

         for (int i = minimumIndex; targetDayExists && i < max; i++) {
            DayList$Transition curr = super._list.getAt(i);
            if (curr._transitionType == 2 && offset == -1) {
               offset = i;
               if (!isToday) {
                  break;
               }
            } else {
               if (i > minimumIndex && curr._transitionType == 1) {
                  break;
               }

               if ((curr._transitionType & 16) != 0) {
                  offset = -1;
                  break;
               }
            }
         }

         if (offset >= 0) {
            return offset;
         }

         for (int i = minimumIndex; i < max; i++) {
            DayList$Transition curr = super._list.getAt(i);
            if (targetTime > curr._timeInMillis && targetTime < curr._endTimeInMillis && curr._transitionType != 3 && curr._transitionType != 2
               || targetTime <= curr._timeInMillis) {
               offset = i;
               break;
            }
         }

         return offset;
      } else {
         return -1;
      }
   }

   protected void setStartOfListBoundary(long loadTime) {
      if (!CalendarOptions.getOptions().useLegacyAgendaView()) {
         long currentTime = System.currentTimeMillis();
         long minimumStartOfList = loadTime - 1209600000;
         long firstEvent = this.getStartOfList();
         if (minimumStartOfList > currentTime) {
            if (this.eventsExistBeforeFirst()) {
               this.setStartOfList(Math.max(firstEvent, minimumStartOfList));
               return;
            }

            this.setStartOfList(minimumStartOfList);
            return;
         }

         if (firstEvent > currentTime && !this.eventsExistBeforeFirst()) {
            this.setStartOfList(this.getLongTimeFromStartFromEventAndTime(currentTime, 0));
         }
      }
   }

   protected void setEndOfListBoundary(long loadTime) {
      if (!CalendarOptions.getOptions().useLegacyAgendaView()) {
         long currentTime = System.currentTimeMillis();
         long lastEvent = this.getEndOfList();
         long maximumEndOfList;
         if (loadTime <= currentTime) {
            maximumEndOfList = currentTime + 1382400000;
         } else {
            maximumEndOfList = loadTime + 1382400000;
         }

         if (this.eventsExistAfterLast() && maximumEndOfList > lastEvent) {
            maximumEndOfList = lastEvent;
         }

         this.setEndOfList(maximumEndOfList);
      }
   }

   @Override
   protected void verifyListBoundaries(long loadTime) {
      this.setStartOfListBoundary(loadTime);
      this.setEndOfListBoundary(loadTime);
   }
}
