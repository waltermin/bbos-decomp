package net.rim.device.apps.internal.addressbook.eventmodel;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;

public final class PackageManager {
   private PackageManager() {
   }

   public static final void registerOnceOnSystemStart() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      EventModelFactory factory = new EventModelFactory(82);
      ar.put(-502242568902916599L, factory);
      RIMModelFactoryRepository.addFactory(-5785746452676094833L, factory);
      factory = new EventModelFactory(83);
      ar.put(1359937100302273559L, factory);
      RIMModelFactoryRepository.addFactory(-5785746452676094833L, factory);
   }
}
