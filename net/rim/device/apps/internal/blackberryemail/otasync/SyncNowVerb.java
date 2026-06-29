package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.resources.MessageResources;

final class SyncNowVerb extends Verb {
   SyncNowVerb() {
      super(16987392, MessageResources.getBundle(), 165);
   }

   @Override
   public final Object invoke(Object parameter) {
      OTAMessageSync.getInstance().flushTransmitBuffers(true);
      return null;
   }
}
