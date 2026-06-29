package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.internal.blackberryemail.header.SubjectModel;

class SubjectRecognizer implements Recognizer {
   @Override
   public boolean recognize(Object o) {
      return o instanceof SubjectModel;
   }
}
