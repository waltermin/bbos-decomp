package net.rim.device.apps.internal.keystore.browser;

import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateAttachmentModelFactory;

public interface KeyStoreBrowserContext {
   int FILTER_MY_CERTS;
   int FILTER_OTHERS_CERTS;
   int FILTER_CA_CERTS;
   int FILTER_ROOT_CERTS;
   int FILTER_END_ENTITY_CERTS;
   int FILTER_ALL_CERTS;
   int RELOAD_KEYSTORE;
   int RELOAD_CERTIFICATES;
   int RELOAD_SCREEN;

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
