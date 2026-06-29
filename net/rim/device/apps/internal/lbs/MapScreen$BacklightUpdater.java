package net.rim.device.apps.internal.lbs;

import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.DeviceInfo;

final class MapScreen$BacklightUpdater implements Runnable {
   private final MapScreen this$0;

   private MapScreen$BacklightUpdater(MapScreen this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      int bStat = DeviceInfo.getBatteryStatus();
      boolean isCharging = (bStat & 1) != 0 || (bStat & 8) != 0 || (bStat & 4) != 0;
      if ((isCharging || DeviceInfo.getBatteryLevel() > this.this$0._batteryBacklightLevel) && this.this$0._appInForeground && this.this$0._keepBacklightOn) {
         Backlight.resetElapsedTime();
      }
   }

   MapScreen$BacklightUpdater(MapScreen x0, MapScreen$1 x1) {
      this(x0);
   }
}
