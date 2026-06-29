package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.system.DeviceInternal;
import net.rim.device.api.system.EventLogger;

final class AutoOffDialog$1 implements Runnable {
   private final AutoOffDialog this$0;

   AutoOffDialog$1(AutoOffDialog _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      EventLogger.logEvent(-6210296463828503575L, "Device off request".getBytes(), 0);
      RadioOffWarningManagerImpl.fire(3);
      DeviceInternal.requestPowerOff(this.this$0._allowAutoOn || this.this$0._forceAutoOn);
   }
}
