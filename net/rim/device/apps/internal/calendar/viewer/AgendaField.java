package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import java.util.Vector;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.controller.Duration;
import net.rim.device.apps.api.pim.TimeBasedCollection;
import net.rim.device.cldc.util.CalendarExtensions;

final class AgendaField extends AgendaFieldBase {
   private CalendarViewController _callback;
   private static final int DEFAULT_NUM_ITEMS_BEFORE;
   private static final int DEFAULT_NUM_ITEMS_ON_OR_AFTER;
   private static final int NUM_OFFSET_BUFFER;
   private static final byte SELECT_LOAD_TIME;
   private static final byte SELECT_NEXT_DAY;
   private static final byte SELECT_PREV_DAY;

   final void init(CalendarViewController callback) {
      this._callback = callback;
   }

   final void uninit() {
      this._callback = null;
   }

   final Runnable loadAgenda(long desiredTime, Object object, byte loadType) {
      CalendarExtensions calEx = super._calEx;
      calEx.setTimeLong(desiredTime);
      long loadDayStart = calEx.getTimeLong();
      Vector events = this.loadAgendaNonUIWork(loadDayStart);
      return new AgendaField$DoUIRunnable(this, desiredTime, object, events, loadType);
   }

   private final void loadAgendaUI(long loadTime, byte loadType, Object objectToSelect, Vector events) {
      this.updateTransitions(events, loadTime);
      Calendar cal = super._cal;
      CalendarExtensions calEx = super._calEx;
      calEx.setTimeLong(loadTime);
      DateTimeUtilities.zeroCalendarTime(cal);
      long loadDayStart = calEx.getTimeLong();
      calEx.add(5, 1);
      long nextDayStart = calEx.getTimeLong();
      this.getField().setSize(super._numTransitions, -1);
      int offsetToSelect = this.getInitialFocusOffset(loadTime);
      if (offsetToSelect < 0) {
         if (loadTime >= this.getEndOfList()) {
            offsetToSelect = this.getNumberOfItems() - 1;
         } else {
            offsetToSelect = 0;
         }
      }

      if (offsetToSelect > 0
         && super._list.getAt(offsetToSelect)._timeInMillis >= nextDayStart
         && super._list.getAt(offsetToSelect - 1)._timeInMillis >= loadDayStart) {
         offsetToSelect--;
      }

      if (loadType == 3) {
         int separatorOffset = offsetToSelect;
         DayList$Transition curTransition = super._list.getAt(separatorOffset);
         if (curTransition._transitionType != 1 || curTransition._transitionType == 1 && curTransition._timeInMillis != loadTime) {
            separatorOffset = this.findSeparatorOffsetInNextDay(offsetToSelect - 1);
         }

         if (separatorOffset >= 0) {
            offsetToSelect = separatorOffset;
         } else {
            offsetToSelect = this.getNumberOfItems() - 1;
         }
      } else if (loadType == 4) {
         int var15 = offsetToSelect;
         DayList$Transition curTransition = super._list.getAt(var15);
         if (curTransition._transitionType != 1 || curTransition._transitionType == 1 && curTransition._timeInMillis != loadTime) {
            var15 = this.findSeparatorOffsetInPrevDay(offsetToSelect + 1);
         }

         if (var15 >= 0) {
            offsetToSelect = var15;
         } else {
            offsetToSelect = 0;
         }
      }

      if (offsetToSelect >= 0) {
         if (super._list.getAt(offsetToSelect)._transitionType == 1 && loadType == 1) {
            offsetToSelect++;
         } else {
            for (int i = offsetToSelect; i >= 0; i--) {
               if (super._list.getAt(i)._transitionType == 1) {
                  break;
               }
            }
         }
      } else {
         offsetToSelect = 0;
         int var16 = false;
      }

      if (offsetToSelect >= this.getNumberOfItems()) {
         offsetToSelect = this.getNumberOfItems() - 1;
      }

      this.setSelectedOffset(offsetToSelect);
      this.makeOffsetVisible(this.getNumberOfItems() - 1);
      this.makeOffsetVisible(offsetToSelect > 0 ? offsetToSelect - 1 : 0);
      if (loadType == 1 || loadType == 0) {
         this.setSelectedObject(objectToSelect, loadTime);
      }

      if (this._callback != null) {
         this._callback.selectedDateChanged(this.getTimeFromOffset(this.getSelectedOffset()));
      }
   }

   private final Vector loadAgendaNonUIWork(long loadTime) {
      int numBefore = 16;
      int numOnOrAfter = 16;
      Vector events = (Vector)(new Object());
      TimeBasedCollection tbc = TimeBasedCollection.getInstance();
      long[] result = tbc.getElementsStartingAround(loadTime - 1, numBefore, numOnOrAfter, super._tz, events);
      super._moreEvents = (int)result[2];
      this.setStartOfList(Long.MIN_VALUE);
      this.setEndOfList(Long.MIN_VALUE);
      int numEvents = events.size();
      if (numEvents > 0) {
         this.setStartOfList(result[3]);
         this.setEndOfList(result[4]);

         for (int i = numEvents - 1; i >= 0; i--) {
            Duration dur = (Duration)events.elementAt(i);
            long endTime = dur.getStart(super._tz) + dur.getDuration(super._tz);
            if (endTime > this.getEndOfList() && (result[2] & 1) == 0) {
               this.setEndOfList(endTime);
            }
         }
      }

      return events;
   }

   private final int findSeparatorOffsetInNextDay(int startingOffset) {
      int nextDaySeparatorOffset = -1;

      for (int i = startingOffset + 1; i < super._numTransitions; i++) {
         if (super._list.getAt(i)._transitionType == 1) {
            return i;
         }
      }

      return nextDaySeparatorOffset;
   }

   private final int findSeparatorOffsetInPrevDay(int startingOffset) {
      for (int i = startingOffset; i >= 0; i--) {
         if (super._list.getAt(i)._transitionType == 1) {
            startingOffset = i;
            break;
         }
      }

      int prevDaySeparatorOffset = -1;

      for (int i = startingOffset - 1; i >= 0; i--) {
         if (super._list.getAt(i)._transitionType == 1) {
            return i;
         }
      }

      return prevDaySeparatorOffset;
   }

   public final void gotoAdjacentDay(boolean nextDay) {
      int currentOffset = this.getSelectedOffset();
      int adjacentDayOffset = nextDay ? this.findSeparatorOffsetInNextDay(currentOffset) : this.findSeparatorOffsetInPrevDay(currentOffset);
      int futureDayOffset = nextDay ? this.findSeparatorOffsetInNextDay(adjacentDayOffset + 14) : this.findSeparatorOffsetInPrevDay(adjacentDayOffset - 1);
      if (adjacentDayOffset != -1 && futureDayOffset != -1 && futureDayOffset != adjacentDayOffset) {
         this.makeOffsetVisible(adjacentDayOffset, true);
         super._field.setSelectedIndex(adjacentDayOffset);
         this._callback.updateSelectedDate(this.getTimeFromOffset(adjacentDayOffset));
      } else {
         Calendar cal = super._cal;
         CalendarExtensions calEx = super._calEx;
         calEx.setTimeLong(super._list.getAt(currentOffset)._timeInMillis);
         DateTimeUtilities.zeroCalendarTime(cal);
         byte loadType = 4;
         if (nextDay) {
            loadType = 3;
            calEx.add(5, 1);
         } else {
            calEx.add(5, -1);
         }

         long nextDayStart = calEx.getTimeLong();
         this._callback.loadViewContents(nextDayStart, (byte)0, null, (byte)1, true, true, loadType);
      }
   }

   @Override
   protected final void addDateTimeTransitions() {
      this.addDateTimeTransitions(true);
   }

   @Override
   public final void focusMoved(Object field, int amount, int status, int time, int currentlySelectedIndex) {
      super.focusMoved(field, amount, status, time, currentlySelectedIndex);
      int lastIndex = this.getNumberOfItems() - 1;
      if (currentlySelectedIndex != 0 && currentlySelectedIndex != lastIndex) {
         this._callback.selectedDateChanged(this.getTimeFromOffset(currentlySelectedIndex));
      } else {
         long selectedTime = this.getSelectedStartTime();
         boolean loadView = false;
         if (currentlySelectedIndex == 0 && lastIndex > 0) {
         }

         if (currentlySelectedIndex == 0) {
            if (this.eventsExistBeforeFirst() || !CalendarOptions.getOptions().useLegacyAgendaView() && this.getStartOfList() > System.currentTimeMillis()) {
               loadView = true;
            }
         } else if (this.eventsExistAfterLast() || !CalendarOptions.getOptions().useLegacyAgendaView()) {
            loadView = true;
         }

         if (loadView) {
            this._callback.loadViewContents(selectedTime, (byte)1, null, (byte)1, true, true, (byte)0);
            return;
         }
      }
   }

   @Override
   public final AccessibleContext getAccessibleChildAt(int index) {
      return super.getAccessibleChildAt(index);
   }
}
