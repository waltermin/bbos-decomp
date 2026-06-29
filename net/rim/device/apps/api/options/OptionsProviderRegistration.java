package net.rim.device.apps.api.options;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;

public class OptionsProviderRegistration {
   private static Vector _optionsProviders;
   private static final long KEY_NAME = -4040876185295916320L;

   public static void registerOptionsProvider(OptionsProviderRegistration$OptionsProvider optionsProvider) {
      synchronized (_optionsProviders) {
         if (!_optionsProviders.contains(optionsProvider)) {
            _optionsProviders.addElement(optionsProvider);
         }
      }
   }

   public static void deRegisterOptionsProvider(OptionsProviderRegistration$OptionsProvider optionsProvider) {
      synchronized (_optionsProviders) {
         if (_optionsProviders.contains(optionsProvider)) {
            _optionsProviders.removeElement(optionsProvider);
         }
      }
   }

   public static Vector getOptionsProviders() {
      Vector optionsProviders = new Vector();
      synchronized (_optionsProviders) {
         int numProviders = _optionsProviders.size();

         for (int i = 0; i < numProviders; i++) {
            optionsProviders.addElement(_optionsProviders.elementAt(i));
         }

         return optionsProviders;
      }
   }

   static {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      _optionsProviders = (Vector)registry.get(-4040876185295916320L);
      if (_optionsProviders == null) {
         synchronized (registry) {
            _optionsProviders = (Vector)registry.get(-4040876185295916320L);
            if (_optionsProviders == null) {
               _optionsProviders = new Vector();
               registry.put(-4040876185295916320L, _optionsProviders);
            }
         }
      }
   }
}
