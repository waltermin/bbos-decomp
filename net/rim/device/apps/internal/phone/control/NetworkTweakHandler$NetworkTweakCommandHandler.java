package net.rim.device.apps.internal.phone.control;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.internal.callcontrol.CallCommandHandler;

class NetworkTweakHandler$NetworkTweakCommandHandler extends CallCommandHandler {
   private NetworkTweakHandler$MyRadioStatusListener _myRadioStatusListener;
   private final NetworkTweakHandler this$0;
   private static final int NOT_MPTY = -32769;

   public NetworkTweakHandler$NetworkTweakCommandHandler(NetworkTweakHandler _1) {
      super(150);
      this.this$0 = _1;
      this._myRadioStatusListener = new NetworkTweakHandler$MyRadioStatusListener(this.this$0);
   }

   @Override
   protected void onRegistration() {
      super.onRegistration();
      this._myRadioStatusListener.startListening();
   }

   @Override
   protected void onDeregistration() {
      super.onDeregistration();
      this._myRadioStatusListener.stopListening();
   }

   @Override
   public int startCall(String number, int clir) {
      if (PhoneUtilities.getDebugFlag(-6324595103497951497L)
         && this.this$0._mcc == 440
         && (this.this$0._networkService & 8) == 0
         && number != null
         && number.length() >= 3
         && number.charAt(0) == '+'
         && (number.charAt(1) != '8' || number.charAt(2) != '1')) {
         number = "009130010" + number.substring(1);
      }

      return super.startCall(number, clir);
   }

   @Override
   public void startDTMF(int callId, byte character) {
      super.startDTMF(callId & -32769, character);
   }

   @Override
   public void stopDTMF(int callId) {
      super.stopDTMF(callId & -32769);
   }

   @Override
   public int getNetworkFeatures() {
      if (this.this$0._networkFeatures == 0) {
         this.this$0._networkFeatures = super.getNetworkFeatures();
      }

      return this.this$0._networkFeatures;
   }

   void checkForNetworkFeatureUpdate() {
      if (this.isRegistered()) {
         int oldFeatures = this.getNetworkFeatures();
         int newFeatures = super.getNetworkFeatures();
         if (oldFeatures != newFeatures) {
            if (((oldFeatures ^ newFeatures) & 2097152) != 0) {
               this.this$0._myEventHandler.alternateLinesUpdated();
            }

            this.this$0._networkFeatures = newFeatures;
         }
      }
   }

   @Override
   public boolean setAlternateLine(int line) {
      if ((this.getNetworkFeatures() & 2097152) == 0) {
         if (line == 1) {
            this.this$0._myEventHandler.voiceLineChanged(line);
            return true;
         } else {
            return false;
         }
      } else {
         boolean result = super.setAlternateLine(line);
         if (result && RadioInfo.getNetworkType() == 3) {
            this.this$0._myEventHandler.voiceLineChanged(line);
         }

         return result;
      }
   }

   @Override
   public String getAlternateLineLabel(int lineId) {
      return PhoneOptions.getOptions().getLineDescription(lineId);
   }

   @Override
   public void setAlternateLineLabel(int lineId, String description) {
      PhoneOptions.getOptions().setLineDescription(lineId, description);
      this.this$0._myEventHandler.alternateLinesUpdated();
   }

   @Override
   public String getVoiceMailNumber(int line) {
      if (line != 1 && line != 2) {
         return super.getVoiceMailNumber(line);
      }

      String number = null;

      try {
         number = super.getVoiceMailNumber(line);
      } finally {
         ;
      }

      return number != null && number.length() > 0 ? number : this.this$0._voiceMailNumber;
   }
}
