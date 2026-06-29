package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.TrackwheelListener;

class VolumeIndicator$1 implements TrackwheelListener {
   private final VolumeIndicator this$0;

   VolumeIndicator$1(VolumeIndicator _1) {
      this.this$0 = _1;
   }

   @Override
   public boolean trackwheelRoll(int amount, int status, int time) {
      if (!DeviceInfo.isInHolster()) {
         if (this.this$0._screenRef != null && this.this$0._screenRef.get() != this.this$0._app.getActiveScreen()) {
            return false;
         }

         this.this$0._router.incrementMasterVolume(-10 * amount);
      }

      return false;
   }

   @Override
   public boolean trackwheelClick(int status, int time) {
      return false;
   }

   @Override
   public boolean trackwheelUnclick(int status, int time) {
      return false;
   }
}
