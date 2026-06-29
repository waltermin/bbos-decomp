package net.rim.device.apps.internal.phone.control;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.apps.api.phone.VoiceServices;

class NetworkTweakHandler$MyRadioStatusListener implements RadioStatusListener {
   private Application _app;
   private final NetworkTweakHandler this$0;

   NetworkTweakHandler$MyRadioStatusListener(NetworkTweakHandler _1) {
      this.this$0 = _1;
      this._app = VoiceServices.getUiApplication();
   }

   public void startListening() {
      this._app.addRadioListener(this);
   }

   public void stopListening() {
      this._app.removeRadioListener(this);
   }

   @Override
   public void signalLevel(int level) {
   }

   @Override
   public void networkStarted(int networkId, int service) {
      this.this$0._networkService = service;
      this.this$0._myCommandHandler.checkForNetworkFeatureUpdate();
   }

   @Override
   public void baseStationChange() {
   }

   @Override
   public void radioTurnedOff() {
   }

   @Override
   public void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public void networkStateChange(int state) {
   }

   @Override
   public void networkScanComplete(boolean success) {
   }

   @Override
   public void networkServiceChange(int networkId, int service) {
      this.this$0._networkService = service;
      this.this$0._myCommandHandler.checkForNetworkFeatureUpdate();
   }
}
