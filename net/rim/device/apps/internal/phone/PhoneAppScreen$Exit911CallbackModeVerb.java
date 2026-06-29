package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class PhoneAppScreen$Exit911CallbackModeVerb extends Verb {
   PhoneAppScreen$Exit911CallbackModeVerb() {
      super(131587);
   }

   @Override
   public final String toString() {
      return PhoneResources.getString(6275);
   }

   @Override
   public final Object invoke(Object parameter) {
      VoiceServices.exitEmergencyCallbackMode();
      return null;
   }
}
