package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.options.PhoneInfoOption;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class PhoneAppScreen$PhoneInfoVerb extends Verb {
   public PhoneAppScreen$PhoneInfoVerb() {
      super(16982273, PhoneResources.getResourceBundle(), 451);
   }

   @Override
   public final Object invoke(Object context) {
      new PhoneInfoOption().perform(6099736323056465049L, null);
      return null;
   }
}
