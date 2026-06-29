package net.rim.device.apps.internal.calendar.viewer;

import java.util.Vector;

final class WeekController$DoUIRunnable implements Runnable {
   boolean _updateSelectedDate;
   Object _object;
   Vector _events;
   long _time;
   private final WeekController this$0;

   WeekController$DoUIRunnable(WeekController _1, long time, boolean updateSelectedDate, Object object, Vector events) {
      this.this$0 = _1;
      this._updateSelectedDate = updateSelectedDate;
      this._object = object;
      this._events = events;
      this._time = time;
   }

   @Override
   public final void run() {
      this.this$0._weekField.paintWeek(this._time, this._object, this._events);
      if (this._updateSelectedDate) {
         this.this$0.updateSelectedDate();
      }
   }
}
