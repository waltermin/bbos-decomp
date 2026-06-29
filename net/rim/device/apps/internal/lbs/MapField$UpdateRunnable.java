package net.rim.device.apps.internal.lbs;

final class MapField$UpdateRunnable implements Runnable {
   private final MapField this$0;

   MapField$UpdateRunnable(MapField this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      this.this$0.update(true);
      this.this$0._updatePID = -1;
   }
}
