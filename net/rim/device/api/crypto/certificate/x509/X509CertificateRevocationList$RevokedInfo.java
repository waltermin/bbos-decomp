package net.rim.device.api.crypto.certificate.x509;

import java.util.Date;
import net.rim.device.api.crypto.certificate.CertificateExtension;

class X509CertificateRevocationList$RevokedInfo {
   Date revocationDate;
   int revocationReason = -1;
   CertificateExtension[] extensions;
}
