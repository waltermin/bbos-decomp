package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.transmission.rim.CMIMEConverterRegistry;

public final class PackageManager {
   private PackageManager() {
   }

   public static final void registerOnceOnSystemStart() {
      AddressCardCache.initialize();
      RIMModelFactory factory = new AddressCardModelFactory();
      RecognizerRepository.registerRecognizer(-3124646573404667739L, factory);
      VerbRepository.getVerbRepository(1666635727707141867L).register(new AddToAddressBookVerb(), 4738722199580714034L);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(-3124646573404667739L, factory);
      RIMModelFactoryRepository.addFactory(-7921492803965144520L, factory);
      factory = new PersonNameModelFactory();
      ar.put(5149066071290992769L, factory);
      RIMModelFactoryRepository.addFactory(-5785746452676094833L, factory);
      RecognizerRepository.registerRecognizer(5149066071290992769L, factory);
      AddressCardUtilities.initializeSalutationsTable();
      factory = new CompanyInfoModelFactory();
      ar.put(-2467076596918202204L, factory);
      RIMModelFactoryRepository.addFactory(-5785746452676094833L, factory);
      RecognizerRepository.registerRecognizer(-2467076596918202204L, factory);
      factory = new WebPageAddressModelFactory();
      ar.put(-2606680735022884905L, factory);
      RIMModelFactoryRepository.addFactory(-5785746452676094833L, factory);
      RecognizerRepository.registerRecognizer(-2606680735022884905L, factory);
      DisplayPictureModelFactory dpmFactory = new DisplayPictureModelFactory();
      ar.put(2940120466515154418L, dpmFactory);
      RIMModelFactoryRepository.addFactory(-5785746452676094833L, dpmFactory);
      RecognizerRepository.registerRecognizer(2940120466515154418L, dpmFactory);
      VerbRepository.getVerbRepository(-2843135760572915788L).register(new DisplayPictureVerb(4, 16864581), -753816125826020042L);
      VerbFactoryRepository.addFactory(-5325383713183591786L, dpmFactory);
      VerbFactoryRepository.addFactory(-5325383713183591786L, new AddCustomTuneVerb());
      CMIMEConverterRegistry.addConverter(new AddressCardConverter(), 3);
   }
}
