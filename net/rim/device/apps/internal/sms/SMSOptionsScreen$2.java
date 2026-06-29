package net.rim.device.apps.internal.sms;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

class SMSOptionsScreen$2 implements OptionsProviderRegistration$OptionsProvider {
   @Override
   public Vector getOptionsItems() {
      Vector v = new Vector(1);
      v.addElement(new SMSOptionsScreen(null));
      return v;
   }
}
