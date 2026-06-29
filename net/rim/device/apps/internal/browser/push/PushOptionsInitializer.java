package net.rim.device.apps.internal.browser.push;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

final class PushOptionsInitializer implements OptionsProviderRegistration$OptionsProvider {
   @Override
   public final Vector getOptionsItems() {
      Vector items = new Vector();
      items.addElement(new PushOptionsItem());
      return items;
   }
}
