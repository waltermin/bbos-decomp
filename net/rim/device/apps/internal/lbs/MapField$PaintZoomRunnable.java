package net.rim.device.apps.internal.lbs;

final class MapField$PaintZoomRunnable implements Runnable {
   private final MapField this$0;

   MapField$PaintZoomRunnable(MapField this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      this.this$0._paintZoom = false;
      this.this$0._paintZoomPID = -1;
      if (this.this$0._timer.isRenderingDone()) {
         this.this$0._timer.stopTiming();
      }

      this.this$0.invalidate();
   }
}
