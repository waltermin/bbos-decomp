package net.rim.device.api.crypto.certificate.pgp;

import net.rim.device.api.crypto.Initialization;
import net.rim.device.api.crypto.certificate.CertificateChainFactory;
import net.rim.device.api.crypto.certificate.CertificateFactory;
import net.rim.device.api.crypto.certificate.CertificateInfoDialogFactory;
import net.rim.device.api.crypto.certificate.CertificateSummaryFieldFactory;

public final class PGPCertificateInitialization implements Initialization {
   @Override
   public final void initialize() {
      CertificateFactory.register(new PGPCertificateFactory());
      CertificateChainFactory.register(new PGPCertificateChainFactory());
      CertificateInfoDialogFactory.register(new PGPCertificateInfoDialogFactory());
      CertificateSummaryFieldFactory.register(new PGPCertificateSummaryFieldFactory());
   }
}
