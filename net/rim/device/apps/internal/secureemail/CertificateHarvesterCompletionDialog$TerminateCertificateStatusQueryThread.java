package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.certificate.status.CertificateStatusQuery;

class CertificateHarvesterCompletionDialog$TerminateCertificateStatusQueryThread extends Thread {
   CertificateStatusQuery _certificateStatusQuery;

   CertificateHarvesterCompletionDialog$TerminateCertificateStatusQueryThread(CertificateStatusQuery certificateStatusQuery) {
      this._certificateStatusQuery = certificateStatusQuery;
   }

   @Override
   public void run() {
      this._certificateStatusQuery.terminateQuery();
   }
}
