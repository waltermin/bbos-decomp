package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.calendar.controller.Duration;
import net.rim.device.apps.api.framework.model.DescriptionProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.UniqueIDProvider;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.vm.Array;

final class WeekField$WeekFieldDayContents {
   private boolean _dayPopulated;
   private boolean _dayLaidOut;
   private long _startOfFocus;
   private long _endOfFocus;
   private int _startDstOffset;
   private int _endDstOffset;
   private long _focusAnchor;
   private long _preferredSOD;
   private long _preferredEOD;
   private long _sod;
   private long _eod;
   private WeekField$EventEntry[] _events;
   private int _numEvents;
   private int _currentEventEntryIndex;
   private int _emphasizedEventEntryIndex;
   private boolean _appointmentsConflict;
   private final WeekField this$0;
   private static final int FOCUS_DURATION;
   private static final int GROW_SIZE;

   WeekField$WeekFieldDayContents(WeekField _1) {
      this.this$0 = _1;
      this._sod = -1;
      this._eod = -1;
      this._events = new WeekField$EventEntry[0];
      this._currentEventEntryIndex = -1;
      this._emphasizedEventEntryIndex = -1;
   }

   public final long getStartOfFocusAnchor() {
      return this._focusAnchor;
   }

   public final long getEndOfFocusAnchor() {
      return this._focusAnchor + 1800000;
   }

   public final long getStartOfFocus() {
      return this._startOfFocus;
   }

   public final long getEndOfFocus() {
      return this._endOfFocus;
   }

   public final long getSOD() {
      return this._sod;
   }

   public final void setSOD(long sod) {
      this._sod = sod;
   }

   public final long getEOD() {
      return this._eod;
   }

   public final void setEOD(long eod) {
      this._eod = eod;
   }

   public final void setPreferredSODAndEOD(long preferredSOD, long preferredEOD) {
      this._preferredSOD = preferredSOD;
      this._preferredEOD = preferredEOD;
   }

   public final void setDayPopulated(boolean dayPopulated) {
      this._dayPopulated = dayPopulated;
      if (!dayPopulated) {
         for (int i = 0; i < this._events.length; i++) {
            this._events[i]._object = null;
         }

         this._numEvents = 0;
         this._dayLaidOut = false;
      }
   }

   public final boolean isDayPopulated() {
      return this._dayPopulated;
   }

   public final boolean isDayLaidOut() {
      return this._dayLaidOut;
   }

   public final WeekField$EventEntry[] getEventEntries() {
      return this._events;
   }

   public final int getNumEventEntries() {
      return this._numEvents;
   }

   public final boolean isDayConflictFree() {
      return this._appointmentsConflict;
   }

   public final boolean isDayEmphasized() {
      return this._emphasizedEventEntryIndex >= 0;
   }

   public final void populateDay() {
      this.this$0._eventVector.setSize(0);
      if (this.this$0._timeBasedCollection != null) {
         this.this$0._timeBasedCollection.getElementsVisibleDuring(this._sod, this._eod - this._sod, this.this$0._tz, this.this$0._eventVector);
      }

      int numEvents = this.this$0._eventVector.size();
      this.makeRoomForEvents(numEvents);

      for (int i = 0; i < numEvents; i++) {
         RIMModel calElement = (RIMModel)this.this$0._eventVector.elementAt(i);
         Duration currDur = (Duration)calElement;
         long eventStart = currDur.getStart(this.this$0._tz);
         long eventEnd = eventStart + currDur.getDuration(this.this$0._tz);
         this.addEvent(i, calElement, eventStart, eventEnd);
      }

      this._numEvents = numEvents;
      this._dayPopulated = true;
   }

   public final void addEvent(RIMModel calElement, long eventStart, long eventEnd) {
      this.makeRoomForEvents(++this._numEvents);
      this.addEvent(this._numEvents - 1, calElement, eventStart, eventEnd);
   }

   private final void addEvent(int eventEntryIndex, RIMModel calElement, long eventStart, long eventEnd) {
      WeekField$EventEntry ee = this._events[eventEntryIndex];
      ee._start = eventStart;
      ee._displayStart = eventStart;
      ee._end = eventEnd;
      ee._displayEnd = eventEnd;
      ee._object = calElement;
      ee._properties = 0;
      DescriptionProvider descriptionProvider = (DescriptionProvider)calElement;
      if ((descriptionProvider.getProperties() & 1) != 0) {
         ee._properties = 3;
      }

      this._dayLaidOut = false;
   }

   private final void makeRoomForEvents(int numEvents) {
      int oldSize = -1;
      WeekField$EventEntry[] events = this._events;
      if (events == null) {
         oldSize = 0;
         events = new WeekField$EventEntry[numEvents];
      } else if (numEvents > events.length) {
         numEvents += 5;
         oldSize = events.length;
         Array.resize(events, numEvents);
      }

      if (oldSize >= 0) {
         for (int i = oldSize; i < numEvents; i++) {
            events[i] = new WeekField$EventEntry();
         }
      }
   }

   public final void layoutDay() {
      this._emphasizedEventEntryIndex = -1;
      this._appointmentsConflict = false;
      long[] slotInUse = WeekField._slotInUseUntil;
      int slotInUseLength = slotInUse.length;
      Arrays.fill(slotInUse, Long.MIN_VALUE);
      Arrays.sort(this._events, 0, this._numEvents, WeekField._eeComparator);
      WeekField$EventEntry ee = null;

      for (int i = 0; i < this._numEvents; i++) {
         ee = this._events[i];
         ee._displayStart = ee._start;
         ee._displayEnd = ee._end;
         if (ee._displayStart < this._preferredSOD) {
            ee._displayStart = this._preferredSOD;
         }

         if (ee._displayEnd < this._preferredSOD + 1800000) {
            ee._displayEnd = this._preferredSOD + 1800000;
         }

         if (ee._displayStart > this._preferredEOD - 1800000) {
            ee._displayStart = this._preferredEOD - 1800000;
         }

         if (ee._displayEnd > this._preferredEOD) {
            ee._displayEnd = this._preferredEOD;
         }

         long eventStart = ee._displayStart;
         long eventEnd = ee._displayEnd;
         if ((this._events[i]._properties & 1) != 0) {
            this._emphasizedEventEntryIndex = i;
            ee._slot = -1;
         } else {
            int j = 0;

            while (true) {
               if (j >= slotInUseLength) {
                  Array.resize(slotInUse, slotInUseLength + 1);
                  slotInUse[slotInUseLength++] = Long.MIN_VALUE;
               }

               if (eventStart >= slotInUse[j]) {
                  if (j > 0) {
                     this._appointmentsConflict = true;
                  }

                  ee._slot = j;
                  slotInUse[j] = eventEnd;
                  break;
               }

               j++;
            }
         }
      }

      this._dayLaidOut = true;
   }

   public final void setFocus(long desiredTime) {
      long startOfFocus = desiredTime - this._preferredSOD;
      startOfFocus /= 1800000;
      startOfFocus *= 1800000;
      startOfFocus += this._preferredSOD;
      if (startOfFocus < this._preferredSOD) {
         startOfFocus = this._preferredSOD;
      }

      if (startOfFocus + 1800000 > this._preferredEOD) {
         startOfFocus = this._preferredEOD - 1800000;
      }

      this._startOfFocus = startOfFocus;
      this._endOfFocus = startOfFocus + 1800000;
      this._focusAnchor = startOfFocus;
      this._currentEventEntryIndex = -1;
      this.changeFocusEntry(1);
   }

   public final int moveFocus(int amount, boolean selecting, boolean ignoreEvents) {
      int amountExtra = 0;
      if (!ignoreEvents) {
         this.changeFocusEntry(amount);
      }

      if (this._currentEventEntryIndex == -1 || ignoreEvents) {
         this._focusAnchor += amount * 1800000;
         long startOfFocusAnchor = this._focusAnchor;
         long endOfFocusAnchor = startOfFocusAnchor + 1800000;
         if (startOfFocusAnchor >= this._preferredSOD && endOfFocusAnchor <= this._preferredEOD) {
            if (selecting) {
               if (startOfFocusAnchor < this._startOfFocus) {
                  this._startOfFocus = startOfFocusAnchor;
               } else if (endOfFocusAnchor > this._endOfFocus) {
                  this._endOfFocus = endOfFocusAnchor;
               } else if (amount > 0) {
                  this._startOfFocus = startOfFocusAnchor;
               } else {
                  this._endOfFocus = endOfFocusAnchor;
               }
            } else {
               this._startOfFocus = startOfFocusAnchor;
               this._endOfFocus = endOfFocusAnchor;
            }

            this.changeFocusEntry(amount);
         } else if (!selecting) {
            amountExtra = amount;
         } else if (endOfFocusAnchor > this._preferredEOD) {
            this._focusAnchor = this._preferredEOD - 1800000;
            this.changeFocusEntry(amount * -1);
         } else if (startOfFocusAnchor < this._preferredSOD) {
            this._focusAnchor = this._preferredSOD;
            this.changeFocusEntry(amount * -1);
         }
      } else if (!selecting) {
         this._startOfFocus = this._focusAnchor;
         this._endOfFocus = this._startOfFocus + 1800000;
      }

      return amountExtra;
   }

   private final void changeFocusEntry(int direction) {
      boolean foundEntry = false;
      int numEvents = this._numEvents;
      int i = this._currentEventEntryIndex;
      int increment;
      if (direction > 0) {
         if (i < 0) {
            i = -1;
         }

         increment = 1;
      } else {
         if (i < 0) {
            i = numEvents;
         }

         increment = -1;
      }

      for (int var10 = i + increment; var10 >= 0 && var10 < numEvents; var10 += increment) {
         long start = this._events[var10]._displayStart;
         long end = this._events[var10]._displayEnd;
         if (start == end) {
            end += 1;
         }

         if (start < this._focusAnchor + 1800000 && end > this._focusAnchor && this._events[var10]._slot < 3 && this._events[var10]._slot >= 0) {
            foundEntry = true;
            this._currentEventEntryIndex = var10;
            break;
         }
      }

      if (!foundEntry) {
         this._currentEventEntryIndex = -1;
      }
   }

   public final WeekField$EventEntry getEntryWithFocus() {
      if (this._currentEventEntryIndex >= 0 && this._currentEventEntryIndex < this._numEvents) {
         return this._events[this._currentEventEntryIndex];
      } else {
         return this._emphasizedEventEntryIndex >= 0 && this._emphasizedEventEntryIndex < this._numEvents
            ? this._events[this._emphasizedEventEntryIndex]
            : null;
      }
   }

   public final boolean setEntryWithFocus(Object object) {
      if (object == null) {
         return false;
      }

      long uid = 0;
      if (object instanceof Object) {
         UniqueIDProvider uniqueIDProvider = (UniqueIDProvider)object;
         uid = uniqueIDProvider.getLUID(null);
      }

      boolean success = false;
      boolean found = false;

      for (int i = 0; i < this._numEvents; i++) {
         Object currentObject = this._events[i]._object;
         if (currentObject == object) {
            found = true;
         } else if (uid != 0 && currentObject instanceof Object && ((UniqueIDProvider)currentObject).getLUID(null) == uid) {
            found = true;
         }

         if (found) {
            if (this._events[i]._slot < 3) {
               this._currentEventEntryIndex = i;
               success = true;
            }
            break;
         }
      }

      return success;
   }

   public final void setStartDstOffset(int val) {
      this._startDstOffset = val;
   }

   public final void setEndDstOffset(int val) {
      this._endDstOffset = val;
   }

   public final void resetDstOffsets() {
      this._startDstOffset = 0;
      this._endDstOffset = 0;
   }

   public final long getDstAdjustment(long time) {
      long adjustment = 0;
      if (this._startDstOffset != this._endDstOffset) {
         Calendar cal = this.this$0._cal;
         CalendarExtensions calEx = (CalendarExtensions)cal;
         calEx.setTimeLong(time);
         if (cal.get(16) != this._endDstOffset) {
            adjustment = this._startDstOffset - this._endDstOffset;
         }
      }

      return adjustment;
   }
}
