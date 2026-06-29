package net.rim.device.api.crypto.certificate.x509;

import net.rim.device.api.crypto.Initialization;
import net.rim.device.api.crypto.certificate.CertificateFactory;

public final class X509CertificateInitialization implements Initialization {
   @Override
   public final void initialize() {
      CertificateFactory.register(new X509CertificateFactory());
   }
}
