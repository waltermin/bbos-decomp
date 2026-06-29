package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.apps.api.framework.verb.Verb;

final class IgnoreVerbRunner extends VerbRunner {
   public IgnoreVerbRunner(Verb verb, Object context) {
      super(verb, context);
   }

   @Override
   public final void run() {
      if (DeviceInfo.isInHolster()) {
         super.run();
      }
   }
}
