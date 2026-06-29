package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.ribbon.skin.svg.NewMessageFilter;
import net.rim.plazmic.internal.mediaengine.model.intarray.v1_2.ModelInteractorImpl;

class SMSHandler extends EmailHandler implements Runnable {
   private static final String ACTIVATE_SMS = "ActivateSMS";
   private static final String ACTIVATE_SMS_MMS = "ActivateSMSMMS";

   public SMSHandler(ModelInteractorImpl mi, UiApplication app, NewMessageFilter msgCollection) {
      super(mi, app, msgCollection);
      super._hotspotType = "sms";
      super._modelInteractor.trigger(107, super._modelInteractor.getHandle("ActivateSMS"), null);
   }

   @Override
   protected boolean isItemValid(Object item) {
      return true;
   }

   @Override
   public void run() {
      super._modelInteractor.trigger(107, super._modelInteractor.getHandle("ActivateSMSMMS"), null);
   }
}
