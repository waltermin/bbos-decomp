package net.rim.device.apps.internal.commonmodels.body;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;

public class PackageManager {
   private PackageManager() {
   }

   public static void registerOnceOnSystemStart() {
      BodyModelFactory bodyFactory = new BodyModelFactory();
      RecognizerRepository.registerRecognizer(2096811533660483L, bodyFactory);
      ApplicationRegistry.getApplicationRegistry().put(2096811533660483L, bodyFactory);
      RIMModelFactoryRepository.addFactory(-5785746452676094833L, bodyFactory);
      RIMModelFactoryRepository.addFactory(8809206174646860213L, bodyFactory);
      RIMModelFactoryRepository.addFactory(7798410905730545828L, bodyFactory);
      RIMModelFactoryRepository.addFactory(-5829986326706945081L, bodyFactory);
   }
}
