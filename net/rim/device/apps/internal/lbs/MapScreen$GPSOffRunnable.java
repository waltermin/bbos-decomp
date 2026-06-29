package net.rim.device.apps.internal.lbs;

import net.rim.device.api.lbs.gps.GPSDevice;
import net.rim.device.api.lbs.gps.GPSProvider;
import net.rim.device.api.system.EventLogger;

final class MapScreen$GPSOffRunnable implements Runnable {
   GPSDevice _device;
   private final MapScreen this$0;

   MapScreen$GPSOffRunnable(MapScreen this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      EventLogger.logEvent(LBSApplication.UID, ((StringBuffer)(new Object("Pausing GPS NOW: "))).append(this._device).toString().getBytes(), 0);
      if (this._device.isInternalGPS()) {
         GPSProvider.getInstance().stopReporting(this._device);
      } else {
         this.this$0.stopTracking();
      }

      this.this$0._reconnectGPS = true;
      this.this$0._gpsPID = -1;
   }
}
