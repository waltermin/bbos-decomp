package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.Initialization;
import net.rim.device.apps.api.options.OptionsProviderRegistration;

public final class CertificateOptionsInitialization implements Initialization {
   @Override
   public final void initialize() {
      CertificateServers.getInstance();
      CertificateOptionsInitialization$CertificateServersOptionsInitializer app = new CertificateOptionsInitialization$CertificateServersOptionsInitializer();
      OptionsProviderRegistration.registerOptionsProvider(app);
   }
}
