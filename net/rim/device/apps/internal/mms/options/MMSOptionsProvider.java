package net.rim.device.apps.internal.mms.options;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

class MMSOptionsProvider implements OptionsProviderRegistration$OptionsProvider {
   static void register() {
      OptionsProviderRegistration.registerOptionsProvider(new MMSOptionsProvider());
   }

   @Override
   public Vector getOptionsItems() {
      Vector v = new Vector(1);
      v.addElement(new MMSOptionsScreen());
      return v;
   }
}
