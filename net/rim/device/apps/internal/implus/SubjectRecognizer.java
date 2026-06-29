package net.rim.device.apps.internal.implus;

import net.rim.device.apps.api.framework.model.Recognizer;

final class SubjectRecognizer implements Recognizer {
   @Override
   public final boolean recognize(Object o) {
      return o instanceof Object;
   }
}
