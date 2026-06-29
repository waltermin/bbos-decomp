package net.rim.device.api.crypto.certificate.status;

import net.rim.device.api.crypto.certificate.Certificate;

public interface ProviderRequestData {
   void addGlobalField(int var1, byte[] var2);

   void addCertField(Certificate var1, int var2, byte[] var3);

   void setContextObject(Object var1);
}
