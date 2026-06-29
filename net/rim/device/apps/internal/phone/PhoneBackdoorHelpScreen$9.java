package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.Application;
import net.rim.device.apps.api.phone.VoiceServices;

final class PhoneBackdoorHelpScreen$9 implements Runnable {
   private boolean _connected;
   private final PhoneBackdoorHelpScreen this$0;

   PhoneBackdoorHelpScreen$9(PhoneBackdoorHelpScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (!this._connected) {
         this._connected = true;
         int[] scclParams = new int[]{1, 1, 1234, -804651005, 3, 1, 12, -804651004, 4, 5, 6, 9};
         VoiceServices.broadcastEvent(201000, 1, scclParams);
         Application.getApplication().invokeLater(this, 5000, false);
      } else {
         VoiceServices.broadcastEvent(201010, 1, null);
      }
   }
}
