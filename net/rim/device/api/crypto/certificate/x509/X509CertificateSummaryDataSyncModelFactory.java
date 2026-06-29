package net.rim.device.api.crypto.certificate.x509;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateSummaryDataSyncModel;
import net.rim.device.api.crypto.certificate.CertificateSummaryDataSyncModelFactory;

public class X509CertificateSummaryDataSyncModelFactory extends CertificateSummaryDataSyncModelFactory {
   @Override
   protected String getType() {
      return "X509";
   }

   @Override
   protected CertificateSummaryDataSyncModel createCertificateSummaryDataSyncModelInternal(Certificate certificate, boolean isPrivateKeySet) {
      return new X509CertificateSummaryDataSyncModel(certificate, isPrivateKeySet);
   }
}
