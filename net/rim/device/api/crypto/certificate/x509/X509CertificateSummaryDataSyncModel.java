package net.rim.device.api.crypto.certificate.x509;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateSummaryDataSyncModel;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;

class X509CertificateSummaryDataSyncModel extends CertificateSummaryDataSyncModel implements Persistable {
   private byte[] _issuer;
   private byte[] _serialNumber;
   private byte[] _subjectKeyIdentifier;
   private byte[] _flags;

   public X509CertificateSummaryDataSyncModel(Certificate certificate, boolean isPrivateKeySet) {
      super(certificate, isPrivateKeySet);
      X509Certificate x509Certificate = (X509Certificate)certificate;
      if (isPrivateKeySet) {
         DistinguishedName issuerName = x509Certificate.getIssuer();
         if (issuerName != null) {
            this._issuer = issuerName.getEncoding();
         }

         this._serialNumber = x509Certificate.getSerialNumber();
         this._subjectKeyIdentifier = x509Certificate.getSubjectKeyIdentifier();
      }
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (!super.convert(context, target)) {
         return false;
      }

      if (ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         if (this._issuer != null) {
            syncBuffer.addBytes(3, this._issuer);
         }

         if (this._serialNumber != null) {
            syncBuffer.addBytes(4, this._serialNumber);
         }

         if (this._subjectKeyIdentifier != null) {
            syncBuffer.addBytes(5, this._subjectKeyIdentifier);
         }

         return true;
      } else {
         return false;
      }
   }
}
