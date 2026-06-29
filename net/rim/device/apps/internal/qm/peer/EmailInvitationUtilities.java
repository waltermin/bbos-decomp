package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModelImpl;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailBuilderApi;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;

final class EmailInvitationUtilities {
   public static final String EMAIL_START_BOUNDARY = "-----￼----\n";
   public static final String EMAIL_BOUNDARY = "-------o-------";
   public static final String BACKWARDS_COMPATIBLE_BOUNDARY = "-------AM-------";

   private EmailInvitationUtilities() {
   }

   public static final boolean canSendEmail() {
      RIMMessagingService service = (RIMMessagingService)TransmissionServiceManager.get(8399767144006445082L);
      return service == null ? false : service.getOutgoingServiceRecord() != null;
   }

   public static final void sendEmailMessage(String to, String subject, String body, EmailInvitation invite, boolean show, boolean includeInvite) {
      boolean isPin = invite.isPin();
      ContextObject contextObject = (ContextObject)(new Object());
      contextObject.setFlag(31);
      contextObject.setFlag(85);
      if (invite.isPin()) {
         contextObject.setFlag(94);
      }

      EmailMessageModelImpl msg = (EmailMessageModelImpl)(new Object(contextObject));
      String[] names = new Object[2];
      names[0] = to;
      names[1] = to;
      ContextObject context = (ContextObject)(new Object());
      ContextObject.put(context, 251, names);
      Object recipient = FactoryUtil.createInstance(-2985347935260258684L, context);
      EmailBuilderApi.addRecipient(msg, 0, (RIMModel)recipient);
      EmailBuilderApi.addSubjectLine(msg, subject);
      if (includeInvite) {
         byte[] data = invite.pickle(body);
         char[] encoded = Utils.encodeBase64(data, 0, data.length);
         body = ((StringBuffer)(new Object())).append(body).append("\n\n\n").toString();
         if (invite._backwards) {
            body = ((StringBuffer)(new Object())).append(body).append("-------AM-------").toString();
         } else {
            body = ((StringBuffer)(new Object())).append(body).append("-------o-------").toString();
         }

         body = ((StringBuffer)(new Object())).append(body).append('\n').toString();
         String encodedtext = (String)(new Object(encoded));
         body = ((StringBuffer)(new Object())).append(body).append(encodedtext.length()).toString();
         body = ((StringBuffer)(new Object())).append(body).append(':').toString();
         body = ((StringBuffer)(new Object())).append(body).append(encodedtext).toString();
         body = ((StringBuffer)(new Object())).append(body).append("\n\n").toString();
      }

      body = ((StringBuffer)(new Object("-----￼----\n"))).append(body).toString();
      EmailBuilderApi.addMessageBody(msg, body);
      msg.setType((byte)32);
      RIMMessagingService service = (RIMMessagingService)TransmissionServiceManager.get(8399767144006445082L);
      if (service != null) {
         ServiceRecord sr = service.getOutgoingServiceRecord();
         if (sr != null || isPin) {
            context.reset();
            EmailSendUtility.sendMessage(msg, sr, new Object());
            if (!show) {
               EmailHierarchy.removeMessage(msg, msg.getFolderId());
            }
         }
      }
   }
}
