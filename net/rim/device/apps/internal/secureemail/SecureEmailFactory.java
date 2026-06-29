package net.rim.device.apps.internal.secureemail;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;
import net.rim.device.apps.api.transmission.rim.CMIMEConverterRegistry;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethodFactory;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.MessageListMainOptionsScreen;
import net.rim.device.apps.internal.blackberryemail.sendmethods.SendMethodSelector;
import net.rim.device.apps.internal.ldap.LDAPBrowserContextFactory;
import net.rim.device.apps.internal.secureemail.cache.CachedMessage;
import net.rim.device.apps.internal.secureemail.encodings.SecureEmailEncodingManager;
import net.rim.device.internal.deviceagent.OutgoingDeviceAgentCollection;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.vm.Array;

public class SecureEmailFactory implements SendMethodFactory, OptionsProviderRegistration$OptionsProvider {
   public void performRegistration() {
      SendMethodSelector.getInstance().registerSendMethodFactory(this);
      SecureEmailMailboxOptionsModelFactory.registerSecureEmailEncoding(this);
      OptionsProviderRegistration.registerOptionsProvider(this);
      MessageListMainOptionsScreen.registerOptions(this.createOptionsItem());
      SecureEmailOptions globalSecureEmailOptions = this.getGlobalOptions();
      Proxy.getInstance().addGlobalEventListener(globalSecureEmailOptions);
      this.getPreferredKeyStore().addCollectionListener(globalSecureEmailOptions);
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.enableSynchronization(this.getGlobalOptionsSyncItem());
      }

      OutgoingDeviceAgentCollection oac = (OutgoingDeviceAgentCollection)OutgoingDeviceAgentCollection.getInstance();
      if (oac != null) {
         oac.setDeviceCapabilitiesFlag((byte)2, this.getSecureMessagingCapabilitiesArray());
      }

      SecureEmailListener secureEmailListener = SecureEmailListener.getInstance();
      secureEmailListener.addKeyStore(this.getPreferredKeyStore());
      secureEmailListener.addKeyStore(TrustedKeyStore.getInstance());
      CMIMEConverterRegistry.addConverter(this.getConverter(), 4);
      EventLogger.register(this.getEventLoggerGUID(), this.getEncodingString(), 2);
      SecureEmailEncodingManager.getInstance().register(this.getEncodingUID(), this.getEncodingPriority());
      LDAPBrowserContextFactory.addLDAPBrowserEntryPoints(this.getLDAPBrowserContextString());
      this.checkMemoryCleanerSettings();
   }

   protected byte[] getSecureMessagingCapabilitiesArray() {
      throw null;
   }

   public int getEncodingPriority() {
      throw null;
   }

   public String getEncodingString() {
      throw null;
   }

   public String getHelpContextString() {
      throw null;
   }

   public String getPublicKeyContainerChainString(boolean _1) {
      throw null;
   }

   public String getPublicKeyContainerString(boolean _1, boolean _2) {
      throw null;
   }

   protected SendMethod createSendMethod(RIMModel _1, ServiceRecord _2, int _3, Object _4) {
      throw null;
   }

   public SecureEmailOptionsItem createOptionsItem() {
      return new SecureEmailOptionsItem(this);
   }

   public SecureEmailCryptoSystemProperties getCryptoSystemProperties() {
      throw null;
   }

   public SecureEmailConfigureVerb createGlobalOptionsConfigureVerb() {
      return new SecureEmailConfigureVerb(this);
   }

   public SecureEmailOptionsModel createOptionsModel(Object _1) {
      throw null;
   }

   public Recognizer getOptionsRecognizer() {
      throw null;
   }

   public SecureEmailOptions createGlobalOptionsCopy() {
      throw null;
   }

   protected SecureEmailOptions getGlobalOptions() {
      throw null;
   }

   protected OTASyncCapableSyncItem getGlobalOptionsSyncItem() {
      throw null;
   }

   public void saveGlobalOptions() {
      throw null;
   }

   public synchronized void saveGlobalOptions(SecureEmailOptions newGlobalOptions) {
      this.getGlobalOptions().copy(newGlobalOptions);
      this.saveGlobalOptions();
   }

   protected void checkMemoryCleanerSettings() {
      throw null;
   }

   public synchronized boolean getShowMessageDetails() {
      return this.getGlobalOptions().getShowMessageDetails();
   }

   public boolean areOptionsConfigurable(int encodingAction) {
      KeyStore keyStore = this.getPreferredKeyStore();
      if ((encodingAction & 1) != 0) {
         keyStore.addIndex(new KeyUsagePrivateKeysKeyStoreIndex());
         Enumeration enumeration = keyStore.elements(-2733667523168089402L, new Integer(0));
         return enumeration.hasMoreElements();
      } else {
         return true;
      }
   }

   public synchronized boolean areGlobalOptionsConfigured(int encodingAction, ServiceRecord serviceRecord) {
      return this.getGlobalOptions().isConfigured(encodingAction, serviceRecord);
   }

   public synchronized boolean areGlobalOptionsConfiguredCompletely() {
      SecureEmailOptions globalOptions = this.getGlobalOptions();
      return globalOptions.getSigningKeyStoreData() != null && globalOptions.getEncryptionKeyStoreData() != null;
   }

   public synchronized void autoConfigureGlobalOptions() {
      KeyStore keyStore = this.getPreferredKeyStore();
      keyStore.addIndex(new KeyUsagePrivateKeysKeyStoreIndex());
      SecureEmailOptions globalOptions = this.getGlobalOptions();
      boolean globalOptionsWereAutoConfigured = false;
      if (globalOptions.getSigningKeyStoreData() == null) {
         KeyStoreData autoConfiguredSigningData = null;
         Enumeration enumeration = keyStore.elements(-2733667523168089402L, new Integer(0));
         if (enumeration.hasMoreElements()) {
            autoConfiguredSigningData = (KeyStoreData)enumeration.nextElement();
            if (enumeration.hasMoreElements()) {
               autoConfiguredSigningData = null;
            }
         }

         if (autoConfiguredSigningData != null) {
            globalOptions.setSigningKeyStoreData(autoConfiguredSigningData);
            globalOptionsWereAutoConfigured = true;
         }
      }

      if (globalOptions.getEncryptionKeyStoreData() == null) {
         KeyStoreData autoConfiguredEncryptionData = null;
         Enumeration enumeration = keyStore.elements(-2733667523168089402L, new Integer(1));
         if (enumeration.hasMoreElements()) {
            autoConfiguredEncryptionData = (KeyStoreData)enumeration.nextElement();
            if (enumeration.hasMoreElements()) {
               autoConfiguredEncryptionData = null;
            }
         }

         if (autoConfiguredEncryptionData != null) {
            globalOptions.setEncryptionKeyStoreData(autoConfiguredEncryptionData);
            globalOptionsWereAutoConfigured = true;
         }
      }

      if (globalOptionsWereAutoConfigured) {
         this.saveGlobalOptions();
      }
   }

   protected Converter getConverter() {
      throw null;
   }

   public SecureEmailProcessor createProcessor(SecureEmailBodyModel _1, Object _2, boolean _3, Object _4) {
      throw null;
   }

   public CachedMessage createCachedMessage() {
      throw null;
   }

   public SecureEmailBodyModel createBodyModel(byte[] _1) {
      throw null;
   }

   public PopupDialog createCertificatePropertiesDialog(Certificate _1, long _2) {
      throw null;
   }

   public String getLDAPBrowserContextString() {
      throw null;
   }

   public SecureEmailUtilities getUtilities() {
      throw null;
   }

   public KeyStore getPreferredKeyStore() {
      throw null;
   }

   public long getEventLoggerGUID() {
      throw null;
   }

   @Override
   public Vector getOptionsItems() {
      Vector optionsItems = new Vector(1);
      optionsItems.addElement(this.createOptionsItem());
      return optionsItems;
   }

   @Override
   public int getPriority() {
      return 1;
   }

   @Override
   public SendMethod[] create(RIMModel message, ServiceRecord serviceRecord, Object context) {
      if (message instanceof EmailMessageModel) {
         EmailMessageModel emailMessageModel = (EmailMessageModel)message;
         boolean isPIN = emailMessageModel.flagsSet(8192);
         if (isPIN && serviceRecord != null) {
            return null;
         }

         if (!isPIN && serviceRecord == null) {
            return null;
         }
      }

      if (serviceRecord != null) {
         byte[] applicationData = serviceRecord.getApplicationData();
         if (applicationData != null && applicationData.length > 1 && applicationData[0] == 16) {
            DataBuffer applicationDataBuffer = new DataBuffer(applicationData, 1, applicationData.length, true);

            label105:
            try {
               while (!applicationDataBuffer.eof()) {
                  int type = applicationDataBuffer.readUnsignedByte();
                  int length = applicationDataBuffer.readCompressedInt();
                  if (type == 112) {
                     byte[] serviceSecureMessagingCapabilities = new byte[length];
                     applicationDataBuffer.read(serviceSecureMessagingCapabilities);
                     byte[] encodingCapabilitiesArray = this.getSecureMessagingCapabilitiesArray();
                     int encodingCapabilitiesArrayLength = encodingCapabilitiesArray.length;
                     if (encodingCapabilitiesArrayLength > length) {
                        Array.resize(serviceSecureMessagingCapabilities, encodingCapabilitiesArrayLength);
                     }

                     for (int i = 0; i < encodingCapabilitiesArrayLength; i++) {
                        if ((serviceSecureMessagingCapabilities[i] & encodingCapabilitiesArray[i]) != encodingCapabilitiesArray[i]) {
                           return null;
                        }
                     }
                     break;
                  }

                  applicationDataBuffer.skipBytes(length);
               }
            } finally {
               break label105;
            }
         }
      }

      SecureEmailUtilities utilities = this.getUtilities();
      SendMethod[] sendMethods = new SendMethod[3];
      int i = 0;
      if (!utilities.isEncryptionRequired()) {
         sendMethods[i++] = this.createSendMethod(message, serviceRecord, 1, context);
      }

      if (!utilities.isSignatureRequired()) {
         sendMethods[i++] = this.createSendMethod(message, serviceRecord, 2, context);
      }

      sendMethods[i++] = this.createSendMethod(message, serviceRecord, 3, context);
      Array.resize(sendMethods, i);
      return sendMethods;
   }

   @Override
   public long getEncodingUID() {
      throw null;
   }
}
