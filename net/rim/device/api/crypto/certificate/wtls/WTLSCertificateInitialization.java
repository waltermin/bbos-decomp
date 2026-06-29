package net.rim.device.api.crypto.certificate.wtls;

import net.rim.device.api.crypto.Initialization;
import net.rim.device.api.crypto.certificate.CertificateFactory;

public final class WTLSCertificateInitialization implements Initialization {
   @Override
   public final void initialize() {
      CertificateFactory.register(new WTLSCertificateFactory());
   }
}
