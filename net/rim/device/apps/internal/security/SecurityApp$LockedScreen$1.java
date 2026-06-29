package net.rim.device.apps.internal.security;

import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.DeviceInfo;

final class SecurityApp$LockedScreen$1 implements Runnable {
   private final SecurityApp$LockedScreen this$1;

   SecurityApp$LockedScreen$1(SecurityApp$LockedScreen _1) {
      this.this$1 = _1;
   }

   @Override
   public final void run() {
      if (this.this$1.this$0._turnBacklightOff && DeviceInfo.getIdleTime() >= 1) {
         this.this$1.this$0._turnBacklightOff = false;
         Backlight.enable(false);
      }
   }
}
