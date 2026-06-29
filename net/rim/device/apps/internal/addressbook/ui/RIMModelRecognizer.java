package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;

final class RIMModelRecognizer implements Recognizer {
   @Override
   public final boolean recognize(Object object) {
      Recognizer groupRecognizer = RecognizerRepository.getRecognizers(-1326186686655625745L);
      if (groupRecognizer != null && groupRecognizer.recognize(object)) {
         return false;
      } else {
         return object instanceof Object ? false : object instanceof Object;
      }
   }
}
