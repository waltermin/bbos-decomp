package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.internal.blackberryemail.header.SubjectModel;

final class EmailSendVerb$SubjectModelRecognizer implements Recognizer {
   private EmailSendVerb$SubjectModelRecognizer() {
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof SubjectModel;
   }

   EmailSendVerb$SubjectModelRecognizer(EmailSendVerb$1 x0) {
      this();
   }
}
