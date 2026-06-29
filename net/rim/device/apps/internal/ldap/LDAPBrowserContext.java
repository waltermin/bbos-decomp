package net.rim.device.apps.internal.ldap;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateServerInfo;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.ldap.LDAPEntry;
import net.rim.device.api.ldap.LDAPQuery;
import net.rim.device.api.system.ApplicationDescriptor;

public interface LDAPBrowserContext {
   KeyStore getKeyStore();

   boolean isCertificateInKeyStore(Certificate var1);

   Certificate getCertificateFromKeyStore(byte[] var1);

   String getFriendlyName(LDAPEntry var1, Certificate var2);

   String getEmail(LDAPEntry var1);

   boolean isFetchRootApplicable();

   void addToKeyStore(LDAPEntry[] var1, Certificate[] var2);

   void addCrossCertificatesToKeyStore(LDAPEntry[] var1);

   boolean isCrossCertificateAvailable(LDAPEntry var1);

   void getCertificates(LDAPEntry var1, Certificate[] var2, byte[][][] var3);

   void getCertificateIDs(LDAPEntry var1, byte[][][] var2);

   void addFirstStageFilter(String[] var1, String[] var2, String[] var3, LDAPQuery var4);

   void addFirstStageAttributes(LDAPQuery var1);

   void addSecondStageFilter(LDAPEntry var1, LDAPQuery var2);

   void addSecondStageFilter(LDAPEntry[] var1, LDAPQuery var2);

   void addSecondStageAttributes(LDAPQuery var1);

   void addRootCertFilter(LDAPQuery var1);

   void addRootCertAttributes(LDAPQuery var1);

   long getObjectTypesConstant();

   ApplicationDescriptor getRibbonApplicationDescriptor();

   String getFetchingString();

   String getScreenTitle();

   String getEmptyString();

   String getFetchStringPlural();

   String getMenuFetchStatusString();

   String getMenuFetchRootString();

   String getMenuAddCertString();

   String getMenuAddCertStringPlural();

   String getMenuViewCertString();

   CertificateServerInfo getLastServer();

   void setLastServer(CertificateServerInfo var1);

   boolean isRootCertificatesLoadNeeded(Certificate[] var1);

   String getKeyStoreBrowserContextString();
}
