package net.rim.device.apps.internal.lbs;

import net.rim.device.api.system.Application;

final class MapScreen$4 implements Runnable {
   private final MapScreen this$0;

   MapScreen$4(MapScreen this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      GPSMode gpsMode = (GPSMode)this.this$0._modeMgr.getMode(1);
      if (gpsMode != null && this.this$0._gpsLocationData.getSatelliteCount() > 0) {
         synchronized (Application.getApplication().getAppEventLock()) {
            gpsMode.setMessage("", 0);
            this.this$0._dashboard.refresh();
         }
      }
   }
}
