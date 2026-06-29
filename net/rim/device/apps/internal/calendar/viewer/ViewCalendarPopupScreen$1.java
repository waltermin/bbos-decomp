package net.rim.device.apps.internal.calendar.viewer;

final class ViewCalendarPopupScreen$1 implements Runnable {
   private final ViewCalendarPopupScreen this$0;

   ViewCalendarPopupScreen$1(ViewCalendarPopupScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      label17:
      try {
         Thread.sleep(100);
      } finally {
         break label17;
      }

      this.this$0.close();
   }
}
