package net.rim.device.apps.internal.sms;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

class SMSOptionsScreen$1 implements OptionsProviderRegistration$OptionsProvider {
   @Override
   public Vector getOptionsItems() {
      Vector v = (Vector)(new Object(1));
      v.addElement(new SMSOptionsScreen(null));
      return v;
   }
}
