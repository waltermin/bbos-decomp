package net.rim.device.api.crypto.certificate;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

final class CertificateOptionsInitialization$CertificateServersOptionsInitializer implements OptionsProviderRegistration$OptionsProvider {
   @Override
   public final Vector getOptionsItems() {
      Vector items = new Vector(1);
      items.addElement(new CertificateServersOptionsItem());
      return items;
   }
}
