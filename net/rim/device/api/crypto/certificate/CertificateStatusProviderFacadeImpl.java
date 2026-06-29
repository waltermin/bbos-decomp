package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.certificate.status.CertificateStatusListener;
import net.rim.device.api.crypto.certificate.status.CertificateStatusProvider;
import net.rim.device.api.crypto.certificate.status.CertificateStatusRequest;

final class CertificateStatusProviderFacadeImpl extends CertificateStatusProviderFacade {
   @Override
   final boolean queryStatusAvailability0() {
      return CertificateStatusProvider.queryStatusAvailability();
   }

   @Override
   final boolean queryStatusAvailability0(Certificate[] certChain, boolean checkEntireChain) {
      return CertificateStatusProvider.queryStatusAvailability(certChain, checkEntireChain);
   }

   @Override
   final int requestCertificateStatus0(CertificateStatusRequest request, CertificateStatusListener listener, boolean allowDismiss, boolean allowDetails) {
      return CertificateStatusProvider.requestCertificateStatus(request, listener, allowDismiss, allowDetails);
   }

   @Override
   final int fetchCertificateStatus0(CertificateStatusRequest request, CertificateStatusListener listener) {
      return CertificateStatusProvider.fetchCertificateStatus(request, listener);
   }
}
