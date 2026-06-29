package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.PublicKey;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.util.Persistable;

public interface Certificate extends Persistable {
   long EMAIL_ADDRESSES = -7850001002262082664L;
   long PUBLIC_KEY_ALGORITHM_INFORMATION = -3174973482910568002L;
   long DISPLAY_ROOT = -334528756150594391L;
   long DISPLAY_CA = -2021910959928808912L;
   long PROMPT_TO_TRUST_ON_IMPORT = -1188891808812199856L;
   long IS_END_ENTITY = -7341435958452683242L;
   long SUMMARY_TEXT = -5753772986264564736L;

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
