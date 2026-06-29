package net.rim.device.api.crypto.keystore;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

final class KeyStoreInitialization1$KeyStoreOptionsItemInitializer implements OptionsProviderRegistration$OptionsProvider {
   @Override
   public final Vector getOptionsItems() {
      Vector items = (Vector)(new Object(1));
      items.addElement(new KeyStoreOptionsItem());
      return items;
   }
}
