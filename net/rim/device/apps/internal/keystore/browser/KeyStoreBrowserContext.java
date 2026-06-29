package net.rim.device.apps.internal.keystore.browser;

import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateAttachmentModelFactory;

public interface KeyStoreBrowserContext {
   int FILTER_MY_CERTS = 0;
   int FILTER_OTHERS_CERTS = 1;
   int FILTER_CA_CERTS = 2;
   int FILTER_ROOT_CERTS = 3;
   int FILTER_END_ENTITY_CERTS = 4;
   int FILTER_ALL_CERTS = 5;
   int RELOAD_KEYSTORE = 1;
   int RELOAD_CERTIFICATES = 2;
   int RELOAD_SCREEN = 3;

   long[] getVerbRepositoryTypes();

   boolean getAllowHideCertificateTypes();

   String getKeyStoreBrowserName();

   boolean isCertificateTypeSupported(Certificate var1);

   int getCertificateFilter();

   void setCertificateFilter(int var1);

   String getPublicKeyContainerString(boolean var1, boolean var2);

   void addCurrentItemsVerb(VerbToMenu var1, KeyStoreBrowserData[] var2);

   CertificateAttachmentModelFactory getCertificateAttachmentModelFactory();

   KeyStore getKeyStore();

   String getLaunchKeyStoreBrowserVerbDescription();

   String getHelpContextString();

   int[] getRelevantRevocationReasons();

   CryptoSystemProperties getCryptoSystemProperties();
}
