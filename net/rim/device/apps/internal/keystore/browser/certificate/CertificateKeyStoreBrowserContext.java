package net.rim.device.apps.internal.keystore.browser.certificate;

import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatusProviderFacade;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateAttachmentModelFactory;
import net.rim.device.apps.internal.keystore.browser.KeyStoreBrowserContext;
import net.rim.device.apps.internal.keystore.browser.KeyStoreBrowserData;
import net.rim.device.apps.internal.keystore.browser.KeyStoreBrowserResources;
import net.rim.device.apps.internal.keystore.browser.KeyStoreBrowserVerb;
import net.rim.vm.PersistentInteger;

public final class CertificateKeyStoreBrowserContext implements KeyStoreBrowserContext {
   private int certificateFilterId = PersistentInteger.getId(8223641991913099402L, 5);
   private KeyStoreBrowserVerb _fetchStatusChainVerb = (KeyStoreBrowserVerb)(new Object(5, 1200241));
   private static final long CERTIFICATE_FILTER_GUID = 8223641991913099402L;
   private static final int[] REVOCATION_REASONS = new int[]{
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      -805044213,
      775162112,
      774909491,
      3420721,
      -805044199,
      1699878656,
      1918985587,
      1226860643,
      1867325550,
      1852795252,
      1685343264,
      46,
      0,
      -804519934,
      -198770382,
      2891761,
      -555611058,
      1845123545,
      -804651007,
      51,
      -805044223,
      1761869955,
      134278175,
      556140544
   };

   @Override
   public final long[] getVerbRepositoryTypes() {
      return new long[]{
         12420023019045170L,
         7924745286593940558L,
         222533648385L,
         7567173840019914753L,
         2388605448593927199L,
         6730182323391971428L,
         513721691243836290L,
         7379475934967040000L,
         8318827328392233565L,
         461980519740016640L,
         8742756114294712899L,
         1974056613806564720L,
         7041521355055508035L,
         6730182323385925633L,
         6730182323385925762L,
         -8471949978704264830L
      };
   }

   @Override
   public final boolean getAllowHideCertificateTypes() {
      return true;
   }

   @Override
   public final String getKeyStoreBrowserName() {
      return KeyStoreBrowserResources.getString(6044);
   }

   @Override
   public final boolean isCertificateTypeSupported(Certificate certificate) {
      return certificate instanceof Object || certificate instanceof Object;
   }

   @Override
   public final int getCertificateFilter() {
      return PersistentInteger.get(this.certificateFilterId);
   }

   @Override
   public final void setCertificateFilter(int filter) {
      PersistentInteger.set(this.certificateFilterId, filter);
   }

   @Override
   public final CertificateAttachmentModelFactory getCertificateAttachmentModelFactory() {
      return new X509WTLSCertificateAttachmentModelFactory();
   }

   @Override
   public final String getPublicKeyContainerString(boolean startWithUpperCase, boolean plural) {
      return CryptoCommonResources.getCertificateContainerString(startWithUpperCase, plural);
   }

   @Override
   public final void addCurrentItemsVerb(VerbToMenu verbToMenu, KeyStoreBrowserData[] browserDatas) {
      if (browserDatas.length == 1
         && CertificateStatusProviderFacade.queryStatusAvailability(browserDatas[0].getBestCertificateChain(), true)
         && !browserDatas[0].isRoot()) {
         this._fetchStatusChainVerb.initialize(browserDatas[0], this);
         verbToMenu.addVerb(this._fetchStatusChainVerb);
      }
   }

   @Override
   public final KeyStore getKeyStore() {
      return DeviceKeyStore.getInstance();
   }

   @Override
   public final String getLaunchKeyStoreBrowserVerbDescription() {
      return KeyStoreBrowserResources.getString(6090);
   }

   @Override
   public final String getHelpContextString() {
      return "net_rim_bb_secureemail_help/smime_certificates";
   }

   @Override
   public final int[] getRelevantRevocationReasons() {
      return REVOCATION_REASONS;
   }

   @Override
   public final CryptoSystemProperties getCryptoSystemProperties() {
      return null;
   }
}
