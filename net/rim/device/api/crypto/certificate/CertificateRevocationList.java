package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.oid.OID;

public interface CertificateRevocationList {
   CertificateStatus getCertificateStatus(Certificate var1);

   CertificateStatus getCertificateStatus(Certificate var1, long var2);

   long getThisUpdate();

   long getNextUpdate();

   DistinguishedName getIssuer();

   CertificateExtension getExtension(OID var1);

   CertificateExtension[] getExtensions();

   CertificateExtension[] getExtensions(boolean var1);

   CertificateExtension getCRLEntryExtension(Certificate var1, OID var2);

   CertificateExtension[] getCRLEntryExtensions(Certificate var1);

   CertificateExtension[] getCRLEntryExtensions(Certificate var1, boolean var2);
}
