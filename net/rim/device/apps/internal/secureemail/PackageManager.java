package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.transmission.rim.CMIMEConverterRegistry;
import net.rim.device.apps.internal.blackberryemail.sendmethods.SendMethodSelector;
import net.rim.device.apps.internal.messaging.search.MessageSearchable;
import net.rim.device.apps.internal.messaging.search.criteria.BodySearchModelFactory;
import net.rim.device.apps.internal.secureemail.encodings.SecureEmailEncodingManager;
import net.rim.device.apps.internal.secureemail.sendmethods.SecureEmailPlainTextSendMethodFactory;

class PackageManager {
   public static void libMain(String[] args) {
      SendMethodSelector.getInstance().registerSendMethodFactory(new SecureEmailPlainTextSendMethodFactory());
      SecureEmailEncodingManager.getInstance().register(182808770805039415L, 300);
      RIMModelFactory factory = new SecureEmailMailboxOptionsModelFactory();
      RIMModelFactoryRepository.addFactory(3735013535338552331L, factory);
      SecureEmailListener secureEmailListener = SecureEmailListener.getInstance();
      ModelViewListenerRegistry.addModelViewListener(-6822293833372928884L, secureEmailListener);
      MemoryCleanerDaemon.addListener(secureEmailListener, false);
      CertificateServersConverter converter = new CertificateServersConverter();
      CMIMEConverterRegistry.addConverter(converter, 4);
      VerbRepository verbRepository = VerbRepository.getVerbRepository(1363053212162519223L);
      verbRepository.register(new SendCertificateServerVerb(true), 4738722199580714034L);
      verbRepository.register(new SendCertificateServerVerb(false), 4738722199580714034L);
      RIMModelFactory encodingActionSearchModelFactory = new EncodingActionSearchModelFactory();
      BodySearchModelFactory.getInstance().registerSubCriterionFactory(encodingActionSearchModelFactory);
      MessageSearchable.getInstance().addSubItem(new EncodingActionMessageSearchableSubItem());
   }
}
