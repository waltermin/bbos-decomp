package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.certificate.Certificate;

public class RecipientData$CertificateDetails {
   private Certificate _certificate;
   private RecipientData$CertificateDetails[] _additionalCertificates;
   private String _certificateLabel;
   private String _emailAddress;
   private long _certificateChainProperties;
   private Certificate[] _certificateChain;

   public RecipientData$CertificateDetails(Certificate certificate, String certificateLabel, String emailAddress) {
      this(certificate, certificateLabel, 0, null, null, emailAddress);
   }

   public RecipientData$CertificateDetails(Certificate certificate, String certificateLabel, long certificateChainProperties) {
      this(certificate, certificateLabel, certificateChainProperties, null, null);
   }

   public RecipientData$CertificateDetails(Certificate certificate, String certificateLabel, long certificateChainProperties, Certificate[] certificateChain) {
      this(certificate, certificateLabel, certificateChainProperties, certificateChain, null);
   }

   public RecipientData$CertificateDetails(
      Certificate certificate,
      String certificateLabel,
      long certificateChainProperties,
      Certificate[] certificateChain,
      RecipientData$CertificateDetails[] additionalCertificates
   ) {
      this(certificate, certificateLabel, certificateChainProperties, certificateChain, additionalCertificates, null);
   }

   public RecipientData$CertificateDetails(
      Certificate certificate,
      String certificateLabel,
      long certificateChainProperties,
      Certificate[] certificateChain,
      RecipientData$CertificateDetails[] additionalCertificates,
      String emailAddress
   ) {
      this._certificate = certificate;
      this._certificateLabel = certificateLabel;
      this._certificateChainProperties = certificateChainProperties;
      this._certificateChain = certificateChain;
      this._additionalCertificates = additionalCertificates;
      this._emailAddress = emailAddress;
   }

   public Certificate getCertificate() {
      return this._certificate;
   }

   public void setCertificate(Certificate certificate) {
      this._certificate = certificate;
   }

   public String getCertificateLabel() {
      return this._certificateLabel;
   }

   public long getCertificateChainProperties() {
      return this._certificateChainProperties;
   }

   public void updateCertificateChainProperties(long newCertificateChainProperties) {
      this._certificateChainProperties = newCertificateChainProperties;
   }

   public void setCertificateChain(Certificate[] certificateChain) {
      this._certificateChain = certificateChain;
   }

   public Certificate[] getCertificateChain() {
      return this._certificateChain;
   }

   public RecipientData$CertificateDetails[] getAdditionalCertificates() {
      return this._additionalCertificates;
   }

   public void setAdditionalCertificates(RecipientData$CertificateDetails[] certificates) {
      this._additionalCertificates = certificates;
   }

   public String getEmailAddress() {
      return this._emailAddress;
   }

   public RecipientData$CertificateDetails clone() {
      return new RecipientData$CertificateDetails(
         this.getCertificate(),
         this.getCertificateLabel(),
         this.getCertificateChainProperties(),
         this.getCertificateChain(),
         this.getAdditionalCertificates(),
         this.getEmailAddress()
      );
   }
}
