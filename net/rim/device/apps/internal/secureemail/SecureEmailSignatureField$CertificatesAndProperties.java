package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.crypto.certificate.Certificate;

public class SecureEmailSignatureField$CertificatesAndProperties {
   Certificate[] _certificateChain;
   long _certificateChainProperties;

   public SecureEmailSignatureField$CertificatesAndProperties(Certificate[] certificateChain, long certificateChainProperties) {
      this._certificateChain = certificateChain;
      this._certificateChainProperties = certificateChainProperties;
   }
}
