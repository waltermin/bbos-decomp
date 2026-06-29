package net.rim.device.api.crypto;

import net.rim.device.api.crypto.certificate.Certificate;

public class CertificatePrivateKeyPair {
   private Certificate _certificate;
   private PrivateKey _privateKey;

   public CertificatePrivateKeyPair(Certificate certificate, PrivateKey key) {
      this._certificate = certificate;
      this._privateKey = key;
   }

   public Certificate getCertificate() {
      return this._certificate;
   }

   public PrivateKey getPrivateKey() {
      return this._privateKey;
   }
}
