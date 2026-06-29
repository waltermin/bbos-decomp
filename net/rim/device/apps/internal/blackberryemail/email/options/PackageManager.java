package net.rim.device.apps.internal.blackberryemail.email.options;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;

public final class PackageManager {
   private PackageManager() {
   }

   public static final void registerOnceOnSystemStart() {
      RIMModelFactory factory = new EmailImportanceOptionModelFactory();
      RIMModelFactoryRepository.addFactory(3735013535338552331L, factory);
      if (EmailEncodingHintsOptionModelFactory.isHanScriptInputLocaleAvailable()) {
         RIMModelFactoryRepository.addFactory(3735013535338552331L, new EmailEncodingHintsOptionModelFactory());
      }

      ApplicationRegistry.getApplicationRegistry().put(4868385214269919484L, factory);
   }
}
