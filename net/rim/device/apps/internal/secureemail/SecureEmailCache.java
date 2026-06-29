package net.rim.device.apps.internal.secureemail;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.crypto.SymmetricKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateServerInfo;
import net.rim.device.api.crypto.certificate.CertificateServers;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.certificate.status.CertificateStatusRequest;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStorePasswordManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.utility.general.LRUCache;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.secureemail.cache.CachedMessage;
import net.rim.device.apps.internal.secureemail.cache.CachedMessageStatusData;
import net.rim.device.apps.internal.secureemail.cache.CachedOpenedMessageData;

public class SecureEmailCache {
   private LRUCache _messageCache = (LRUCache)(new Object());
   private LRUCache _messageStatusCache = (LRUCache)(new Object());
   private LRUCache _contextCache = (LRUCache)(new Object());
   private LRUCache _openedMessageCache = (LRUCache)(new Object());
   private SecureEmailLookupFailureCache _autoFetchStatusFailureCache = new SecureEmailLookupFailureCache(3600000);
   private long _lastGlobalAutoFetchStatusFailure = -1;
   private Hashtable _certServerFetchInfo;
   private SecureEmailListener _secureEmailListener = SecureEmailListener.getInstance();
   private static int ldapServer = 1;
   public static CertificateServerInfo _defaultServer = (CertificateServerInfo)(new Object("", 1, "default Server", "", 0));
   private static final long TIME_BETWEEN_FAILED_AUTO_FETCHES = 3600000L;
   private static final long ID_LOCK = -2405970696515239737L;
   private static final long ID = 6573482441497432665L;

   private SecureEmailCache() {
      CertificateServers certificateServers = CertificateServers.getInstance();
      this._certServerFetchInfo = (Hashtable)(new Object(certificateServers.getServerSize(ldapServer)));
      this._certServerFetchInfo.put(_defaultServer, new SecureEmailCache$CertificateServerFetchInformation(null));
   }

   public static SecureEmailCache getInstance() {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      synchronized (appRegistry.getObject(-2405970696515239737L)) {
         SecureEmailCache secureEmailCache = (SecureEmailCache)appRegistry.get(6573482441497432665L);
         if (secureEmailCache == null) {
            secureEmailCache = new SecureEmailCache();
            appRegistry.put(6573482441497432665L, secureEmailCache);
         }

         return secureEmailCache;
      }
   }

   public synchronized void putMessage(BodyModel model, CachedMessage message) {
      this._messageCache.put(model, message);
   }

   public synchronized boolean isMessageInCache(BodyModel model) {
      Object o = this._messageCache.get(model);
      return o instanceof CachedMessage;
   }

   public synchronized boolean isMessageEncrypted(BodyModel model) {
      Object o = this._messageCache.get(model);
      if (!(o instanceof CachedMessage)) {
         return false;
      }

      CachedMessage cachedMessage = (CachedMessage)o;
      return cachedMessage.isEncrypted();
   }

   public synchronized boolean isMessageSigned(BodyModel model) {
      Object o = this._messageCache.get(model);
      if (!(o instanceof CachedMessage)) {
         return false;
      }

      CachedMessage cachedMessage = (CachedMessage)o;
      return cachedMessage.isSigned();
   }

   public synchronized boolean isMessageBodyTruncated(BodyModel model) {
      Object o = this._messageCache.get(model);
      if (!(o instanceof CachedMessage)) {
         return false;
      }

      CachedMessage cachedMessage = (CachedMessage)o;
      return cachedMessage.isBodyTruncated();
   }

   public CachedMessage getMessage(BodyModel model, boolean allowUI) {
      CachedMessage cachedMessage = null;

      try {
         return this.getMessageWithExceptions(model, allowUI);
      } finally {
         cachedMessage = new CachedMessage();
         cachedMessage.setErrorString(SecureEmailResources.getString(26));
         return cachedMessage;
      }
   }

   public CachedMessage getMessageWithExceptions(BodyModel model, boolean allowUI) {
      CachedMessage cachedMessage = null;
      synchronized (this) {
         Object o = this._messageCache.get(model);
         if (o instanceof CachedMessage) {
            cachedMessage = (CachedMessage)o;
         }
      }

      if (cachedMessage != null && cachedMessage.isPasswordRequiredForAccess() && !this._secureEmailListener.isMessageOpen(model)) {
         if (!allowUI) {
            return null;
         }

         KeyStorePasswordManager.getInstance().getTicket(SecureEmailResources.getString(25), DeviceKeyStore.getInstance());
      }

      return cachedMessage;
   }

   private synchronized boolean cleanCache(LRUCache cacheToClean) {
      int numMessages = cacheToClean.size();
      if (numMessages == 0) {
         return false;
      }

      int currentMessageIndex = 0;
      Object[] messagesToRemove = new Object[numMessages];
      Enumeration enumeration = cacheToClean.keys();

      while (enumeration.hasMoreElements()) {
         Object currentMessage = enumeration.nextElement();
         if (!this._secureEmailListener.isMessageOpen(currentMessage)) {
            messagesToRemove[currentMessageIndex++] = currentMessage;
         }
      }

      for (int i = 0; i < currentMessageIndex; i++) {
         cacheToClean.remove(messagesToRemove[i]);
      }

      return currentMessageIndex > 0;
   }

   public synchronized boolean cleanMessageCache() {
      return this.cleanCache(this._messageCache);
   }

   public synchronized boolean cleanMessageStatusCache() {
      return this.cleanCache(this._messageStatusCache);
   }

   public synchronized boolean cleanProcessingContextCache() {
      return this.cleanCache(this._contextCache);
   }

   public synchronized void putSignatureStatus(EmailMessageModel model, int signatureStatus, String signerName, String details) {
      CachedMessageStatusData statusData = this.getOrCreateCachedMessageStatusData(model);
      statusData.setSignatureStatus(signatureStatus, signerName, details);
      this._messageStatusCache.put(model, statusData);
   }

   public synchronized void putTrustStatus(EmailMessageModel model, int trustStatus, String trustStatusDetails) {
      CachedMessageStatusData statusData = this.getOrCreateCachedMessageStatusData(model);
      statusData.setTrustStatus(trustStatus, trustStatusDetails);
      this._messageStatusCache.put(model, statusData);
   }

   public synchronized void putEncryptionStatus(EmailMessageModel model, String encryptionAlgorithm, int bitLength) {
      CachedMessageStatusData statusData = this.getOrCreateCachedMessageStatusData(model);
      statusData.setEncryptionStatus(encryptionAlgorithm, bitLength);
      this._messageStatusCache.put(model, statusData);
   }

   private synchronized CachedMessageStatusData getOrCreateCachedMessageStatusData(EmailMessageModel model) {
      CachedMessageStatusData statusData = this.getMessageStatusData(model);
      if (statusData == null) {
         statusData = new CachedMessageStatusData();
      }

      return statusData;
   }

   public synchronized CachedMessageStatusData getMessageStatusData(EmailMessageModel model) {
      Object o = this._messageStatusCache.get(model);
      return !(o instanceof CachedMessageStatusData) ? null : (CachedMessageStatusData)o;
   }

   public synchronized void putProcessingContext(BodyModel model, Object processingContext) {
      this._contextCache.put(model, processingContext);
   }

   public synchronized Object getAndRemoveProcessingContext(BodyModel model) {
      Object processingContext = this._contextCache.get(model);
      this._contextCache.remove(model);
      return processingContext;
   }

   public synchronized void putSessionKey(EmailMessageModel model, SymmetricKey key) {
      CachedOpenedMessageData openedMessageData = this.getOrCreateCachedOpenedMessageData(model);
      openedMessageData.setSymmetricKey(key);
      this._openedMessageCache.put(model, openedMessageData);
   }

   public synchronized SymmetricKey getSessionKey(EmailMessageModel model) {
      CachedOpenedMessageData openedMessageData = this.getCachedOpenedMessageData(model);
      return openedMessageData != null ? openedMessageData.getSymmetricKey() : null;
   }

   public synchronized void putShowShortForm(EmailMessageModel model, boolean showShortForm) {
      CachedOpenedMessageData openedMessageData = this.getOrCreateCachedOpenedMessageData(model);
      openedMessageData.setShowShortForm(showShortForm);
      this._openedMessageCache.put(model, openedMessageData);
   }

   public synchronized boolean getShowShortForm(EmailMessageModel model, boolean defaultValue) {
      CachedOpenedMessageData openedMessageData = this.getCachedOpenedMessageData(model);
      return openedMessageData != null ? openedMessageData.getShowShortForm() : defaultValue;
   }

   public synchronized CertificateHarvesterManager getCertificateHarvesterManager(EmailMessageModel model) {
      CachedOpenedMessageData openedMessageData = this.getOrCreateCachedOpenedMessageData(model);
      CertificateHarvesterManager certificateHarvesterManager = openedMessageData.getCertificateHarvesterManager();
      if (certificateHarvesterManager == null) {
         certificateHarvesterManager = new CertificateHarvesterManager();
         openedMessageData.setCertificateHarvesterManager(certificateHarvesterManager);
         this._openedMessageCache.put(model, openedMessageData);
      }

      return certificateHarvesterManager;
   }

   public synchronized void removeCachedOpenedMessage(EmailMessageModel model) {
      CachedOpenedMessageData openedMessageData = this.getCachedOpenedMessageData(model);
      if (openedMessageData != null) {
         CertificateHarvesterManager certificateHarvesterManager = openedMessageData.getCertificateHarvesterManager();
         if (certificateHarvesterManager != null) {
            certificateHarvesterManager.terminate();
         }

         this._openedMessageCache.remove(model);
      }
   }

   private synchronized CachedOpenedMessageData getOrCreateCachedOpenedMessageData(EmailMessageModel model) {
      CachedOpenedMessageData openedMessageData = this.getCachedOpenedMessageData(model);
      if (openedMessageData == null) {
         openedMessageData = new CachedOpenedMessageData();
      }

      return openedMessageData;
   }

   public synchronized CachedOpenedMessageData getCachedOpenedMessageData(EmailMessageModel model) {
      Object o = this._openedMessageCache.get(model);
      return !(o instanceof CachedOpenedMessageData) ? null : (CachedOpenedMessageData)o;
   }

   public void addFailureTimeElement(CertificateServerInfo[] certificateServer, String emailAddress, boolean isGlobal) {
      int numCertificateServers = certificateServer == null ? 0 : certificateServer.length;

      for (int i = 0; i < numCertificateServers; i++) {
         CertificateServerInfo currentCertificateServerInfo = certificateServer[i];
         if (currentCertificateServerInfo == null) {
            currentCertificateServerInfo = _defaultServer;
         }

         SecureEmailCache$CertificateServerFetchInformation currentFetchInformation = this.getCertificateServerFetchInformation(currentCertificateServerInfo);
         if (isGlobal) {
            currentFetchInformation.setGlobalFetchFailureTime();
         } else {
            currentFetchInformation.putEmailFetchFailureTime(emailAddress);
         }
      }
   }

   public void addFailureTimeElement(CertificateServerInfo[] certificateServer, Object certificateID, boolean isGlobal) {
      int numCertificateServers = certificateServer == null ? 0 : certificateServer.length;

      for (int i = 0; i < numCertificateServers; i++) {
         CertificateServerInfo currentCertificateServerInfo = certificateServer[i];
         if (currentCertificateServerInfo == null) {
            currentCertificateServerInfo = _defaultServer;
         }

         SecureEmailCache$CertificateServerFetchInformation currentFetchInformation = this.getCertificateServerFetchInformation(currentCertificateServerInfo);
         if (isGlobal) {
            currentFetchInformation.setGlobalFetchFailureTime();
         } else {
            currentFetchInformation.putCertIDFetchFailureTime(certificateID);
         }
      }
   }

   public synchronized boolean shouldAttemptAutoFetchStatus(Certificate certificate) {
      return this.shouldAttemptAutoFetch(this._lastGlobalAutoFetchStatusFailure) && !this._autoFetchStatusFailureCache.checkFetchFailure(certificate);
   }

   public synchronized boolean shouldAttemptAutoFetchCertificate(CertificateServerInfo certServerInfo, String emailAddress) {
      if (certServerInfo == null) {
         certServerInfo = _defaultServer;
      }

      SecureEmailCache$CertificateServerFetchInformation currentServerFetchInfo = this.getCertificateServerFetchInformation(certServerInfo);
      long currentGlobalServerFailure = currentServerFetchInfo.getGlobalFetchFailureTime();
      return this.shouldAttemptAutoFetch(currentGlobalServerFailure) && !currentServerFetchInfo.checkEmailFetchFailure(emailAddress);
   }

   public synchronized boolean shouldAttemptAutoFetchCertificate(CertificateServerInfo certServerInfo, Object certificateID) {
      if (certServerInfo == null) {
         certServerInfo = _defaultServer;
      }

      SecureEmailCache$CertificateServerFetchInformation currentServerFetchInfo = this.getCertificateServerFetchInformation(certServerInfo);
      long currentGlobalServerFailure = currentServerFetchInfo.getGlobalFetchFailureTime();
      return this.shouldAttemptAutoFetch(currentGlobalServerFailure) && !currentServerFetchInfo.checkCertIDFetchFailure(certificateID);
   }

   private SecureEmailCache$CertificateServerFetchInformation getCertificateServerFetchInformation(CertificateServerInfo certServerInfo) {
      SecureEmailCache$CertificateServerFetchInformation currentServerFetchInfo = (SecureEmailCache$CertificateServerFetchInformation)this._certServerFetchInfo
         .get(certServerInfo);
      if (currentServerFetchInfo == null) {
         currentServerFetchInfo = new SecureEmailCache$CertificateServerFetchInformation(null);
         this._certServerFetchInfo.put(certServerInfo, currentServerFetchInfo);
      }

      return currentServerFetchInfo;
   }

   private boolean shouldAttemptAutoFetch(long lastGlobalFailure) {
      long currentTimeMillis = System.currentTimeMillis();
      return lastGlobalFailure < 0 || currentTimeMillis - lastGlobalFailure > 3600000;
   }

   public synchronized void recordAutoFetchStatusResult(CertificateStatusRequest completedRequest) {
      int errorCode = completedRequest.getErrorCode();
      switch (errorCode) {
         case -1:
         case 1:
         case 2:
         case 3:
         case 4:
            break;
         case 0:
         default:
            CertificateStatus overallStatus = completedRequest.getOverallStatus();
            if (overallStatus == null || overallStatus.isStale()) {
               this._autoFetchStatusFailureCache.recordFetchFailure(completedRequest.getCertChain()[0]);
               return;
            }
            break;
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
            this._lastGlobalAutoFetchStatusFailure = System.currentTimeMillis();
            break;
         case 16:
            this._autoFetchStatusFailureCache.recordFetchFailure(completedRequest.getCertChain()[0]);
            return;
      }
   }

   public synchronized void recordAutoFetchCertificateResult(
      String emailAddress, int ldapErrorCode, int numCertificatesFetched, CertificateServerInfo[] certificateServerInfo
   ) {
      switch (ldapErrorCode) {
         case -1:
            if (numCertificatesFetched == 0) {
               this.addFailureTimeElement(certificateServerInfo, emailAddress, false);
               return;
            }
            break;
         case 0:
         case 1:
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 127:
         case 128:
         case 129:
         case 130:
         case 131:
         case 132:
            this.addFailureTimeElement(certificateServerInfo, emailAddress, true);
      }
   }

   public synchronized void recordAutoFetchCertificateResult(
      Object certificateID, int ldapErrorCode, int numCertificatesFetched, CertificateServerInfo[] certificateServerInfo
   ) {
      switch (ldapErrorCode) {
         case -1:
            if (numCertificatesFetched == 0) {
               this.addFailureTimeElement(certificateServerInfo, certificateID, false);
               return;
            }
            break;
         case 0:
         case 1:
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 127:
         case 128:
         case 129:
         case 130:
         case 131:
         case 132:
            this.addFailureTimeElement(certificateServerInfo, certificateID, true);
      }
   }

   public synchronized boolean cleanAutoFetchInformation() {
      boolean cleaned = false;
      if (this._lastGlobalAutoFetchStatusFailure >= 0) {
         this._lastGlobalAutoFetchStatusFailure = -1;
         cleaned = true;
      }

      if (!this._certServerFetchInfo.isEmpty()) {
         this._certServerFetchInfo.clear();
         cleaned = true;
      }

      if (this._autoFetchStatusFailureCache.numElements() > 0) {
         this._autoFetchStatusFailureCache.clear();
         cleaned = true;
      }

      return cleaned;
   }
}
