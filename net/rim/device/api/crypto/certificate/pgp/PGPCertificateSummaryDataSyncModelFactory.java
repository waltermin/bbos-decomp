package net.rim.device.api.crypto.certificate.pgp;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateSummaryDataSyncModel;
import net.rim.device.api.crypto.certificate.CertificateSummaryDataSyncModelFactory;

public class PGPCertificateSummaryDataSyncModelFactory extends CertificateSummaryDataSyncModelFactory {
   @Override
   protected String getType() {
      return "PGP";
   }

   @Override
   protected CertificateSummaryDataSyncModel createCertificateSummaryDataSyncModelInternal(Certificate certificate, boolean isPrivateKeySet) {
      return new PGPCertificateSummaryDataSyncModel(certificate, isPrivateKeySet);
   }
}
