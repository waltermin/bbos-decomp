package net.rim.device.api.smartcard;

import net.rim.device.api.crypto.CryptoSmartCardUtilities;
import net.rim.device.api.crypto.Initialization;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.SystemListener;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.internal.proxy.Proxy;

public final class SmartCardInitialization implements Initialization, SystemListener {
   @Override
   public final void initialize() {
      SmartCardOptions.register();
      SmartCardInitialization$SmartCardOptionsInitializer optionsInitializer = new SmartCardInitialization$SmartCardOptionsInitializer(null);
      OptionsProviderRegistration.registerOptionsProvider(optionsInitializer);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(6437022998700452919L, new CachedPasswordManagerImpl());
      Proxy.getInstance().addSystemListener(this);
   }

   @Override
   public final void powerOff() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void powerUp() {
      if (CryptoSmartCardUtilities.isImportCertificatesAvailable()) {
         VerbRepository.getVerbRepository(-3067780115376710723L).register(new SmartCardImportVerb(), 7924745286593940558L);
      }

      Proxy.getInstance().removeSystemListener(this);
   }
}
