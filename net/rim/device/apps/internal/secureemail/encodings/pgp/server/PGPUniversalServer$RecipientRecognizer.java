package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

final class PGPUniversalServer$RecipientRecognizer implements Recognizer {
   public PGPUniversalServer$RecipientRecognizer() {
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof EmailHeaderModel;
   }
}
