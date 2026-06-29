package net.rim.device.apps.internal.addressbook.groupaddresscard;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;

public final class PackageManager {
   private PackageManager() {
   }

   public static final void registerOnceOnSystemStart() {
      GroupAddressCardModelFactory gacmFactory = new GroupAddressCardModelFactory();
      RecognizerRepository.registerRecognizer(-1326186686655625745L, gacmFactory);
      ApplicationRegistry.getApplicationRegistry().put(-1326186686655625745L, gacmFactory);
      RIMModelFactoryRepository.addFactory(-7921492803965144520L, gacmFactory);
   }
}
