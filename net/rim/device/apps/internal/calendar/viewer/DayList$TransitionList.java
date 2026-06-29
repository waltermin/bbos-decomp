package net.rim.device.apps.internal.calendar.viewer;

import java.util.TimeZone;
import java.util.Vector;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.calendar.controller.Duration;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;

final class DayList$TransitionList {
   private DayList$Transition[] _listData;
   private int[] _conflictArray;
   Vector _events;
   private int[] _ignoreArray;
   int _currOffset;
   private final DayList this$0;

   DayList$TransitionList(DayList _1) {
      this.this$0 = _1;
      this._listData = DayList.makeRoomForTransitions(10, null);
      this._conflictArray = new int[0];
      this._ignoreArray = new int[0];
   }

   final void reset(Vector events) {
      this._currOffset = 0;
      this._events = events;
      this._conflictArray = new int[0];
      this._ignoreArray = new int[0];
   }

   private final void grow(int size) {
      this._listData = DayList.makeRoomForTransitions(size, this._listData);
   }

   final DayList$Transition getAt(int index) {
      if (index >= this._listData.length) {
         this.grow(Math.max(this._listData.length * 2, index + 1));
      }

      if (index >= 0 && index < this._listData.length) {
         return this._listData[index];
      } else {
         throw new Object("Watch out for this!");
      }
   }

   final Duration getEventAt(int index) {
      return (Duration)(index >= 0 && index < this._events.size() ? this._events.elementAt(index) : null);
   }

   final boolean canSkipFreeTimeProcessing(Duration event) {
      if (event == null) {
         return true;
      } else {
         return event.isAllDay() ? !(event instanceof Object) || ((Event)event).getFreeBusy() != 2 : false;
      }
   }

   final Duration getNextEvent(int index, boolean markContainedAppointmentsAsConflicting) {
      boolean conflictAddedForCurrentIndex = false;
      if (index >= -1) {
         for (int nextElementIndex = index + 1; nextElementIndex < this._events.size(); nextElementIndex++) {
            Duration nextEvent = this.getEventAt(nextElementIndex);
            if (!this.canSkipFreeTimeProcessing(nextEvent) && !this.shouldIgnore(nextElementIndex)) {
               if (!this.isContainedIn(index, nextElementIndex)) {
                  return nextEvent;
               }

               if (markContainedAppointmentsAsConflicting) {
                  if (!conflictAddedForCurrentIndex) {
                     if (!this.isConflicting(index)) {
                        this.addConflict(index);
                     }

                     conflictAddedForCurrentIndex = true;
                  }

                  this.addConflict(nextElementIndex);
               }
            }
         }
      }

      return null;
   }

   final Duration getPreviousEvent(int index) {
      if (index <= this._events.size()) {
         for (int prevElementIndex = index - 1; prevElementIndex >= 0; prevElementIndex--) {
            Duration prevEvent = this.getEventAt(prevElementIndex);
            if (!this.canSkipFreeTimeProcessing(prevEvent) && !this.shouldIgnore(prevElementIndex) && !this.isContainedIn(index, prevElementIndex)) {
               return prevEvent;
            }
         }
      }

      return null;
   }

   final boolean isContainedIn(int index1, int index2) {
      TimeZone tz = TimeZone.getDefault();
      Duration event1 = this.getEventAt(index1);
      Duration event2 = this.getEventAt(index2);
      long start1 = event1.getStart(tz);
      long end1 = start1 + event1.getDuration(tz);
      long start2 = event2.getStart(tz);
      long end2 = start2 + event2.getDuration(tz);
      if (start2 >= start1 && end2 <= end1) {
         Arrays.add(this._ignoreArray, index2);
         return true;
      } else {
         return false;
      }
   }

   final boolean shouldIgnore(int index) {
      for (int i = 0; i < this._ignoreArray.length; i++) {
         if (this._ignoreArray[i] == index) {
            return true;
         }
      }

      return false;
   }

   final void addConflict(int conflictIndex) {
      Arrays.add(this._conflictArray, conflictIndex);
   }

   protected final void clearTransitions(int startAt) {
      int max = this._listData.length;

      for (int i = startAt; i < max; i++) {
         DayList$Transition t = this._listData[i];
         t._displayTime = null;
         t.setSummaryText(null);
         t._calElement = null;
         t._barString = null;
         t._barColours = null;
         t._extraLinesBarString = null;
         t._extraLinesBarColours = null;
         t._subsequentDayOfSpanningEvent = false;
         t._attributes = 0;
         t._timeInMillis = 0;
         t._endTimeInMillis = 0;
      }
   }

   final int getLength() {
      return this._listData.length;
   }

   final int getCurrentOffset() {
      return this._currOffset;
   }

   final void sort() {
      Arrays.sort(this._listData, 0, this.this$0._numTransitions, this.this$0._tsc);
   }

   private final boolean isConflicting(int currentIndex) {
      for (int i = 0; i < this._conflictArray.length; i++) {
         if (this._conflictArray[i] == currentIndex) {
            return true;
         }
      }

      return false;
   }

   final boolean setTransition(int index, byte type, long time, int sequenceNumber) {
      return this.setTransition(index, type, time, 0, null, null, sequenceNumber);
   }

   final boolean setTransition(int index, byte transitionType, long timeStart, long timeEnd, Duration element, String description, int sequenceNumber) {
      if (index >= this._listData.length) {
         this.grow(Math.max(this._listData.length * 2, index + 1));
      }

      int indexOfDurationObject = this._events.indexOf(element);
      DayList$Transition transition = this._listData[index];
      transition.setTransitionData(transitionType, timeStart, sequenceNumber, timeEnd, element, description);
      if (transition._endTimeInMillis == 0 && transition._timeInMillis < System.currentTimeMillis()
         || transition._endTimeInMillis != 0 && transition._endTimeInMillis < System.currentTimeMillis()) {
         transition.setAttribute((byte)1, true);
      }

      if (this.isConflicting(indexOfDurationObject)) {
         transition.setAttribute((byte)2, true);
      }

      return true;
   }

   final boolean addTransition(byte transitionType, long timeStart, long timeEnd, Duration element, String description, int sequenceNumber) {
      if (this._currOffset >= this._listData.length) {
         this.grow(Math.max(this._listData.length * 2, this._currOffset + 1));
      }

      this.setTransition(this._currOffset, transitionType, timeStart, timeEnd, element, description, sequenceNumber);
      this._currOffset++;
      return true;
   }
}
