package net.rim.device.api.gps;

final class GPSRegistry$GPSCleanerRunnable implements Runnable {
   int _processId;
   int _mode;
   private final GPSRegistry this$0;

   public GPSRegistry$GPSCleanerRunnable(GPSRegistry _1, int processId, int mode) {
      this.this$0 = _1;
      this._processId = processId;
      this._mode = mode;
   }

   @Override
   public final void run() {
      synchronized (this.this$0) {
         this.this$0.stopLocationUpdate(this._mode, this._processId);
      }
   }
}
