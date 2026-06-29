package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.ApplicationControlInformation;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class FirewallOptionsItem$DefaultPermissionsVerb extends Verb {
   public FirewallOptionsItem$DefaultPermissionsVerb() {
      super(16986368, OptionsResources.getResourceBundle(), 1960);
   }

   @Override
   public final Object invoke(Object parameter) {
      ApplicationControlInformation aci = (ApplicationControlInformation)(new Object(0));
      aci.open();
      return null;
   }
}
