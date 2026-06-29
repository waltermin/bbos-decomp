package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.phone.VoiceServices;

final class PhoneBackdoorHelpScreen$8 implements Runnable {
   private final PhoneBackdoorHelpScreen this$0;

   PhoneBackdoorHelpScreen$8(PhoneBackdoorHelpScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      VoiceServices.broadcastEvent(202020, 11, null);
   }
}
