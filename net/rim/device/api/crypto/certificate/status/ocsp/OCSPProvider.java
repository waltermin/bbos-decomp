package net.rim.device.api.crypto.certificate.status.ocsp;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.status.CertificateStatusProvider;
import net.rim.device.api.crypto.certificate.status.ProviderRequestData;
import net.rim.device.api.crypto.certificate.status.ProviderResponseData;
import net.rim.device.api.crypto.certificate.status.ProviderUiContext;
import net.rim.device.api.crypto.keystore.KeyStore;

public final class OCSPProvider extends CertificateStatusProvider {
   public OCSPProvider() {
      super(-45497617038542455L);
   }

   @Override
   protected final boolean checkCompatibility(Certificate[] certChain, boolean checkEntireChain) {
      return OCSPQuery.isChainX509(certChain, checkEntireChain);
   }

   @Override
   protected final void encodeRequest(
      Certificate[] certChain, boolean checkEntireChain, ProviderRequestData request, KeyStore keyStore, ProviderUiContext uiContext
   ) {
      OCSPQuery query = new OCSPQuery(certChain, checkEntireChain, keyStore, uiContext);
      request.setContextObject(query);
      query.encodeRequest(request);
   }

   @Override
   protected final void decodeResponse(
      Certificate[] certChain, boolean checkEntireChain, ProviderResponseData response, KeyStore keyStore, ProviderUiContext uiContext
   ) {
      OCSPQuery query = (OCSPQuery)response.getContextObject();
      query.decodeResponse(response, uiContext);
   }
}
