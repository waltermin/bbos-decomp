package net.rim.blackberry.api.options;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration;

public final class OptionsManager {
   private static Vector _optionsProviders = (Vector)(new Object());

   private OptionsManager() {
   }

   public static final void registerOptionsProvider(OptionsProvider optionsProvider) {
      if (!_optionsProviders.contains(optionsProvider)) {
         OptionsProviderRegistration.registerOptionsProvider(new OptionsManager$OptionsProviderWrapper(optionsProvider));
         _optionsProviders.addElement(optionsProvider);
      }
   }
}
