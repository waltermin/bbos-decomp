package net.rim.device.api.crypto.tls;

import net.rim.device.api.crypto.Initialization;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.cldc.io.ssl.SSLOptionsRegistration;
import net.rim.device.cldc.io.ssl.TLSOptionStore;

public final class TLSInitialization implements Initialization {
   @Override
   public final void initialize() {
      SSLOptionsRegistration.register(true);
      TLSInitialization$TLSOptionsInitializer optionsInitializer = new TLSInitialization$TLSOptionsInitializer(null);
      OptionsProviderRegistration.registerOptionsProvider(optionsInitializer);
      TLSOptionStore.register();
      PersistentContent.addListener(new SessionResumptionPersistentContentListener());
   }
}
