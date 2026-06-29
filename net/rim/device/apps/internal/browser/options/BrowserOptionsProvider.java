package net.rim.device.apps.internal.browser.options;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

public final class BrowserOptionsProvider implements OptionsProviderRegistration$OptionsProvider {
   @Override
   public final Vector getOptionsItems() {
      Vector items = new Vector();
      items.addElement(new BrowserOptionsProvider$BrowserOptionsItem());
      return items;
   }
}
