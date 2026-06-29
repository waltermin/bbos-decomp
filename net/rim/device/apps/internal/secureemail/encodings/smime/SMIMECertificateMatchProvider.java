package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.crypto.certificate.x509.X509Certificate;
import net.rim.device.api.crypto.cms.CMSEntityIdentifier;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.MatchProvider;

public final class SMIMECertificateMatchProvider implements MatchProvider {
   CMSEntityIdentifier[] _ids;
   byte[] _certificateHash;

   public SMIMECertificateMatchProvider(CMSEntityIdentifier[] ids) {
      this._ids = ids;
   }

   public SMIMECertificateMatchProvider(byte[] certificateHash) {
      this._certificateHash = certificateHash;
   }

   @Override
   public final int match(Object searchCriteria) {
      if (searchCriteria instanceof Object) {
         X509Certificate cert = (X509Certificate)searchCriteria;
         if (this._ids != null) {
            return this.matchByCMSEntityIdentifier(cert);
         }

         if (this._certificateHash != null) {
            return this.matchByCertificateHash(cert);
         }
      }

      return 0;
   }

   private final int matchByCMSEntityIdentifier(X509Certificate cert) {
      byte[] serialNumber = cert.getSerialNumber();
      DistinguishedName dn = cert.getIssuer();
      byte[] subjectKeyID = cert.getSubjectKeyIdentifier();

      for (int i = 0; i < this._ids.length; i++) {
         byte[] compareSerialNumber = this._ids[i].getSerialNumber();
         if (compareSerialNumber != null) {
            if (Arrays.equals(serialNumber, compareSerialNumber) && dn.equals(this._ids[i].getIssuer())) {
               return 1;
            }
         } else {
            byte[] compareSubjectKeyID = this._ids[i].getSubjectKeyIdentifier();
            if (subjectKeyID != null && compareSubjectKeyID != null && Arrays.equals(subjectKeyID, compareSubjectKeyID)) {
               return 1;
            }
         }
      }

      return 0;
   }

   private final int matchByCertificateHash(X509Certificate cert) {
      SHA1Digest digest = (SHA1Digest)(new Object());
      digest.update(cert.getEncoding());
      return Arrays.equals(digest.getDigest(), this._certificateHash) ? 1 : 0;
   }
}
