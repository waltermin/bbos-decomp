package net.rim.device.apps.internal.api.crypto.verb;

import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.CertificateStatusManagerTicket;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.apps.api.framework.verb.Verb;

public final class DisplayCertificateVerb extends Verb {
   private String _description;
   private Certificate _certificate;
   private Certificate[] _certificatePool;
   private KeyStore _keyStore;
   private CryptoSystemProperties _cryptoSystemProperties;
   private CertificateStatusManagerTicket _certificateStatusManagerTicket;

   public DisplayCertificateVerb(String description) {
      this(description, null, null);
   }

   public DisplayCertificateVerb(String description, Certificate certificate, KeyStore keyStore) {
      this(description, certificate, null, keyStore);
   }

   public DisplayCertificateVerb(String description, Certificate certificate, Certificate[] certificatePool, KeyStore keyStore) {
      this(description, certificate, certificatePool, keyStore, null);
   }

   public DisplayCertificateVerb(
      String description,
      Certificate certificate,
      Certificate[] certificatePool,
      KeyStore keyStore,
      CertificateStatusManagerTicket certificateStatusManagerTicket
   ) {
      this(description, certificate, certificatePool, keyStore, null, certificateStatusManagerTicket);
   }

   public DisplayCertificateVerb(
      String description,
      Certificate certificate,
      Certificate[] certificatePool,
      KeyStore keyStore,
      CryptoSystemProperties cryptoSystemProperties,
      CertificateStatusManagerTicket certificateStatusManagerTicket
   ) {
      super(1200208);
      this._description = description;
      this.setCertificate(certificate, certificatePool, keyStore, cryptoSystemProperties, certificateStatusManagerTicket);
   }

   public final void setCertificate(Certificate certificate, KeyStore keyStore) {
      this.setCertificate(certificate, null, keyStore);
   }

   public final void setCertificate(Certificate certificate, Certificate[] certificatePool, KeyStore keyStore) {
      this.setCertificate(certificate, certificatePool, keyStore, null);
   }

   public final void setCertificate(
      Certificate certificate, Certificate[] certificatePool, KeyStore keyStore, CertificateStatusManagerTicket certificateStatusManagerTicket
   ) {
      this.setCertificate(certificate, certificatePool, keyStore, null, certificateStatusManagerTicket);
   }

   public final void setCertificate(
      Certificate certificate,
      Certificate[] certificatePool,
      KeyStore keyStore,
      CryptoSystemProperties cryptoSystemProperties,
      CertificateStatusManagerTicket certificateStatusManagerTicket
   ) {
      this._certificate = certificate;
      this._certificatePool = certificatePool;
      this._keyStore = keyStore;
      this._cryptoSystemProperties = cryptoSystemProperties;
      this._certificateStatusManagerTicket = certificateStatusManagerTicket;
   }

   @Override
   public final String toString() {
      return this._description;
   }

   @Override
   public final Object invoke(Object context) {
      CertificateUtilities.displayCertificateDetails(
         this._certificate, this._certificatePool, this._keyStore, this._cryptoSystemProperties, true, this._certificateStatusManagerTicket
      );
      return null;
   }
}
