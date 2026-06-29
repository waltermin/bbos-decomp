package net.rim.device.apps.internal.secureemail.encodings.pgp;

import net.rim.device.apps.api.framework.model.Recognizer;

public final class PGPFactory$PGPOptionsRecognizer implements Recognizer {
   @Override
   public final boolean recognize(Object o) {
      return o instanceof PGPOptions;
   }
}
