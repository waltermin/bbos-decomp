package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.TransportRegistry;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;

final class EmailResendVerb extends Verb {
   private EmailMessageModelImpl _message;

   EmailResendVerb(EmailMessageModelImpl message) {
      super(611440, CommonResources.getResourceBundle(), 9151);
      this._message = message;
   }

   EmailResendVerb() {
      this(null);
   }

   final void setParameters(EmailMessageModelImpl message) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final Object invoke(Object context) {
      if (!MessagingUtil.confirmOnSend(null)) {
         return null;
      }

      ContextObject contextObject = ContextObject.castOrCreate(context);
      Object nnePassword = null;
      if (NNEPasswordManager.isPasswordRequired(this._message)) {
         nnePassword = NNEPasswordManager.confirmEncodedPassword(this._message);
         if (nnePassword == null) {
            return null;
         }

         contextObject.put(129, nnePassword);
      }

      ServiceRecord serviceRecord = EmailMessageUtilities.getServiceRecordForMessage(this._message);
      EmailTransport et = null;
      if (serviceRecord == null) {
         TransportRegistry tr = TransportRegistry.getInstance();
         et = (EmailTransport)tr.get("CMIME");
      } else {
         et = (EmailTransport)serviceRecord.getTransport();
      }

      et.resendMessage(this._message, serviceRecord, contextObject);
      if (!ContextObject.getFlag(contextObject, 121)) {
         ShowMessageApp.showMessageApp();
      }

      return new ContextObject(39, 40);
   }
}
