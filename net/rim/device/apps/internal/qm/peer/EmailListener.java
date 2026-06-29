package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceListener;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.RIMMessagingIncomingMessage;
import net.rim.device.apps.api.transmission.rim.RIMMessagingMessage;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;

final class EmailListener implements TransmissionServiceListener {
   static final void register() {
      TransmissionService transmissionService = TransmissionServiceManager.get(8399767144006445082L);
      if (transmissionService != null) {
         EmailListener emailListener = new EmailListener();
         transmissionService.addTransmissionServiceListener(
            "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE", 70, emailListener
         );
      }
   }

   @Override
   public final boolean receiveObject(TransmissionService aTransmissionService, Object transmissionObject, Object context) {
      ContextObject contextObject = ContextObject.castOrCreate(context);
      RIMMessagingIncomingMessage incMsg = null;
      String emailtext = null;
      if (!(transmissionObject instanceof Object)) {
         return false;
      }

      RIMMessagingIncomingMessage var18 = transmissionObject;
      Object emailbody = ((RIMMessagingMessage)var18).getText();
      if (!(emailbody instanceof Object)) {
         return false;
      }

      emailtext = (String)emailbody;
      emailbody = null;
      String body = null;
      if ((body = EmailInvitation.stripBody(emailtext)) == null) {
         return false;
      }

      if ((emailbody = EmailInvitation.stripData(emailtext)) == null) {
         return false;
      }

      EmailInvitation email = EmailInvitation.makeInbound();

      String sender;
      String recipient;
      try {
         if (!email.unPickle((byte[])emailbody)) {
            return false;
         }

         String[][] address = ((RIMMessagingMessage)var18).getFrom();
         sender = address[0][0];
         email.setReplyTo(sender);
         address = ((RIMMessagingMessage)var18).getTo();
         recipient = address[0][0];
      } finally {
         ;
      }

      if (contextObject.getFlag(94)) {
         email.setIsPin(true);
      }

      int exist = emailtext.indexOf("-------AM-------");
      if (exist != -1) {
         email._backwards = true;
      }

      String finalbody = Utils.stripLastTwoParagraphs(body);
      ServiceRecord serviceRecord = ((RIMMessagingService)aTransmissionService).getOutgoingServiceRecord();
      int refId = ((RIMMessagingMessage)var18).getReferenceIdentifier();
      PeerApplication.getInstance().invokeLater(new EmailListener$1(this, email, sender, finalbody, recipient, serviceRecord, refId));
      return true;
   }

   @Override
   public final void statusChanged(TransmissionService aTransmissionService, int statusInt, Object contextObject) {
   }
}
