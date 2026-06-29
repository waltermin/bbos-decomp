package net.rim.device.apps.api.location;

import java.util.Enumeration;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.container.MainScreen;

public class LocationServicesOptionsProvider {
   private static final long KEY = 1019160237187605356L;

   public void populateMainScreen(MainScreen _1) {
      throw null;
   }

   public void activationStatus() {
      throw null;
   }

   public void save() {
      throw null;
   }

   public static void register(LocationServicesOptionsProvider provider) {
      getFactoryInstance().add(provider);
   }

   public static Enumeration getLocationServicesOptionsProviders() {
      return new LocationServicesOptionsProvider$ProviderEnumeration(null);
   }

   private static LocationServicesOptionsProvider$Factory getFactoryInstance() {
      LocationServicesOptionsProvider$Factory factory = (LocationServicesOptionsProvider$Factory)ApplicationRegistry.getApplicationRegistry()
         .getOrWaitFor(1019160237187605356L);
      if (factory == null) {
         factory = new LocationServicesOptionsProvider$Factory(null);
         ApplicationRegistry.getApplicationRegistry().put(1019160237187605356L, factory);
      }

      return factory;
   }
}
