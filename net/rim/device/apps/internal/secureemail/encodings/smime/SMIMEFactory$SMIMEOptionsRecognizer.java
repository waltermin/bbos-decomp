package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.apps.api.framework.model.Recognizer;

public final class SMIMEFactory$SMIMEOptionsRecognizer implements Recognizer {
   @Override
   public final boolean recognize(Object o) {
      return o instanceof SMIMEOptions;
   }
}
