package net.rim.device.apps.internal.secureemail.encodings.pgp;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateSummaryDataSyncCollection;
import net.rim.device.api.crypto.certificate.CertificateSummaryDataSyncModelFactory;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificateSummaryDataSyncModelFactory;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.PGPKeyStore;
import net.rim.device.api.memorycleaner.MemoryCleanerManager;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.apps.internal.keystore.browser.pgp.PGPKeyAttachmentModelFactory;
import net.rim.device.apps.internal.keystore.browser.pgp.PGPKeyStoreBrowser;
import net.rim.device.apps.internal.secureemail.SecureEmailBodyModel;
import net.rim.device.apps.internal.secureemail.SecureEmailCryptoSystemProperties;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;
import net.rim.device.apps.internal.secureemail.SecureEmailOptions;
import net.rim.device.apps.internal.secureemail.SecureEmailOptionsModel;
import net.rim.device.apps.internal.secureemail.SecureEmailProcessor;
import net.rim.device.apps.internal.secureemail.SecureEmailUtilities;
import net.rim.device.apps.internal.secureemail.cache.CachedMessage;
import net.rim.device.apps.internal.secureemail.encodings.pgp.cache.CachedPGPMessage;
import net.rim.device.apps.internal.secureemail.encodings.pgp.sendmethods.PGPSendMethod;
import net.rim.device.internal.ui.component.PopupDialog;

public final class PGPFactory extends SecureEmailFactory {
   private final PGPUtilities _pgpUtilities = new PGPUtilities();
   private final PGPFactory$PGPOptionsRecognizer _pgpOptionsRecognizer = new PGPFactory$PGPOptionsRecognizer();
   private PGPCryptoSystemProperties _pgpCryptoSystemProperties = new PGPCryptoSystemProperties();
   private PersistentObject _globalPGPOptionsHolder;
   private PGPOptions _globalPGPOptions;
   private OTASyncCapableSyncItem _globalPGPOptionsSyncItem;
   private static final long ID = 388916581435827926L;
   private static final long GLOBAL_OPTIONS_ID = 6440148952809610573L;
   private static final byte[] SECURE_MESSAGING_CAPABILITIES_ARRAY = new byte[]{64};
   private static PGPFactory _instance;

   public static final PGPFactory getInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (PGPFactory)applicationRegistry.getOrWaitFor(388916581435827926L);
         if (_instance == null) {
            _instance = new PGPFactory();
            applicationRegistry.put(388916581435827926L, _instance);
            _instance.initializeGlobalOptions();
         }
      }

      return _instance;
   }

   private PGPFactory() {
   }

   private final void initializeGlobalOptions() {
      this._globalPGPOptionsHolder = RIMPersistentStore.getPersistentObject(6440148952809610573L);
      synchronized (this._globalPGPOptionsHolder) {
         this._globalPGPOptions = (PGPOptions)this._globalPGPOptionsHolder.getContents(CodeSigningKey.getBuiltInKey(4801362));
         if (this._globalPGPOptions == null) {
            this._globalPGPOptions = new PGPOptions();
            this._globalPGPOptionsHolder.setContents(this._globalPGPOptions, 4801362);
            this._globalPGPOptionsHolder.commit();
         }
      }

      this._globalPGPOptionsSyncItem = this._globalPGPOptions.getSyncItem();
   }

   @Override
   public final long getEncodingUID() {
      return 3681505275764314063L;
   }

   @Override
   public final String getEncodingString() {
      return PGPResources.getString(8045);
   }

   @Override
   public final int getEncodingPriority() {
      return 200;
   }

   @Override
   public final void performRegistration() {
      super.performRegistration();
      RIMModelFactory pgpKeyAttachmentModelFactory = new PGPKeyAttachmentModelFactory();
      RIMModelFactoryRepository.addFactory(2497613418300956405L, pgpKeyAttachmentModelFactory);
      RIMModelFactoryRepository.addFactory(3893959701496671961L, pgpKeyAttachmentModelFactory);
      PGPKeyStoreBrowser.register(this._pgpCryptoSystemProperties);
      CertificateSummaryDataSyncModelFactory.register(new PGPCertificateSummaryDataSyncModelFactory());
      CertificateSummaryDataSyncCollection.getInstance().registerKeyStore(this.getPreferredKeyStore());
   }

   @Override
   protected final byte[] getSecureMessagingCapabilitiesArray() {
      return SECURE_MESSAGING_CAPABILITIES_ARRAY;
   }

   @Override
   protected final SendMethod createSendMethod(RIMModel message, ServiceRecord serviceRecord, int encodingAction, Object context) {
      return new PGPSendMethod(serviceRecord, encodingAction, context);
   }

   @Override
   public final SecureEmailOptionsModel createOptionsModel(Object context) {
      return new PGPOptionsModel(this, context);
   }

   @Override
   public final Recognizer getOptionsRecognizer() {
      return this._pgpOptionsRecognizer;
   }

   @Override
   public final SecureEmailOptions createGlobalOptionsCopy() {
      return new PGPOptions(this._globalPGPOptions);
   }

   @Override
   protected final SecureEmailOptions getGlobalOptions() {
      return this._globalPGPOptions;
   }

   @Override
   protected final OTASyncCapableSyncItem getGlobalOptionsSyncItem() {
      return this._globalPGPOptionsSyncItem;
   }

   @Override
   public final synchronized void saveGlobalOptions() {
      this.checkMemoryCleanerSettings();
      this._globalPGPOptionsHolder.commit();
      this._globalPGPOptionsSyncItem.fireSyncItemUpdated();
   }

   @Override
   protected final void checkMemoryCleanerSettings() {
      boolean enableMemoryCleaner = this._globalPGPOptions.getSigningKeyStoreData() != null || this._globalPGPOptions.getEncryptionKeyStoreData() != null;
      MemoryCleanerManager.getInstance().setPGPSecureOldObjects(enableMemoryCleaner);
   }

   @Override
   protected final Converter getConverter() {
      return PGPConverter.getInstance();
   }

   @Override
   public final SecureEmailProcessor createProcessor(SecureEmailBodyModel secureEmailBodyModel, Object target, boolean allowUI, Object context) {
      if (secureEmailBodyModel instanceof PGPBodyModel) {
         return new PGPProcessor((PGPBodyModel)secureEmailBodyModel, target, allowUI, context);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final CachedMessage createCachedMessage() {
      return new CachedPGPMessage();
   }

   @Override
   public final SecureEmailBodyModel createBodyModel(byte[] modelData) {
      return new PGPBodyModel(modelData, PGPConstants.SECURITY_ENCODING_PGP_BYTES, null);
   }

   @Override
   public final PopupDialog createCertificatePropertiesDialog(Certificate certificate, long certificateProperties) {
      return new PGPCertificatePropertiesDialog(certificate, certificateProperties);
   }

   @Override
   public final String getLDAPBrowserContextString() {
      return "PGP";
   }

   @Override
   public final SecureEmailUtilities getUtilities() {
      return this._pgpUtilities;
   }

   @Override
   public final KeyStore getPreferredKeyStore() {
      return PGPKeyStore.getInstance();
   }

   @Override
   public final long getEventLoggerGUID() {
      return 234044482576569793L;
   }

   @Override
   public final SecureEmailCryptoSystemProperties getCryptoSystemProperties() {
      return this._pgpCryptoSystemProperties;
   }

   @Override
   public final String getPublicKeyContainerString(boolean startWithUpperCase, boolean plural) {
      return CryptoCommonResources.getPGPContainerString(startWithUpperCase, plural);
   }

   @Override
   public final String getPublicKeyContainerChainString(boolean startWithUpperCase) {
      return CryptoCommonResources.getPGPContainerString(startWithUpperCase, false);
   }

   @Override
   public final String getHelpContextString() {
      return "net_rim_bb_secureemail_help/pgp_certificates";
   }
}
