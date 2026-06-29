package net.rim.device.api.crypto.certificate.status;

import java.util.Enumeration;

final class CertificateStatusProvider$NonUIQueryListener implements QueryProgressListener {
   @Override
   public final void updateProgress(CertificateStatusQuery query, int progress) {
      if (progress == 6 || progress == 7) {
         CertificateStatusRequest request = query.getRequest();
         synchronized (CertificateStatusProvider._queriesInProgress) {
            Enumeration matchingQueries = CertificateStatusProvider._queriesInProgress.elements(request);

            while (matchingQueries.hasMoreElements()) {
               CertificateStatusQuery matchingQuery = (CertificateStatusQuery)matchingQueries.nextElement();
               if (matchingQuery != query) {
                  matchingQuery.copyResponseAndNotifyListener(query);
               }
            }

            CertificateStatusProvider._queriesInProgress.removeKey(request);
         }
      }
   }
}
