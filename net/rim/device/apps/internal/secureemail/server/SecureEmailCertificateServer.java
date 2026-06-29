package net.rim.device.apps.internal.secureemail.server;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.servicebook.ServiceRecord;

public interface SecureEmailCertificateServer {
   boolean providesCertificatesForService(ServiceRecord var1);

   long[] getEncodingUIDs();

   Certificate getCertificateByEmailAddress(String var1, int var2, SecureEmailServerOperationListener var3);

   Certificate getCertificateByCertificateID(Object var1, SecureEmailServerOperationListener var2);

   Certificate getCertificateByCertificateID(Object var1, String var2, SecureEmailServerOperationListener var3);

   Long getCertificateProperties(Certificate var1, long var2);

   String getServerName();

   boolean isInitialized();
}
