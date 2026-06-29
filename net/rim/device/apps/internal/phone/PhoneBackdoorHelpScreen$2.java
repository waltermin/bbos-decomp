package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.phone.VoiceServices;

final class PhoneBackdoorHelpScreen$2 implements Runnable {
   private final PhoneBackdoorHelpScreen this$0;

   PhoneBackdoorHelpScreen$2(PhoneBackdoorHelpScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      int[] params = new int[]{
         25,
         5,
         3,
         0,
         0,
         51,
         1866858752,
         -1574934772,
         1208025188,
         186346607,
         1866989824,
         1158355820,
         16812662,
         -1972564893,
         186343757,
         -1888719018,
         1711341669,
         16805145,
         38616944,
         -682312344
      };
      VoiceServices.broadcastEvent(5000, 0, params);
   }
}
