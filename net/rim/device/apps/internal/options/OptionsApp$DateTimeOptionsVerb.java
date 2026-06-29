package net.rim.device.apps.internal.options;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.items.DateTimeOptionsItem;

final class OptionsApp$DateTimeOptionsVerb extends Verb {
   OptionsApp$DateTimeOptionsVerb() {
      super(16864384);
   }

   @Override
   public final String toString() {
      return net.rim.device.apps.internal.options.resources.OptionsResources.getString(503);
   }

   @Override
   public final Object invoke(Object context) {
      OptionsApp.openItem(new DateTimeOptionsItem(), null);
      return null;
   }
}
