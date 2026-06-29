package net.rim.device.apps.internal.addressbook.userfields;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;

public final class PackageManager {
   private PackageManager() {
   }

   public static final void registerOnceOnSystemStart() {
      UserFieldsModelFactory factory = new UserFieldsModelFactory();
      ApplicationRegistry.getApplicationRegistry().put(-8069221209051907189L, factory);
      RIMModelFactoryRepository.addFactory(-5785746452676094833L, factory);
      RecognizerRepository.registerRecognizer(-8069221209051907189L, factory);
   }
}
