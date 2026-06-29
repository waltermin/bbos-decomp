package net.rim.device.apps.internal.blackberryemail.address;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.registration.VerbCombinerRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;

public final class PackageManager {
   public static final void registerOnceOnSystemStart() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      AddressModelFactory factory = new AddressModelFactory(1);
      ar.put(-2985347935260258684L, factory);
      RIMModelFactoryRepository.addFactory(-5785746452676094833L, factory);
      RIMModelFactoryRepository.addFactory(-5785746452676094832L, factory);
      RecognizerRepository.registerRecognizer(-2985347935260258684L, factory);
      VerbRepository.getVerbRepository(8016149483483360697L).register(UseOnceAddressVerb.newUseOnceEmailAddressVerb(true), -2985347935260258684L);
      VerbCombinerRepository.addCombiner(6552349874078558230L, new ComposeEmailVerbCombiner());
      AddressModelFactory pinFactory = new AddressModelFactory(10);
      ar.put(4246852237058296601L, pinFactory);
      RIMModelFactoryRepository.addFactory(-5785746452676094833L, pinFactory);
      RIMModelFactoryRepository.addFactory(-5785746452676094832L, pinFactory);
      RecognizerRepository.registerRecognizer(4246852237058296601L, pinFactory);
      ApplicationRegistry.getApplicationRegistry().put(8245590029279666536L, new MailToMessageFactory());
      VerbCombinerRepository.addCombiner(-4100019750034398199L, new ComposePINVerbCombiner());
   }
}
