package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.util.Persistable;

public interface Certificate extends Persistable {
   long EMAIL_ADDRESSES;
   long PUBLIC_KEY_ALGORITHM_INFORMATION;
   long DISPLAY_ROOT;
   long DISPLAY_CA;
   long PROMPT_TO_TRUST_ON_IMPORT;
   long IS_END_ENTITY;
   long SUMMARY_TEXT;

   void verify(PublicKey var1);

   void verify(KeyStore var1);

   boolean isRoot();

   boolean isCA();

   void verify();

   void checkCertificateChain(int var1, Certificate[] var2);

   PublicKey getPublicKey();

   String getPublicKeyAlgorithm();

   String getSignatureAlgorithm();

   DistinguishedName getIssuer();

   DistinguishedName getSubject();

   CertificateStatus getStatus();

   void setStatus(CertificateStatus var1);

   boolean isValid();

   boolean isValid(long var1);

   long getNotBefore();

   long getNotAfter();

   CertificateExtension getExtension(OID var1);

   CertificateExtension[] getExtensions();

   CertificateExtension[] getExtensions(boolean var1);

   String getType();

   int getVersion();

   byte[] getSerialNumber();

   byte[] getEncoding();

   byte[] getEncoding(int var1);

   String getSubjectFriendlyName();

   int queryKeyUsage(long var1);

   CertificateDisplayField[] getCustomDisplayFields();

   Object getInformation(long var1, Object var3, Object var4);
}
