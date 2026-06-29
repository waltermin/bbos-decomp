package net.rim.wica.runtime.activation.internal;

import net.rim.device.api.ui.UiApplication;

class ActivationServiceImpl$PushActivationScreen extends UiApplication {
   ActivationServiceImpl$PushActivationScreen(ActivationServiceImpl activationService) {
      this.pushScreen(new AGMainScreen(activationService));
      this.enterEventDispatcher();
   }
}
