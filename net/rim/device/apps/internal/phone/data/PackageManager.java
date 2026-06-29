package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.quickcontact.QuickContactItemRegistrationFactory;
import net.rim.device.apps.internal.phone.control.FDNPolicyHandler;
import net.rim.device.apps.internal.phone.control.NetworkTweakHandler;

public class PackageManager {
   public static void registerOnceOnSystemStart() {
      PhoneFolders.initializePhoneFolderHierarchy();
      RIMModelFactory callerIDInfoFactory = new CallerIDInfoFactory();
      RIMModelFactoryRepository.addFactory(-3466239368616563929L, callerIDInfoFactory);
      RIMModelFactoryRepository.addFactory(-5829986326706945081L, callerIDInfoFactory);
      PhoneCallModelFactory phoneCallModelFactory = new PhoneCallModelFactory();
      RIMModelFactoryRepository.addFactory(-5829986326706945081L, phoneCallModelFactory);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      if (ar.getOrWaitFor(4846413703361859244L) == null) {
         ar.put(4846413703361859244L, phoneCallModelFactory);
         RecognizerRepository.registerRecognizer(4846413703361859244L, phoneCallModelFactory);
      }

      if (ar.getOrWaitFor(2629643229137268956L) == null) {
         ar.put(2629643229137268956L, callerIDInfoFactory);
      }

      if (ar.getOrWaitFor(4046126975918546978L) == null) {
         RIMModelFactory speedDialFactory = new SpeedDialItemFactory();
         ar.put(4046126975918546978L, speedDialFactory);
         QuickContactItemRegistrationFactory.registerQuickContactItemFactory(4046126975918546978L, speedDialFactory);
         SpeedDialVerbFactory speedDialVerbFactory = new SpeedDialVerbFactory();
         VerbFactoryRepository.addFactory(-5922809018445304484L, speedDialVerbFactory);
         VerbFactoryRepository.addFactory(-6650104226833963074L, speedDialVerbFactory);
      }

      PhoneCallCollection.getInstance();
      RIMModelFactoryRepository.addFactory(-3466239368616563929L, new HotlistItemFactory());
      Hotlist.enableSync();
      NetworkTweakHandler.registerOnceOnSystemStart();
      FDNPolicyHandler.registerOnceOnSystemStart();
   }
}
