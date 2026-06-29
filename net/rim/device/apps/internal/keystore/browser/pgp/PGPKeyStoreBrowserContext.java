package net.rim.device.apps.internal.keystore.browser.pgp;

import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatusProviderFacade;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.PGPKeyStore;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateAttachmentModelFactory;
import net.rim.device.apps.internal.keystore.browser.KeyStoreBrowserContext;
import net.rim.device.apps.internal.keystore.browser.KeyStoreBrowserData;
import net.rim.device.apps.internal.keystore.browser.KeyStoreBrowserResources;
import net.rim.device.apps.internal.keystore.browser.KeyStoreBrowserVerb;

public final class PGPKeyStoreBrowserContext implements KeyStoreBrowserContext {
   private KeyStoreBrowserVerb _fetchCertUpdateVerb = (KeyStoreBrowserVerb)(new Object(6, 1200242));
   private CryptoSystemProperties _pgpCryptoSystemProperties;
   private static final int[] REVOCATION_REASONS = new int[]{
      0,
      4,
      1,
      8,
      9,
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
      -805044223,
      2,
      -804651007
   };

   public PGPKeyStoreBrowserContext(CryptoSystemProperties pgpCryptoSystemProperties) {
      this._pgpCryptoSystemProperties = pgpCryptoSystemProperties;
   }

   @Override
   public final long[] getVerbRepositoryTypes() {
      return new long[]{
         -3216013493425785397L,
         2884556575470060544L,
         -3043235945579601637L,
         6730182323385821512L,
         576587631819314306L,
         6730182323387196738L,
         3045286569148790914L,
         3045286571312768617L
      };
   }

   @Override
   public final boolean getAllowHideCertificateTypes() {
      return false;
   }

   @Override
   public final String getKeyStoreBrowserName() {
      return KeyStoreBrowserResources.getString(6046);
   }

   @Override
   public final boolean isCertificateTypeSupported(Certificate certificate) {
      return certificate instanceof Object;
   }

   @Override
   public final int getCertificateFilter() {
      return 5;
   }

   @Override
   public final void setCertificateFilter(int filter) {
   }

   @Override
   public final CertificateAttachmentModelFactory getCertificateAttachmentModelFactory() {
      return new PGPKeyAttachmentModelFactory();
   }

   @Override
   public final String getPublicKeyContainerString(boolean startWithUpperCase, boolean plural) {
      return CryptoCommonResources.getPGPContainerString(startWithUpperCase, plural);
   }

   @Override
   public final void addCurrentItemsVerb(VerbToMenu verbToMenu, KeyStoreBrowserData[] browserDatas) {
      if (browserDatas.length == 1 && CertificateStatusProviderFacade.queryStatusAvailability(browserDatas[0].getBestCertificateChain(), true)) {
         this._fetchCertUpdateVerb.initialize(browserDatas[0], this);
         verbToMenu.addVerb(this._fetchCertUpdateVerb);
      }
   }

   @Override
   public final KeyStore getKeyStore() {
      return PGPKeyStore.getInstance();
   }

   @Override
   public final String getLaunchKeyStoreBrowserVerbDescription() {
      return KeyStoreBrowserResources.getString(6089);
   }

   @Override
   public final String getHelpContextString() {
      return "net_rim_bb_secureemail_help/pgp_certificates";
   }

   @Override
   public final int[] getRelevantRevocationReasons() {
      return REVOCATION_REASONS;
   }

   @Override
   public final CryptoSystemProperties getCryptoSystemProperties() {
      return this._pgpCryptoSystemProperties;
   }
}
