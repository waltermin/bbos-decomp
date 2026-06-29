package net.rim.device.apps.internal.blackberryemail.sendmethods;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.messaging.EmailBodyProvider;

final class SendMethodSelector$EmailBodyProviderRecognizer implements Recognizer {
   private SendMethodSelector$EmailBodyProviderRecognizer() {
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof EmailBodyProvider;
   }

   SendMethodSelector$EmailBodyProviderRecognizer(SendMethodSelector$1 x0) {
      this();
   }
}
