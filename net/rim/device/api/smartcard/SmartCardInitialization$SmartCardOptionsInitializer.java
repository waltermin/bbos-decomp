package net.rim.device.api.smartcard;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

final class SmartCardInitialization$SmartCardOptionsInitializer implements OptionsProviderRegistration$OptionsProvider {
   public final Object getOptionsItem(String itemID) {
      return null;
   }

   @Override
   public final Vector getOptionsItems() {
      if (SmartCardReaderFactory.getNumSmartCardReaders() > 0) {
         Vector items = (Vector)(new Object());
         items.addElement(new SmartCardOptionsItem());
         return items;
      } else {
         return null;
      }
   }

   private SmartCardInitialization$SmartCardOptionsInitializer() {
   }

   SmartCardInitialization$SmartCardOptionsInitializer(SmartCardInitialization$1 x0) {
      this();
   }
}
