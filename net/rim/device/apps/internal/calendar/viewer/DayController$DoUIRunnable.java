package net.rim.device.apps.internal.calendar.viewer;

import java.util.Vector;

final class DayController$DoUIRunnable implements Runnable {
   long _time;
   Object _object;
   boolean _updateSelectedDate;
   boolean _reposition;
   boolean _preserveSelectedTime;
   Vector _events;
   private final DayController this$0;

   DayController$DoUIRunnable(
      DayController _1, long time, Object object, boolean updateSelectedDate, boolean reposition, boolean preserveSelectedTime, Vector events
   ) {
      this.this$0 = _1;
      this._time = time;
      this._object = object;
      this._updateSelectedDate = updateSelectedDate;
      this._reposition = reposition;
      this._preserveSelectedTime = preserveSelectedTime;
      this._events = events;
   }

   @Override
   public final void run() {
      this.this$0._tod.updateTransitions(this._events, this._time);
      this._events.removeAllElements();
      this.this$0._tod.setPreferredTimeSelection(this._time);
      int selectedIndex = this.this$0._tod.getOffsetFromTime(this._time);
      if (selectedIndex < 0 && this._time < this.this$0._tod.getEndOfList() && this._time > this.this$0._tod.getVisibleEndOfList()) {
         selectedIndex = this.this$0._tod.getNumberOfItems() - 1;
      }

      this.this$0._tod.getField().setSize(this.this$0._tod._numTransitions, selectedIndex);
      this.this$0._tod.setSelectedObject(this._object);
      long currTime = System.currentTimeMillis();
      int offset = this.this$0._tod.getOffsetFromTime(currTime);
      if (offset < 0 && currTime >= this.this$0._tod.getStartOfList() && currTime - 3600000 < this.this$0._tod.getVisibleEndOfList()) {
         offset = this.this$0._tod.getNumberOfItems();
      }

      this.this$0._tod.markOffset(offset - 1);
      if (this._updateSelectedDate) {
         this.this$0.updateSelectedDate();
      }

      if (this._reposition) {
         this.this$0.repositionView(this._preserveSelectedTime);
      }
   }
}
