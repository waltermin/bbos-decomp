package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

class GranularPolicyExpression$EmailHeaderModelRecognizer implements Recognizer {
   private int _headerType;

   GranularPolicyExpression$EmailHeaderModelRecognizer(int headerType) {
      this._headerType = headerType;
   }

   @Override
   public boolean recognize(Object o) {
      if (!(o instanceof Object)) {
         return false;
      }

      EmailHeaderModel headerModel = (EmailHeaderModel)o;
      return headerModel.getHeaderType() == this._headerType;
   }
}
