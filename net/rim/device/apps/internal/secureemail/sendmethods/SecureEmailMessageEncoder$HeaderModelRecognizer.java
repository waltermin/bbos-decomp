package net.rim.device.apps.internal.secureemail.sendmethods;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

public final class SecureEmailMessageEncoder$HeaderModelRecognizer implements Recognizer {
   @Override
   public final boolean recognize(Object o) {
      return o instanceof EmailHeaderModel;
   }
}
