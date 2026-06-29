package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.controller.Duration;
import net.rim.device.apps.api.framework.model.DescriptionProvider;
import net.rim.device.apps.api.pim.TimeBasedCollection;
import net.rim.device.cldc.util.CalendarExtensions;

final class MonthTimeInMonth implements FocusChangeListener {
   private TimeBasedCollection _timeBasedCollection;
   private CalendarViewController _callback;
   private long _timeToView;
   private MonthField _month;
   private long _startOfMonthPreservingTOD;
   private long _startOfMonth;
   private long _durOfMonth;
   private long _startOfPreviousMonth;
   private long _durOfPreviousMonth;
   private long _startOfNextMonth;
   private long _durOfNextMonth;
   private int _numDaysInMonth;
   private int _numDaysInPreviousMonth;
   private int _numDaysInNextMonth;
   private int _DOWOfFirstOfMonth;
   private int _monthNumber;
   private byte[] _busyPattern = new byte[31];
   private byte[] _propPattern = new byte[31];
   private byte[] _busyPatternPreviousMonth = new byte[31];
   private byte[] _propPatternPreviousMonth = new byte[31];
   private byte[] _busyPatternNextMonth = new byte[31];
   private byte[] _propPatternNextMonth = new byte[31];
   private Calendar _sharedCal = Calendar.getInstance();
   private Calendar _sharedCalForMonthLoads = Calendar.getInstance();
   private CalendarExtensions _sharedCalEx = (CalendarExtensions)this._sharedCal;
   private CalendarExtensions _sharedCalForMonthLoadsEx = (CalendarExtensions)this._sharedCalForMonthLoads;
   private TimeZone _sharedTZ = this._sharedCal.getTimeZone();
   private static final int MAX_DAYS_IN_MONTH;

   final void init(CalendarViewController callback) {
      this._timeBasedCollection = TimeBasedCollection.getInstance();
      this._callback = callback;
      this._month = new MonthField(callback);
      this._month.setFocusListener(this);
      this._month.setTitleDisplay(true);
   }

   final void uninit() {
      this._month.setFocusListener(null);
   }

   final Runnable loadMonth(long timeToView) {
      Calendar cal = this._sharedCalForMonthLoads;
      CalendarExtensions calEx = this._sharedCalForMonthLoadsEx;
      calEx.setTimeLong(timeToView);
      DateTimeUtilities.zeroCalendarTime(cal);
      cal.set(11, (int)((long)CalendarOptions.getOptions().getDayStart() / 3600000));
      timeToView = calEx.getTimeLong();
      this._timeToView = timeToView;
      this.reloadMonthData(timeToView);
      return new MonthTimeInMonth$DoUIRunnable(this, timeToView);
   }

   final long getSelectedStartTime() {
      return this._timeToView;
   }

   final long getSelectedEndTime() {
      return this._timeToView + 3600000;
   }

   final Field getField() {
      return this._month;
   }

   final void setStartingDOW(int startingDOW) {
      this._month.setStartingDOW(startingDOW);
   }

   public final int moveFocus(MonthField monthField, int amount) {
      if (monthField != this._month) {
         return -1;
      }

      CalendarExtensions calEx = this._sharedCalEx;
      calEx.setTimeLong(this._startOfMonthPreservingTOD);
      calEx.add(5, this._month.getSelectedDay());
      calEx.add(5, amount);
      this._timeToView = calEx.getTimeLong();
      return 0;
   }

   @Override
   public final void focusChanged(Field field, int eventType) {
      if (eventType == 2) {
         CalendarExtensions calEx = this._sharedCalEx;
         calEx.setTimeLong(this._startOfMonthPreservingTOD);
         calEx.add(5, this._month.getSelectedDay());
         this._timeToView = calEx.getTimeLong();
         this._callback.selectedDateChanged(this._timeToView);
      }
   }

   private final void reloadMonthData(long time) {
      Vector events = (Vector)(new Object());
      events.removeAllElements();
      CalendarExtensions calEx = this._sharedCalForMonthLoadsEx;
      Calendar cal = this._sharedCalForMonthLoads;
      calEx.setTimeLong(time);
      this._monthNumber = cal.get(2);
      cal.set(5, 1);
      this._startOfMonthPreservingTOD = calEx.getTimeLong();
      DateTimeUtilities.zeroCalendarTime(cal);
      this._startOfMonth = calEx.getTimeLong();
      this._DOWOfFirstOfMonth = cal.get(7) - 1;
      calEx.add(2, 1);
      this._durOfMonth = calEx.getTimeLong() - this._startOfMonth;
      calEx.add(14, -1);
      this._numDaysInMonth = cal.get(5);
      Arrays.fill(this._propPattern, (byte)0);
      Arrays.fill(this._busyPattern, (byte)0);
      this._timeBasedCollection.getElementsVisibleDuring(this._startOfMonth, this._durOfMonth, this._sharedTZ, events);
      this.addEventInfo(events, this._startOfMonth, this._durOfMonth, this._propPattern, this._busyPattern, this._numDaysInMonth);
      events.removeAllElements();
      cal.set(5, 1);
      DateTimeUtilities.zeroCalendarTime(cal);
      calEx.add(14, -1);
      this._numDaysInPreviousMonth = cal.get(5);
      cal.set(5, 1);
      DateTimeUtilities.zeroCalendarTime(cal);
      this._startOfPreviousMonth = calEx.getTimeLong();
      calEx.add(2, 1);
      this._durOfPreviousMonth = calEx.getTimeLong() - this._startOfPreviousMonth;
      calEx.setTimeLong(time);
      Arrays.fill(this._propPatternPreviousMonth, (byte)0);
      Arrays.fill(this._busyPatternPreviousMonth, (byte)0);
      this._timeBasedCollection.getElementsVisibleDuring(this._startOfPreviousMonth, this._durOfPreviousMonth, this._sharedTZ, events);
      this.addEventInfo(
         events,
         this._startOfPreviousMonth,
         this._durOfPreviousMonth,
         this._propPatternPreviousMonth,
         this._busyPatternPreviousMonth,
         this._numDaysInPreviousMonth
      );
      events.removeAllElements();
      cal.set(5, 1);
      DateTimeUtilities.zeroCalendarTime(cal);
      calEx.add(2, 1);
      this._startOfNextMonth = calEx.getTimeLong();
      calEx.add(2, 1);
      this._durOfNextMonth = calEx.getTimeLong() - this._startOfNextMonth;
      calEx.add(14, -1);
      this._numDaysInNextMonth = cal.get(5);
      calEx.setTimeLong(time);
      Arrays.fill(this._propPatternNextMonth, (byte)0);
      Arrays.fill(this._busyPatternNextMonth, (byte)0);
      this._timeBasedCollection.getElementsVisibleDuring(this._startOfNextMonth, this._durOfNextMonth, this._sharedTZ, events);
      this.addEventInfo(events, this._startOfNextMonth, this._durOfNextMonth, this._propPatternNextMonth, this._busyPatternNextMonth, this._numDaysInNextMonth);
      events.removeAllElements();
   }

   private final void addEventInfo(Vector events, long startOfMonth, long durOfMonth, byte[] propPattern, byte[] busyPattern, int numDaysInMonth) {
      int max = events.size();

      for (int i = 0; i < max; i++) {
         Object calObject = events.elementAt(i);
         this.updateMonthInfo(calObject, startOfMonth, durOfMonth, propPattern, busyPattern, numDaysInMonth);
      }
   }

   private final void updateMonthInfo(Object event, long startOfMonth, long durOfMonth, byte[] propPattern, byte[] busyPattern, int numDaysInMonth) {
      CalendarExtensions calEx = this._sharedCalEx;
      Calendar cal = this._sharedCal;
      Duration dur = (Duration)event;
      DescriptionProvider desc = null;
      if (event instanceof Object) {
         desc = (DescriptionProvider)event;
      }

      long start = dur.getStart(this._sharedTZ);
      long duration = dur.getDuration(this._sharedTZ);
      if (start < startOfMonth) {
         duration -= startOfMonth - start;
         start = startOfMonth;
      }

      if (start + duration > startOfMonth + durOfMonth) {
         duration = startOfMonth + durOfMonth - start;
      }

      if (desc != null && (desc.getProperties() & 1) != 0) {
         calEx.setTimeLong(start);
         int startDay = cal.get(5) - 1;
         int startMonth = cal.get(2);
         calEx.setTimeLong(start + duration);
         int endDay = cal.get(5) - 1;
         int endMonth = cal.get(2);
         if (endDay < startDay || endMonth != startMonth) {
            endDay = numDaysInMonth;
         }

         for (int i = startDay; i < endDay; i++) {
            propPattern[i] = (byte)(propPattern[i] | 1);
         }
      } else {
         int numHours = (int)((duration + 3600000 - 1) / 3600000);
         if (numHours <= 0) {
            numHours = 1;
         }

         calEx.setTimeLong(start);
         int day = cal.get(5) - 1;
         int hour = cal.get(11);

         for (int j = 0; j < numHours && day >= 0; j++) {
            if (day >= numDaysInMonth) {
               return;
            }

            int numShifts;
            if (hour < 8) {
               numShifts = 0;
            } else if (hour >= 20) {
               numShifts = 7;
            } else {
               numShifts = hour - 6 >> 1;
            }

            busyPattern[day] = (byte)(busyPattern[day] | 128 >> numShifts);
            if (++hour >= 24) {
               day++;
               hour = 0;
            }
         }
      }
   }

   private final void setupMonthField(long time) {
      MonthField month = this._month;
      Calendar cal = this._sharedCal;
      CalendarExtensions calEx = this._sharedCalEx;
      long currTime = System.currentTimeMillis();
      calEx.setTimeLong(time);
      this._monthNumber = cal.get(2);
      cal.set(5, 1);
      this._startOfMonthPreservingTOD = calEx.getTimeLong();
      DateTimeUtilities.zeroCalendarTime(cal);
      long startOfMonth = calEx.getTimeLong();
      this._DOWOfFirstOfMonth = cal.get(7) - 1;
      calEx.add(2, 1);
      long durOfMonth = calEx.getTimeLong() - startOfMonth;
      calEx.add(14, -1);
      this._numDaysInMonth = cal.get(5);
      month.setMonthInfo(this._numDaysInMonth, this._busyPattern, this._propPattern);
      month.setDOWOfFirstDayOfMonth(this._DOWOfFirstOfMonth);
      month.setStartingDOW(CalendarOptions.getOptions().getFirstDayOfWeek() - 1);
      month.setMonth(this._monthNumber);
      calEx.setTimeLong(time);
      month.setSelectedDay(cal.get(5) - 1);
      if (currTime >= startOfMonth && currTime < startOfMonth + durOfMonth) {
         calEx.setTimeLong(currTime);
         month.setCurrentDayInMonth(cal.get(5) - 1);
      } else {
         month.setCurrentDayInMonth(-1);
      }

      this._timeToView = time;
      this._startOfMonth = startOfMonth;
      this._durOfMonth = durOfMonth;
      cal.set(5, 1);
      DateTimeUtilities.zeroCalendarTime(cal);
      calEx.add(14, -1);
      this._numDaysInPreviousMonth = cal.get(5);
      month.setPreviousMonthInfo(this._numDaysInPreviousMonth, this._busyPatternPreviousMonth, this._propPatternPreviousMonth);
      calEx.setTimeLong(time);
      cal.set(5, 1);
      calEx.add(2, 2);
      DateTimeUtilities.zeroCalendarTime(cal);
      calEx.add(14, -1);
      this._numDaysInNextMonth = cal.get(5);
      month.setNextMonthInfo(this._numDaysInNextMonth, this._busyPatternNextMonth, this._propPatternNextMonth);
   }
}
