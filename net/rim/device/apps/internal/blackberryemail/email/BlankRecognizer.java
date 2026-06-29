package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

class BlankRecognizer implements Recognizer {
   int _headerType;

   BlankRecognizer() {
      this._headerType = -1;
   }

   BlankRecognizer(int headerType) {
      this._headerType = headerType;
   }

   @Override
   public boolean recognize(Object o) {
      if (!(o instanceof EmailHeaderModel)) {
         return false;
      }

      EmailHeaderModel ehm = (EmailHeaderModel)o;
      return (this._headerType == -1 || ehm.getHeaderType() == this._headerType) && ehm.isBlank();
   }
}
