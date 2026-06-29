package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

class NonBlankRecognizer implements Recognizer {
   int _headerType;

   NonBlankRecognizer() {
      this._headerType = -1;
   }

   NonBlankRecognizer(int headerType) {
      this._headerType = headerType;
   }

   @Override
   public boolean recognize(Object o) {
      if (!(o instanceof LargeAttachmentModel$LargeCachedAttachmentModel)) {
         return false;
      }

      EmailHeaderModel ehm = (LargeAttachmentModel$LargeCachedAttachmentModel)o;
      return (this._headerType == -1 || ehm.getHeaderType() == this._headerType) && !ehm.isBlank();
   }
}
