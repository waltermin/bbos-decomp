package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.framework.registration.VerbCombinerRepository;
import net.rim.device.apps.api.framework.verb.VerbCombiner;
import net.rim.device.apps.internal.phone.api.livecall.LiveCallFactoryRegistry;

public final class PackageManager {
   public static boolean _initialized;

   public static final void registerOnceOnSystemStart() {
      VerbCombinerRepository.addCombiner(-3336488203278641373L, (VerbCombiner)(new Object()));
      RIMPhone.registerAllLines();
      LiveCallFactoryRegistry.getRegistry().setDefaultFactory(new DefaultLiveCallFactory());
      PhoneAppScreen.initializeOnceOnSystemStart();
      ColourScreenActivePhoneUI.initializeOnceOnSystemStart();
   }
}
