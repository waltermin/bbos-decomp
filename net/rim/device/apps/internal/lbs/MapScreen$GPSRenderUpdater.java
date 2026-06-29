package net.rim.device.apps.internal.lbs;

final class MapScreen$GPSRenderUpdater implements Runnable {
   private boolean _pending;
   private final MapScreen this$0;

   private MapScreen$GPSRenderUpdater(MapScreen this$0) {
      this.this$0 = this$0;
   }

   final synchronized void invoke() {
      if (!this._pending) {
         this._pending = true;
         this.this$0._application.invokeLater(this);
      }
   }

   @Override
   public final void run() {
      this.this$0.locationUpdatedInternal();
      this._pending = false;
   }

   MapScreen$GPSRenderUpdater(MapScreen x0, MapScreen$1 x1) {
      this(x0);
   }
}
