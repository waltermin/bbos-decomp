package net.rim.device.apps.internal.implus;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.internal.blackberryemail.header.SubjectModel;

final class SubjectRecognizer implements Recognizer {
   @Override
   public final boolean recognize(Object o) {
      return o instanceof SubjectModel;
   }
}
