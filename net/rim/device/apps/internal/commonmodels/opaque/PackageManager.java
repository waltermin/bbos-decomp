package net.rim.device.apps.internal.commonmodels.opaque;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;

public class PackageManager {
   private PackageManager() {
   }

   public static void registerOnceOnSystemStart() {
      RIMModelFactory factory = new OpaqueDataModelFactory();
      RecognizerRepository.registerRecognizer(-8058545440370075039L, factory);
      ApplicationRegistry.getApplicationRegistry().put(-8058545440370075039L, factory);
   }
}
