package net.rim.device.apps.internal.lbs;

import net.rim.device.api.lbs.gps.GPSDevice;
import net.rim.device.api.lbs.gps.GPSProvider;
import net.rim.device.internal.bluetooth.BluetoothME;

final class MapScreen$10 extends LBSMenuItem {
   private final MapScreen this$0;

   MapScreen$10(MapScreen this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   final boolean isVisible() {
      GPSDevice device = GPSProvider.getInstance().getDeviceInUse();
      return GPSProvider.isGPSSupported() && !device.equals(GPSDevice.NO_DEVICE);
   }

   @Override
   public final void run() {
      if (this.this$0._tracking) {
         this.this$0.stopTracking();
      } else {
         if (!GPSProvider.getInstance().getDeviceInUse().isInternalGPS()) {
            BluetoothME.requestPowerOn();
         }

         this.this$0.startTracking();
      }
   }

   @Override
   final int getResourceId() {
      return this.this$0._tracking ? 323 : 322;
   }
}
