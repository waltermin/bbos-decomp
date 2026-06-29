package net.rim.device.api.crypto.certificate.status.ocsp;

import net.rim.device.api.crypto.certificate.CertificateStatus;

final class OCSPResponse$OCSPCertificateInfo {
   public byte[] _issuerDNHash;
   public byte[] _issuerPKHash;
   public byte[] _serialNo;
   public CertificateStatus _status;

   public OCSPResponse$OCSPCertificateInfo(byte[] issuerDNHash, byte[] issuerPKHash, byte[] serialNo, CertificateStatus status) {
      this._issuerDNHash = issuerDNHash;
      this._issuerPKHash = issuerPKHash;
      this._serialNo = serialNo;
      this._status = status;
   }
}
