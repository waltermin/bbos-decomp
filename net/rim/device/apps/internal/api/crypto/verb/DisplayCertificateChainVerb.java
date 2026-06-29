package net.rim.device.apps.internal.api.crypto.verb;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;

public final class DisplayCertificateChainVerb extends Verb {
   private String _title;
   private Certificate _certificate;
   private KeyStore _keyStore;

   public DisplayCertificateChainVerb(ResourceBundleFamily rb, int rbKey) {
      this(rb, rbKey, null, null, null);
   }

   public DisplayCertificateChainVerb(ResourceBundleFamily rb, int rbKey, String title, Certificate certificate, KeyStore keyStore) {
      super(1200209, rb, rbKey);
      this.setCertificate(title, certificate, keyStore);
   }

   public final void setCertificate(String title, Certificate certificate, KeyStore keyStore) {
      this._title = title;
      this._certificate = certificate;
      this._keyStore = keyStore;
   }

   @Override
   public final Object invoke(Object parameter) {
      CertificateUtilities.displayCertificateChainDetails(this._title, this._certificate, this._keyStore);
      return null;
   }
}
