package net.rim.device.apps.internal.commonmodels.title;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;

public class PackageManager {
   private PackageManager() {
   }

   public static void registerOnceOnSystemStart() {
      TitleModelFactory factory = TitleModelFactory.getInstance();
      RecognizerRepository.registerRecognizer(-4904857078378172834L, factory);
      RIMModelFactoryRepository.addFactory(7798410905730545828L, factory);
      RIMModelFactoryRepository.addFactory(8809206174646860213L, factory);
      RIMModelFactoryRepository.addFactory(-5785746452676094833L, factory);
      ApplicationRegistry.getApplicationRegistry().put(-4904857078378172834L, factory);
   }
}
