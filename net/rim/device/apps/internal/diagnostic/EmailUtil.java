package net.rim.device.apps.internal.diagnostic;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModelImpl;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailBuilderApi;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;

public final class EmailUtil extends Thread {
   int type;
   String dest;
   String subject;
   String body;
   EmailMessageModel msgHandler;

   public EmailUtil(int _type, String _dest, String _subject, String _body) {
      this.type = _type;
      this.dest = _dest;
      this.subject = _subject;
      this.body = _body;
   }

   @Override
   public final void run() {
      try {
         ContextObject contextObject = new ContextObject();
         contextObject.setFlag(31);
         contextObject.setFlag(85);
         switch (this.type) {
            case -1:
               break;
            case 0:
            default:
               contextObject.setFlag(94);
               break;
            case 1:
               contextObject.setFlag(43);
         }

         EmailMessageModelImpl msg = new EmailMessageModelImpl(contextObject);
         String[] names = new String[]{this.dest, this.dest};
         ContextObject context = new ContextObject();
         ContextObject.put(context, 251, names);
         Object recipient = FactoryUtil.createInstance(-2985347935260258684L, context);
         EmailBuilderApi.addRecipient(msg, 0, (RIMModel)recipient);
         EmailBuilderApi.addSubjectLine(msg, this.subject);
         EmailBuilderApi.addMessageBody(msg, this.body);
         msg.setType((byte)32);
         RIMMessagingService service = (RIMMessagingService)TransmissionServiceManager.get(8399767144006445082L);
         if (service != null) {
            ServiceRecord sr = service.getOutgoingServiceRecord();
            context.reset();
            this.msgHandler = EmailSendUtility.sendMessage(msg, sr, new ContextObject());
            EmailHierarchy.removeMessage(msg, msg.getFolderId());

            while (this.msgHandler.getStatus() != 16383) {
               if (this.msgHandler.getStatus() == 33554431) {
                  Diag.showMessage(DiagnosticResources.getString(16));
                  return;
               }

               Thread.sleep(500);
            }

            Diag.showMessage(DiagnosticResources.getString(17));
         }
      } finally {
         return;
      }
   }
}
