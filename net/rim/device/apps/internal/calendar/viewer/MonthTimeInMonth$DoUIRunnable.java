package net.rim.device.apps.internal.calendar.viewer;

final class MonthTimeInMonth$DoUIRunnable implements Runnable {
   private long _viewTime;
   private final MonthTimeInMonth this$0;

   MonthTimeInMonth$DoUIRunnable(MonthTimeInMonth _1, long viewTime) {
      this.this$0 = _1;
      this._viewTime = 0;
      this._viewTime = viewTime;
   }

   @Override
   public final void run() {
      this.this$0.setupMonthField(this._viewTime);
   }
}
