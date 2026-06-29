package net.rim.device.apps.internal.phone.control;

import net.rim.device.api.system.SIMCard;
import net.rim.device.internal.callcontrol.CallEventHandler;

class NetworkTweakHandler$NetworkTweakEventHandler extends CallEventHandler {
   private final NetworkTweakHandler this$0;

   public NetworkTweakHandler$NetworkTweakEventHandler(NetworkTweakHandler _1) {
      super(150);
      this.this$0 = _1;
   }

   private String mapVoicemailNumber(int mcc, int mnc) {
      int lookup = mnc * 1000 + mcc;

      for (int i = 0; i < NetworkTweakHandler._codes.length; i++) {
         if (lookup == NetworkTweakHandler._codes[i] && i < NetworkTweakHandler._numbers.length) {
            return NetworkTweakHandler._numbers[i];
         }
      }

      return null;
   }

   @Override
   public void featureReady() {
      super.featureReady();
      if (SIMCard.isSupported()) {
         try {
            this.this$0._mcc = SIMCard.getMCCFromIMSI(SIMCard.getIMSI());
            int mnc = SIMCard.getMNCFromIMSI(SIMCard.getIMSI());
            this.this$0._voiceMailNumber = this.mapVoicemailNumber(this.this$0._mcc, mnc);
         } finally {
            return;
         }
      }
   }
}
