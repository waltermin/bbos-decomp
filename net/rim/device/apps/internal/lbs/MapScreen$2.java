package net.rim.device.apps.internal.lbs;

import net.rim.device.api.lbs.gps.GPSDevice;

final class MapScreen$2 implements Runnable {
   private final GPSDevice val$device;
   private final MapScreen this$0;

   MapScreen$2(MapScreen this$0, GPSDevice val$device) {
      this.this$0 = this$0;
      this.val$device = val$device;
   }

   @Override
   public final void run() {
      if (this.val$device.getDeviceState() == 1) {
         byte[] emptyData = new byte[]{0};
         this.val$device.messageDevice(emptyData);
      }
   }
}
