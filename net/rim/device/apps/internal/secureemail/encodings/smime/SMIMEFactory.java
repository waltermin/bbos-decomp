package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateSummaryDataSyncCollection;
import net.rim.device.api.crypto.certificate.CertificateSummaryDataSyncModelFactory;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
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
import net.rim.device.apps.api.transmission.rim.CMIMEConverterRegistry;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.internal.api.crypto.CryptoCommonResources;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateConverter;
import net.rim.device.apps.internal.secureemail.SecureEmailBodyModel;
import net.rim.device.apps.internal.secureemail.SecureEmailCryptoSystemProperties;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;
import net.rim.device.apps.internal.secureemail.SecureEmailOptions;
import net.rim.device.apps.internal.secureemail.SecureEmailOptionsModel;
import net.rim.device.apps.internal.secureemail.SecureEmailProcessor;
import net.rim.device.apps.internal.secureemail.SecureEmailUtilities;
import net.rim.device.apps.internal.secureemail.cache.CachedMessage;
import net.rim.device.apps.internal.secureemail.encodings.smime.cache.CachedSMIMEMessage;
import net.rim.device.apps.internal.secureemail.encodings.smime.sendmethods.SMIMESendMethod;
import net.rim.device.internal.ui.component.PopupDialog;

public final class SMIMEFactory extends SecureEmailFactory {
   private SMIMEUtilities _smimeUtilities = new SMIMEUtilities();
   private SMIMEFactory$SMIMEOptionsRecognizer _smimeOptionsRecognizer = new SMIMEFactory$SMIMEOptionsRecognizer();
   private SMIMECryptoSystemProperties _smimeCryptoSystemProperties = new SMIMECryptoSystemProperties();
   private PersistentObject _globalSMIMEOptionsHolder;
   private SMIMEOptions _globalSMIMEOptions;
   private OTASyncCapableSyncItem _globalSMIMEOptionsSyncItem;
   private static final long ID;
   private static final long GLOBAL_OPTIONS_ID;
   private static final byte[] SECURE_MESSAGING_CAPABILITIES_ARRAY = new byte[]{-128};
   private static SMIMEFactory _instance;

   public static final SMIMEFactory getInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (SMIMEFactory)applicationRegistry.getOrWaitFor(-4218546031917805731L);
         if (_instance == null) {
            _instance = new SMIMEFactory();
            applicationRegistry.put(-4218546031917805731L, _instance);
            _instance.initializeGlobalOptions();
         }
      }

      return _instance;
   }

   private SMIMEFactory() {
   }

   private final void initializeGlobalOptions() {
      this._globalSMIMEOptionsHolder = RIMPersistentStore.getPersistentObject(-2380431907050321605L);
      synchronized (this._globalSMIMEOptionsHolder) {
         this._globalSMIMEOptions = (SMIMEOptions)this._globalSMIMEOptionsHolder.getContents(CodeSigningKey.getBuiltInKey(4801362));
         if (this._globalSMIMEOptions == null) {
            this._globalSMIMEOptions = new SMIMEOptions();
            this._globalSMIMEOptionsHolder.setContents(this._globalSMIMEOptions, 4801362);
            this._globalSMIMEOptionsHolder.commit();
         }
      }

      this._globalSMIMEOptionsSyncItem = this._globalSMIMEOptions.getSyncItem();
   }

   @Override
   public final void performRegistration() {
      super.performRegistration();
      RIMModelFactory certificateAttachmentModelFactory = (RIMModelFactory)(new Object());
      RIMModelFactoryRepository.addFactory(2497613418300956405L, certificateAttachmentModelFactory);
      RIMModelFactoryRepository.addFactory(3893959701496671961L, certificateAttachmentModelFactory);
      RIMModelFactory emsEmailHeaderModelFactory = new EMSEmailHeaderModelFactory();
      RIMModelFactoryRepository.addFactory(2497613418300956405L, emsEmailHeaderModelFactory);
      CertificateConverter certificateConverter = new PKCS7CertificateConverter();
      CMIMEConverterRegistry.addConverter(certificateConverter, 4);
      certificateConverter = new PKCS12CertificateKeyConverter();
      CMIMEConverterRegistry.addConverter(certificateConverter, 5);
      CertificateSummaryDataSyncModelFactory.register((CertificateSummaryDataSyncModelFactory)(new Object()));
      CertificateSummaryDataSyncCollection.getInstance().registerKeyStore(this.getPreferredKeyStore());
   }

   @Override
   public final long getEncodingUID() {
      return 5942148136637320404L;
   }

   @Override
   public final String getEncodingString() {
      return SMIMEResources.getString(2034);
   }

   @Override
   public final int getEncodingPriority() {
      return 100;
   }

   @Override
   protected final byte[] getSecureMessagingCapabilitiesArray() {
      return SECURE_MESSAGING_CAPABILITIES_ARRAY;
   }

   @Override
   protected final SendMethod createSendMethod(RIMModel message, ServiceRecord serviceRecord, int encodingAction, Object context) {
      return new SMIMESendMethod(serviceRecord, encodingAction, context);
   }

   @Override
   public final SecureEmailOptionsModel createOptionsModel(Object context) {
      return new SMIMEOptionsModel(this, context);
   }

   @Override
   public final PopupDialog createCertificatePropertiesDialog(Certificate certificate, long certificateProperties) {
      return new SMIMECertificatePropertiesDialog(certificate, certificateProperties);
   }

   @Override
   public final Recognizer getOptionsRecognizer() {
      return this._smimeOptionsRecognizer;
   }

   @Override
   public final SecureEmailOptions createGlobalOptionsCopy() {
      return new SMIMEOptions(this._globalSMIMEOptions);
   }

   @Override
   protected final SecureEmailOptions getGlobalOptions() {
      return this._globalSMIMEOptions;
   }

   @Override
   protected final OTASyncCapableSyncItem getGlobalOptionsSyncItem() {
      return this._globalSMIMEOptionsSyncItem;
   }

   @Override
   public final synchronized void saveGlobalOptions() {
      this.checkMemoryCleanerSettings();
      this._globalSMIMEOptionsHolder.commit();
      this._globalSMIMEOptionsSyncItem.fireSyncItemUpdated();
   }

   @Override
   protected final void checkMemoryCleanerSettings() {
      boolean enableMemoryCleaner = this._globalSMIMEOptions.getSigningKeyStoreData() != null || this._globalSMIMEOptions.getEncryptionKeyStoreData() != null;
      MemoryCleanerManager.getInstance().setSMIMESecureOldObjects(enableMemoryCleaner);
   }

   @Override
   protected final Converter getConverter() {
      return SMIMEConverter.getInstance();
   }

   @Override
   public final SecureEmailProcessor createProcessor(SecureEmailBodyModel secureEmailBodyModel, Object target, boolean allowUI, Object context) {
      if (secureEmailBodyModel instanceof SMIMEBodyModel) {
         return new SMIMEProcessor((SMIMEBodyModel)secureEmailBodyModel, target, allowUI, context);
      } else {
         throw new Object();
      }
   }

   @Override
   public final CachedMessage createCachedMessage() {
      return new CachedSMIMEMessage();
   }

   @Override
   public final SecureEmailBodyModel createBodyModel(byte[] modelData) {
      return new SMIMEBodyModel(modelData, false, false, true, SMIMEConstants.SECURITY_ENCODING_SMIME_BYTES, null);
   }

   @Override
   public final String getLDAPBrowserContextString() {
      return "X509";
   }

   @Override
   public final SecureEmailUtilities getUtilities() {
      return this._smimeUtilities;
   }

   @Override
   public final KeyStore getPreferredKeyStore() {
      return DeviceKeyStore.getInstance();
   }

   @Override
   public final long getEventLoggerGUID() {
      return -2021078804619282789L;
   }

   @Override
   public final SecureEmailCryptoSystemProperties getCryptoSystemProperties() {
      return this._smimeCryptoSystemProperties;
   }

   @Override
   public final String getPublicKeyContainerString(boolean startWithUpperCase, boolean plural) {
      return CryptoCommonResources.getCertificateContainerString(startWithUpperCase, plural);
   }

   @Override
   public final String getPublicKeyContainerChainString(boolean startWithUpperCase) {
      return startWithUpperCase ? SMIMEResources.getString(2037) : SMIMEResources.getString(2046);
   }

   @Override
   public final String getHelpContextString() {
      return "net_rim_bb_secureemail_help/smime_certificates";
   }
}
