package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.cldc.util.CalendarExtensions;

final class DateTimeMonitor implements RealtimeClockListener, GlobalEventListener {
   private DateTimeMonitor$DateTimeMonitorCallback _callback;
   private Application _app;
   private Calendar _cal;
   private int _lastYear;
   private int _lastDOY;
   private int _lastHour;
   private int _lastMinute;

   public DateTimeMonitor(DateTimeMonitor$DateTimeMonitorCallback callback, Application app, Calendar cal) {
      this._callback = callback;
      this._app = app;
      this._cal = cal;
      ((CalendarExtensions)this._cal).setTimeLong(System.currentTimeMillis());
      this._lastYear = this._cal.get(1);
      this._lastDOY = this._cal.get(6);
      this._lastHour = cal.get(11);
      this._lastMinute = cal.get(12);
   }

   public final void startMonitor() {
      this._app.addRealtimeClockListener(this);
      this._app.addGlobalEventListener(this);
   }

   public final void stopMonitor() {
      this._app.removeRealtimeClockListener(this);
      this._app.removeGlobalEventListener(this);
   }

   @Override
   public final void clockUpdated() {
      ((CalendarExtensions)this._cal).setTimeLong(System.currentTimeMillis());
      int thisYear = this._cal.get(1);
      int thisDOY = this._cal.get(6);
      int thisHour = this._cal.get(11);
      int thisMinute = this._cal.get(12);
      if (thisMinute != this._lastMinute) {
         this._callback.minuteChanged();
      }

      if (thisHour != this._lastHour) {
         this._callback.hourChanged();
      }

      if (thisDOY != this._lastDOY || thisYear != this._lastYear) {
         this._callback.dateChanged();
      }

      this._lastYear = thisYear;
      this._lastDOY = thisDOY;
      this._lastHour = thisHour;
      this._lastMinute = thisMinute;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8877632280522743328L) {
         this._lastYear = this._lastDOY = this._lastHour = this._lastMinute = -1;
      } else {
         if (guid == 7207871974803693937L) {
            this._callback.dateFormatChanged();
         }
      }
   }
}
