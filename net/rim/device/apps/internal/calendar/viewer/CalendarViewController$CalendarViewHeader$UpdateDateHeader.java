package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;

final class CalendarViewController$CalendarViewHeader$UpdateDateHeader implements Runnable {
   Object _lock;
   private long _selectedDateValue;
   private long _lastScrollEvent;
   private boolean _isPending;
   private final CalendarViewController$CalendarViewHeader this$1;

   CalendarViewController$CalendarViewHeader$UpdateDateHeader(CalendarViewController$CalendarViewHeader _1, Object lock) {
      this.this$1 = _1;
      this._lock = null;
      this._selectedDateValue = -1;
      this._lastScrollEvent = -1;
      this._lock = lock;
   }

   public final void setSelectedDate(long selectedDate) {
      this._selectedDateValue = selectedDate;
      this._lastScrollEvent = System.currentTimeMillis();
   }

   public final boolean getPending() {
      return this._isPending;
   }

   public final void setPending(boolean pending) {
      this._isPending = pending;
   }

   @Override
   public final void run() {
      synchronized (this._lock) {
         Application app = this.this$1.this$0.getApplication();
         if (app != null) {
            long timeSinceLastScroll = System.currentTimeMillis() - this._lastScrollEvent;
            if (!this.this$1._delayedRenderingEnabled
               || !this._isPending
               || timeSinceLastScroll >= 100
               || app.invokeLater(this.this$1._updateDateRunnable, 100 - timeSinceLastScroll, false) == -1) {
               this._isPending = false;
               this.this$1._selectedDate.setDate(this._selectedDateValue);
               if (this.this$1._currentWeekNumber.getManager() != null) {
                  int fdow = CalendarOptions.getOptions().getFirstDayOfWeek();
                  int weeknum = DateTimeUtilities.getWeekOfYear(this.this$1.this$0._cal, this._selectedDateValue, fdow);
                  if (this.this$1._weekNum != weeknum) {
                     this.this$1._weekNum = weeknum;
                     String value = MessageFormat.format(CalendarApp._rb.getString(626), new Object[]{Integer.toString(weeknum)});
                     this.this$1._currentWeekNumber.setText(value);
                  }
               }
            }
         }
      }
   }
}
