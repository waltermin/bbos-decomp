package net.rim.device.apps.internal.blackberryemail.body;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;

public final class PackageManager {
   private PackageManager() {
   }

   public static final void registerOnceOnSystemStart() {
      EmailBodyModelFactory bodyFactory = new EmailBodyModelFactory();
      RecognizerRepository.registerRecognizer(5987399499453925075L, bodyFactory);
      ApplicationRegistry.getApplicationRegistry().put(5987399499453925075L, bodyFactory);
      RIMModelFactoryRepository.addFactory(2497613418300956405L, bodyFactory);
      RIMModelFactoryRepository.addFactory(3893959701496671961L, bodyFactory);
   }
}
