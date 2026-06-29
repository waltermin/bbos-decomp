package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.framework.registration.VerbCombinerRepository;
import net.rim.device.apps.internal.phone.api.livecall.LiveCallFactoryRegistry;
import net.rim.device.apps.internal.phone.api.verbs.DialVerbCombiner;

public final class PackageManager {
   public static boolean _initialized;

   public static final void registerOnceOnSystemStart() {
      VerbCombinerRepository.addCombiner(-3336488203278641373L, new DialVerbCombiner());
      RIMPhone.registerAllLines();
      LiveCallFactoryRegistry.getRegistry().setDefaultFactory(new DefaultLiveCallFactory());
      PhoneAppScreen.initializeOnceOnSystemStart();
      ColourScreenActivePhoneUI.initializeOnceOnSystemStart();
   }
}
