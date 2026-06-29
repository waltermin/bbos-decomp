package net.rim.device.apps.internal.lbs;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

final class MapsOptionsScreen$MapsOptionsProvider implements OptionsProviderRegistration$OptionsProvider {
   @Override
   public final Vector getOptionsItems() {
      Vector v = new Vector();
      v.addElement(new MapsOptionsScreen());
      return v;
   }
}
