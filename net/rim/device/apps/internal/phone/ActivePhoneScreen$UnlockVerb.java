package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class ActivePhoneScreen$UnlockVerb extends Verb {
   ActivePhoneScreen$UnlockVerb() {
      super(268501008);
   }

   @Override
   public final String toString() {
      return PhoneResources.getString(6086);
   }

   @Override
   public final Object invoke(Object parameter) {
      RIMGlobalMessagePoster.postGlobalEvent(1981938861510850567L);
      return null;
   }
}
