package net.rim.device.api.crypto.certificate.status;

import net.rim.device.api.crypto.Initialization;
import net.rim.device.api.crypto.certificate.status.crl.CRLProvider;
import net.rim.device.api.crypto.certificate.status.ocsp.OCSPProvider;

public final class StatusProviderInitialization implements Initialization {
   @Override
   public final void initialize() {
      CertificateStatusProvider.register(new OCSPProvider());
      CertificateStatusProvider.register(new CRLProvider());
   }
}
