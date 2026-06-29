package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.phone.VoiceServices;

final class PhoneBackdoorHelpScreen$4 implements Runnable {
   private final int val$errorCode;
   private final int val$reasonCode;
   private final PhoneBackdoorHelpScreen this$0;

   PhoneBackdoorHelpScreen$4(PhoneBackdoorHelpScreen _1, int _2, int _3) {
      this.this$0 = _1;
      this.val$errorCode = _2;
      this.val$reasonCode = _3;
   }

   @Override
   public final void run() {
      VoiceServices.broadcastEvent(5001, 0, new int[]{this.val$errorCode, this.val$reasonCode});
   }
}
