package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.gps.GPSFirewall;

final class LocationServicesOptionsItem$ResetVerb extends Verb {
   public LocationServicesOptionsItem$ResetVerb() {
      super(16986368, OptionsResources.getResourceBundle(), 1945);
   }

   @Override
   public final Object invoke(Object parameter) {
      GPSFirewall.getInstance().reset();
      return null;
   }
}
