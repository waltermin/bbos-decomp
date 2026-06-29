package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.phone.VoiceServices;

final class PhoneBackdoorHelpScreen$6 implements Runnable {
   private final PhoneBackdoorHelpScreen this$0;

   PhoneBackdoorHelpScreen$6(PhoneBackdoorHelpScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      VoiceServices.postEvent(150070, 1, new Integer(3));
   }
}
