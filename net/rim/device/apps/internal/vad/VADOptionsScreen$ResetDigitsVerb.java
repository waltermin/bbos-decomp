package net.rim.device.apps.internal.vad;

import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.vad.VADUserEvents;

final class VADOptionsScreen$ResetDigitsVerb extends Verb {
   VADOptionsScreen$ResetDigitsVerb() {
      super(2097152, VADOptionsScreen._rb, 36);
   }

   @Override
   public final Object invoke(Object context) {
      VADUserEvents.sendEvent(5, 0);
      Status.show(VADOptionsScreen._rb.getString(38));
      return null;
   }
}
