package net.rim.device.api.crypto.tls;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

final class TLSInitialization$TLSOptionsInitializer implements OptionsProviderRegistration$OptionsProvider {
   public final Object getOptionsItem(String itemID) {
      return null;
   }

   @Override
   public final Vector getOptionsItems() {
      Vector items = (Vector)(new Object());
      items.addElement(new TLSOptionsItem());
      return items;
   }

   private TLSInitialization$TLSOptionsInitializer() {
   }

   TLSInitialization$TLSOptionsInitializer(TLSInitialization$1 x0) {
      this();
   }
}
