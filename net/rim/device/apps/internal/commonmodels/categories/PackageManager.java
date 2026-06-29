package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;

public class PackageManager {
   private PackageManager() {
   }

   public static void registerOnceOnSystemStart() {
      CategoryModelFactory categoryModelFactory = new CategoryModelFactory();
      ApplicationRegistry.getApplicationRegistry().put(-3348482302610609156L, categoryModelFactory);
      RecognizerRepository.registerRecognizer(-3348482302610609156L, categoryModelFactory);
      CategoriesModelFactory categoriesModelFactory = new CategoriesModelFactory();
      ApplicationRegistry.getApplicationRegistry().put(-537018776823173138L, categoriesModelFactory);
      RecognizerRepository.registerRecognizer(-537018776823173138L, categoriesModelFactory);
      RIMModelFactoryRepository.addFactory(-5785746452676094833L, categoriesModelFactory);
      RIMModelFactoryRepository.addFactory(7798410905730545828L, categoriesModelFactory);
      RIMModelFactoryRepository.addFactory(8809206174646860213L, categoriesModelFactory);
      CategoryList categoryList = CategoryList.createAndRegisterInstance();
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.enableSynchronization(categoryList);
      }
   }
}
