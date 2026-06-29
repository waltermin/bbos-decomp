package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.internal.i18n.CommonResource;

public final class PhoneOptionsVerb extends Verb {
   public PhoneOptionsVerb() {
      this(CommonResource.getBundle(), 20);
   }

   public PhoneOptionsVerb(ResourceBundleFamily resourceBundle, int resourceId) {
      super(16982272, resourceBundle, resourceId);
   }

   @Override
   public final Object invoke(Object context) {
      PhoneOptions.editOptions(context);
      return null;
   }
}
