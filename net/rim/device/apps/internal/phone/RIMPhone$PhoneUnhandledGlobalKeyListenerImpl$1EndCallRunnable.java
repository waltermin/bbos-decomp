package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;

final class RIMPhone$PhoneUnhandledGlobalKeyListenerImpl$1EndCallRunnable implements Runnable {
   private LiveCall _call;
   private final RIMPhone$PhoneUnhandledGlobalKeyListenerImpl this$1;

   RIMPhone$PhoneUnhandledGlobalKeyListenerImpl$1EndCallRunnable(RIMPhone$PhoneUnhandledGlobalKeyListenerImpl _1, LiveCall call) {
      this.this$1 = _1;
      this._call = call;
   }

   @Override
   public final void run() {
      if (DeviceInfo.isInHolster()) {
         System.out.println("Ignore ENDKEY since device is holstered.");
      } else {
         this.this$1.endCallByUser(this._call);
      }
   }
}
