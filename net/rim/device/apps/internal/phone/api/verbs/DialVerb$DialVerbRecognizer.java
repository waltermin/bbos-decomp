package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.verb.WrapperVerb;

final class DialVerb$DialVerbRecognizer implements Recognizer {
   @Override
   public final boolean recognize(Object o) {
      if (o instanceof DialVerb) {
         return true;
      } else {
         return !(o instanceof Object) ? false : ((WrapperVerb)o).getInnerVerb() instanceof DialVerb;
      }
   }
}
