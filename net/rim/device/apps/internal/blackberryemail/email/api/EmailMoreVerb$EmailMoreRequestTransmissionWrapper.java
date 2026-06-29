package net.rim.device.apps.internal.blackberryemail.email.api;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.transmission.TransmissionWrapper;

class EmailMoreVerb$EmailMoreRequestTransmissionWrapper extends TransmissionWrapper {
   EmailMessageModel _message;
   int _contentPartId;
   UiApplication _app;
   Object _context;
   byte _requestTarget;

   public EmailMoreVerb$EmailMoreRequestTransmissionWrapper(
      EmailMessageModel message, int contentPartId, String type, Object transmission, Object context, UiApplication app, byte requestTarget
   ) {
      super(type, transmission, context);
      this._app = app;
      this._message = message;
      this._contentPartId = contentPartId;
      this._requestTarget = requestTarget;
      this._context = ContextObject.clone(context);
   }

   @Override
   public void updateTransmissionStatus(byte code, int extData) {
      if (code == 1) {
         EventLogger.logEvent(-1237457833540244999L, 1632916038, 2);
         if (this._requestTarget == 1 && ModelViewListenerRegistry.isViewerUp(-6822293833372928884L, this._message, null) && this._app != null) {
            this._app.invokeLater(new EmailMoreVerb$EmailMoreRequestTransmissionWrapper$1(this));
         }
      }
   }

   @Override
   public int getTransmissionRetryDelay() {
      return 2000;
   }

   @Override
   public byte getTransmissionRetryLimit() {
      return 1;
   }
}
