package net.rim.device.apps.internal.vad;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.vad.VADUserEvents;

final class VADOptionsScreen$AdaptDigitsVerb extends Verb {
   VADOptionsScreen$AdaptDigitsVerb() {
      super(2097152, VADOptionsScreen._rb, 29);
   }

   @Override
   public final Object invoke(Object context) {
      VADUserEvents.sendEvent(4, 0);
      return null;
   }
}
