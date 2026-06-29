package net.rim.device.apps.internal.calendar.viewer;

final class CalendarSearchResultField$UpdateListRunnable implements Runnable {
   private final CalendarSearchResultField this$0;

   CalendarSearchResultField$UpdateListRunnable(CalendarSearchResultField _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.doUpdateList();
   }
}
