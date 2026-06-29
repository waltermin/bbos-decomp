package net.rim.device.apps.internal.blackberryemail.address;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageAppVerb;
import net.rim.device.apps.internal.blackberryemail.email.EmailBuilder;
import net.rim.device.apps.internal.blackberryemail.email.EmailComposeVerb;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailBuilderApi;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.cldc.io.utility.URIDecoder;

class MailToEmailMessage$MailToEmailMessageVerb extends ShowMessageAppVerb {
   String subject;
   private final MailToEmailMessage this$0;

   MailToEmailMessage$MailToEmailMessageVerb(MailToEmailMessage _1) {
      super(1265920);
      this.this$0 = _1;
      this.subject = null;
   }

   @Override
   public String toString() {
      if (this.this$0._toArray.length > 0) {
         return EmailResources.getString(44) + " " + this.this$0._toArray[0];
      } else {
         return this.this$0._ccArray.length > 0 ? EmailResources.getString(44) + " " + this.this$0._ccArray[0] : EmailResources.getString(5);
      }
   }

   @Override
   public Object doInvoke(Object parameter) {
      if (this.this$0._toArray == null) {
         return null;
      }

      EmailMessageModel emm = EmailBuilder.buildMessage(null);

      for (int i = 0; i < this.this$0._toArray.length; i++) {
         EmailBuilderApi.addRecipient(emm, 0, this.this$0._toArray[i]);
      }

      EmailBuilderApi.addRecipient(emm, 0, EmailHeaderModel.createBlankFreeFormAddress(null));
      if (this.this$0._ccArray != null) {
         for (int i = 0; i < this.this$0._ccArray.length; i++) {
            EmailBuilderApi.addRecipient(emm, 1, this.this$0._ccArray[i]);
         }
      }

      EmailBuilderApi.addRecipient(emm, 1, EmailHeaderModel.createBlankFreeFormAddress(null));
      if (this.this$0._subject != null) {
         EmailBuilderApi.addSubjectLine(emm, URIDecoder.decode(this.this$0._subject, "utf-8", false));
      }

      if (this.this$0._body != null) {
         EmailBuilderApi.addMessageBody(emm, URIDecoder.decode(this.this$0._body, "utf-8", false));
      }

      ContextObject context = new ContextObject();
      context.setFlag(0);
      return EmailComposeVerb.showEditorScreen(context, parameter, emm);
   }

   @Override
   public Object invoke(Object parameter) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }
}
