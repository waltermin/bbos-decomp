package net.rim.device.api.crypto.certificate.status;

import java.util.Enumeration;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatus;

public interface ProviderResponseData {
   byte[] getGlobalField(int var1);

   Enumeration getCertificates();

   byte[] getCertField(Certificate var1, int var2);

   Object getContextObject();

   void setCertificateStatus(Certificate var1, CertificateStatus var2);
}
