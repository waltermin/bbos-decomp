package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

class ValidRecognizer implements Recognizer {
   @Override
   public boolean recognize(Object o) {
      if (!(o instanceof LargeAttachmentModel$LargeCachedAttachmentModel)) {
         return false;
      }

      EmailHeaderModel ehm = (LargeAttachmentModel$LargeCachedAttachmentModel)o;
      return ehm.isValidToSend();
   }
}
