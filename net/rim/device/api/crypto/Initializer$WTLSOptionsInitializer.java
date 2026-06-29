package net.rim.device.api.crypto;

import java.util.Vector;
import net.rim.device.api.crypto.tls.wtls20.WTLSOptionsItem;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

final class Initializer$WTLSOptionsInitializer implements OptionsProviderRegistration$OptionsProvider {
   public final Object getOptionsItem(String itemID) {
      return null;
   }

   @Override
   public final Vector getOptionsItems() {
      Vector items = (Vector)(new Object());
      items.addElement(new WTLSOptionsItem());
      return items;
   }

   private Initializer$WTLSOptionsInitializer() {
   }

   Initializer$WTLSOptionsInitializer(Initializer$1 x0) {
      this();
   }
}
