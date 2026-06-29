package net.rim.device.apps.internal.blackberryemail.address;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.internal.blackberryemail.header.SubjectModel;

class MailToEmailMessage$MailToEmailMessageVerb$SubjectRecognizer implements Recognizer {
   private final MailToEmailMessage$MailToEmailMessageVerb this$1;

   MailToEmailMessage$MailToEmailMessageVerb$SubjectRecognizer(MailToEmailMessage$MailToEmailMessageVerb _1) {
      this.this$1 = _1;
   }

   @Override
   public boolean recognize(Object o) {
      return o instanceof SubjectModel;
   }
}
