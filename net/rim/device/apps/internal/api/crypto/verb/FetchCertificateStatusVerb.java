package net.rim.device.apps.internal.api.crypto.verb;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatusProviderFacade;
import net.rim.device.api.crypto.certificate.status.CertificateStatusRequest;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;

public final class FetchCertificateStatusVerb extends Verb {
   private Certificate[] _chain;
   private boolean _fetchChainStatus;
   private KeyStore _keyStore;

   public FetchCertificateStatusVerb(ResourceBundleFamily rb, int rbKey, boolean fetchChainStatus) {
      this(rb, rbKey, null, fetchChainStatus, null);
   }

   public FetchCertificateStatusVerb(ResourceBundleFamily rb, int rbKey, Certificate[] chain, boolean fetchChainStatus, KeyStore keyStore) {
      super(1200240, rb, rbKey);
      this._fetchChainStatus = fetchChainStatus;
      this.setCertificate(chain, keyStore);
   }

   public final void setCertificate(Certificate[] chain, KeyStore keyStore) {
      this._chain = chain;
      this._keyStore = keyStore;
   }

   @Override
   public final Object invoke(Object context) {
      if (this._chain != null && CertificateStatusProviderFacade.queryStatusAvailability()) {
         CertificateStatusRequest request = (CertificateStatusRequest)(new Object(this._chain, this._fetchChainStatus, this._keyStore, null, null));
         CertificateStatusProviderFacade.requestCertificateStatus(request, null, true, true);
         return null;
      } else {
         Dialog.alert(CryptoCommonResources.getString(2));
         return null;
      }
   }
}
