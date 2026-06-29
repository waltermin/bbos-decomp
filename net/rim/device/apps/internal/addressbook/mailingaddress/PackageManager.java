package net.rim.device.apps.internal.addressbook.mailingaddress;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;

public final class PackageManager {
   private PackageManager() {
   }

   public static final void registerOnceOnSystemStart() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      MailingAddressModelFactory factory = new MailingAddressModelFactory(0);
      ar.put(-7593463283570535867L, factory);
      RIMModelFactoryRepository.addFactory(-5785746452676094833L, factory);
      factory = new MailingAddressModelFactory(1);
      ar.put(2751926499133066620L, factory);
      RIMModelFactoryRepository.addFactory(-5785746452676094833L, factory);
   }
}
