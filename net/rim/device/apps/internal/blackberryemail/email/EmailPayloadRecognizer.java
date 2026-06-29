package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.apps.api.framework.model.Recognizer;

public final class EmailPayloadRecognizer implements Recognizer {
   private static EmailPayloadRecognizer _singletonInstance;

   EmailPayloadRecognizer() {
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof EmailPayloadModel;
   }

   public static final synchronized EmailPayloadRecognizer getInstance() {
      if (_singletonInstance == null) {
         _singletonInstance = new EmailPayloadRecognizer();
      }

      return _singletonInstance;
   }
}
