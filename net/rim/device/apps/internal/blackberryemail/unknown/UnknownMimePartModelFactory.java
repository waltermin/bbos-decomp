package net.rim.device.apps.internal.blackberryemail.unknown;

import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.internal.blackberryemail.transmission.TransmissionWrapper;

final class UnknownMimePartModelFactory extends RIMModelFactory {
   @Override
   public final Object createInstance(Object context) {
      return new TransmissionWrapper(context);
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof TransmissionWrapper;
   }
}
