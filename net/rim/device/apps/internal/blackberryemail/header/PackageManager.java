package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;

public class PackageManager {
   private PackageManager() {
   }

   public static void registerOnceOnSystemStart() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      RIMModelFactory factory = new EmailHeaderModelFactory();
      RIMModelFactoryRepository.addFactory(2497613418300956405L, factory);
      RIMModelFactoryRepository.addFactory(3893959701496671961L, factory);
      ar.put(-8034039608019345282L, factory);
      factory = new SubjectModelFactory();
      RIMModelFactoryRepository.addFactory(2497613418300956405L, factory);
      RIMModelFactoryRepository.addFactory(3893959701496671961L, factory);
      ar.put(3928489455534245796L, factory);
      RIMModelFactory var3 = new TimeStampModelFactory();
      ar.put(569045234765698150L, var3);
      RecognizerRepository.registerRecognizer(-1249752217278100236L, new EmailHeaderModelRecognizer(3));
      RecognizerRepository.registerRecognizer(-3702691709233646541L, new EmailHeaderModelRecognizer(0));
      RecognizerRepository.registerRecognizer(3306251366082544277L, new EmailHeaderModelRecognizer(0, true));
      RecognizerRepository.registerRecognizer(
         4724113409898500292L,
         new EmailHeaderModelRecognizer(new int[]{0, 1, 2, -804651006, 0, -1, 51, 1866989824, 727916, -1569758719, 1661010020, 529165580})
      );
   }
}
