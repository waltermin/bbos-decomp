package net.rim.device.apps.internal.lbs;

final class MapScreen$HideDashboardRunnable implements Runnable {
   private final MapScreen this$0;

   MapScreen$HideDashboardRunnable(MapScreen this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      AbstractMode mode = this.this$0._dashboard.getMode();
      if (mode != null && mode.getView() != 0) {
         mode.changeView(0);
         this.this$0._hideDashboardPID = -1;
         MapScreen.access$800(this.this$0);
      }
   }
}
