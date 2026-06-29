package net.rim.blackberry.api.mail;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.MorePartModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMoreVerb;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;

public class Transport extends Service {
   private static final int MORE_REQUEST_SIZE = 1500;

   Transport() {
   }

   public static void send(Message msg) {
      if (Message.EMAIL_MESSAGE == msg.getMessageType()) {
         if (ITPolicy.getBoolean(24, 5, false)) {
            throw new MessagingException("Email message not sent. Do not have the permissions to send the message.");
         }
      } else if (Message.PIN_MESSAGE == msg.getMessageType() && ITPolicy.getBoolean(24, 6, false)) {
         throw new MessagingException("PIN message not sent. Do not have the permissions to send the message.");
      }

      Folder f = msg.getFolder();
      ServiceConfiguration sc = Session.getDefaultInstance().getServiceConfiguration();
      if (f != null) {
         sc = f.getStore().getServiceConfiguration();
         if (sc == null) {
            throw new SendFailedException(SendFailedException.ERR_SERVICE_RECORD);
         }
      }

      ServiceRecord sr = sc.getServiceRecord();
      EmailSendUtility.sendMessage(msg.getEmailMessageModel(), sr, new Object());
   }

   public static void more(BodyPart bp, boolean reqAll) {
      Message msg = (Message)bp.getParent().getParent();
      if (msg == null) {
         throw new MessagingException("Transport.more(): the provided bodypart does not belong to any message");
      }

      EmailMessageModel em = msg.getEmailMessageModel();
      EmailMoreVerb emv = (EmailMoreVerb)(new Object(em, (byte)(reqAll ? 2 : 1)));
      MorePartModel mpm = bp.getMorePartModel();
      if (mpm == null) {
         throw new MessagingException("Transport.more():  the provided bodypart does not support the more operation");
      }

      ContextObject c = (ContextObject)(new Object());
      c.put(254, mpm);
      emv.invoke(c);
   }
}
