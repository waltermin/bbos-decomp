package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;

public final class PackageManager {
   private PackageManager() {
   }

   public static final void registerOnceOnSystemStart() {
      VerbRepository.getVerbRepository(-1789952090272871921L).register(new AddressSelectionVerb(), -8839945759096901113L);
      VerbRepository.getVerbRepository(-1789952090272871921L).register(new AddressSelectionVerb(), 4738722199580714034L);
      RIMModelFactory addressAttachmentFactory = new AddressAttachmentFactory();
      RIMModelFactoryRepository.addFactory(2497613418300956405L, addressAttachmentFactory);
      RIMModelFactoryRepository.addFactory(3893959701496671961L, addressAttachmentFactory);
   }
}
