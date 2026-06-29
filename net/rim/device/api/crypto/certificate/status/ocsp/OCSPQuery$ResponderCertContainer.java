package net.rim.device.api.crypto.certificate.status.ocsp;

import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;

class OCSPQuery$ResponderCertContainer {
   private byte[] _hash;
   private X509Certificate _cert;
   private byte[] _keyHash;
   private static final int SHA_LENGTH = 20;
   private static final int SHA_HALF_LENGTH = 10;
   private static final int SHA_QUARTER_LENGTH = 5;

   public OCSPQuery$ResponderCertContainer(X509Certificate cert) {
      this._cert = cert;
      SHA1Digest digest = (SHA1Digest)(new Object());
      this._hash = new byte[5];
      digest.update(cert.getEncoding());
      byte[] hashBuffer = digest.getDigest();
      int q1 = 0;
      int q2 = 5;
      int q3 = 10;

      for (int q4 = 15; q1 < 5; q4++) {
         this._hash[q1] = (byte)(hashBuffer[q1] ^ hashBuffer[q3] ^ hashBuffer[q2] ^ hashBuffer[q4]);
         q1++;
         q2++;
         q3++;
      }

      this._keyHash = new byte[20];
      digest.update(cert.getEncoding(1));
      digest.getDigest(this._keyHash, 0);
   }

   public byte[] getHash() {
      return this._hash;
   }

   public X509Certificate getCertificate() {
      return this._cert;
   }

   public byte[] getKeyHash() {
      return this._keyHash;
   }
}
