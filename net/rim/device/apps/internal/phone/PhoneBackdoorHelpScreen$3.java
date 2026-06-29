package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.phone.VoiceServices;

final class PhoneBackdoorHelpScreen$3 implements Runnable {
   private final PhoneBackdoorHelpScreen this$0;

   PhoneBackdoorHelpScreen$3(PhoneBackdoorHelpScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      Dialog.inform("Invalidating SIM card...");
      VoiceServices.broadcastEvent(100201);
   }
}
