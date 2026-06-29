package net.rim.device.apps.internal.secureemail;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChainProperties;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.status.CertificateStatusListener;
import net.rim.device.api.crypto.certificate.status.CertificateStatusProvider;
import net.rim.device.api.crypto.certificate.status.CertificateStatusRequest;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.api.crypto.verb.DisplayCertificateVerb;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.ldap.LDAPFetchCertificatesVerb;
import net.rim.device.apps.internal.secureemail.cache.CachedMessageStatusData;
import net.rim.device.apps.internal.secureemail.server.DisplayServerCertificateVerb;
import net.rim.device.apps.internal.secureemail.server.SecureEmailCertificateServer;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerEnrollmentException;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerManager;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerOperationListener;

public class SecureEmailSignatureField implements CollectionListener, ContextMenuDelegate, CertificateStatusListener {
   protected SecureEmailFactory _secureEmailFactory;
   protected SecureEmailCertificateServer[] _secureEmailCertificateServers;
   protected boolean _inbound;
   protected boolean _moreAvailable;
   protected long _creationDate;
   protected String _senderEmailAddress;
   protected boolean _isPINMessage;
   protected boolean _requestedStatus;
   protected int _besVerificationState;
   protected byte[] _besSignerCertificateHash;
   protected String _besNoVerifyReason;
   protected Certificate _signerCertificate;
   protected Certificate[] _signerCertificateChain;
   protected long _signerCertificateChainProperties;
   protected VerbMenuItem _displaySignerCertificateMenuItem;
   protected VerbMenuItem _fetchSignerCertificateMenuItem;
   protected SecureEmailSignatureField$SignatureStatusField _signatureStatus;
   protected SecureEmailSignatureField$TrustStatusField _trustStatus;
   protected EmailMessageModel _messageModel;
   protected ServiceRecord _serviceRecord;
   protected VerticalExtentIndicatorFieldManager _manager;
   private Object _verifyThreadLock;
   private boolean _threadWaiting;
   private Object _threadWaitingLock;
   protected int[] _overallStatusArray = new int[3];
   protected static final long TIME_DIFFERENCE_THRESHOLD = 1800000L;
   protected static final int OVERALL_SIGNATURE_STATUS_BAD = 0;
   protected static final int OVERALL_SIGNATURE_STATUS_CAUTION = 1;
   protected static final int OVERALL_SIGNATURE_STATUS_GOOD = 2;

   protected void initialize() {
      String signatureStatusDetails = null;
      String trustStatusDetails = null;
      String signerName = null;
      SecureEmailCache secureEmailCache = SecureEmailCache.getInstance();
      CachedMessageStatusData statusData = secureEmailCache.getMessageStatusData(this._messageModel);
      boolean checkSignature = this._besNoVerifyReason == null && this.isSignatureVerificationPossible();
      boolean checkTrust = checkSignature || this._besSignerCertificateHash != null;
      int initialSignatureStatus;
      if (!checkSignature) {
         initialSignatureStatus = 3;
         secureEmailCache.putSignatureStatus(this._messageModel, initialSignatureStatus, null, null);
      } else if (statusData != null) {
         initialSignatureStatus = statusData.getSignatureStatus();
         signerName = statusData.getSignerName();
         signatureStatusDetails = statusData.getSignatureStatusDetails();
      } else {
         initialSignatureStatus = 0;
      }

      int initialTrustStatus;
      if (!checkTrust) {
         initialTrustStatus = 9;
         secureEmailCache.putTrustStatus(this._messageModel, initialTrustStatus, null);
      } else if (statusData != null) {
         initialTrustStatus = statusData.getTrustStatus();
         trustStatusDetails = statusData.getTrustStatusDetails();
      } else {
         initialTrustStatus = 0;
      }

      Application displayApp = Application.getApplication();
      this._signatureStatus = this.createSignatureStatusField(displayApp, initialSignatureStatus, signerName, signatureStatusDetails);
      this._trustStatus = this.createTrustStatusField(displayApp, initialTrustStatus, trustStatusDetails);
      this._signatureStatus.updateStatus();
      this._trustStatus.updateStatus();
   }

   public void verify() {
      boolean checkSignature = this._besNoVerifyReason == null && this.isSignatureVerificationPossible();
      boolean checkTrust = checkSignature || this._besSignerCertificateHash != null;
      if (checkSignature || checkTrust) {
         synchronized (this._threadWaitingLock) {
            if (!this._threadWaiting) {
               this._threadWaiting = true;
               new SecureEmailSignatureField$VerifyThread(this, checkSignature, checkTrust).start();
            }
         }
      }
   }

   protected void checkSendingDate() {
      throw null;
   }

   protected void checkSendingAddress() {
      if ((this._inbound || this._senderEmailAddress != null)
         && !SecureEmailUtilities.verifySenderAddress(this._signerCertificate, this._senderEmailAddress, this._isPINMessage)) {
         this._trustStatus.setStatus(8);
      }
   }

   protected SecureEmailServerOperationListener getServerOperationListener() {
      return new SecureEmailSignatureField$StatusIconSecureEmailServerOperationListener(this, null);
   }

   protected void getSignerCertificateChainAndProperties() {
      if (this._signerCertificate == null) {
         this._signerCertificateChain = null;
      } else {
         String[] containerStringUpperSingularArray = new String[]{this._secureEmailFactory.getPublicKeyContainerString(true, false)};
         String displayCertificateVerbDescription = MessageFormat.format(SecureEmailResources.getString(57), containerStringUpperSingularArray);
         int numSecureEmailCertificateServers = this._secureEmailCertificateServers.length;
         if (numSecureEmailCertificateServers == 0) {
            Certificate[][] certificateChains = CertificateUtilities.buildCertificateChains(
               this._signerCertificate, this.getIncludedCertificates(), this._secureEmailFactory.getPreferredKeyStore(), this._senderEmailAddress
            );
            long[] certificateChainProperties = CertificateChainProperties.getCertificateChainProperties(
               certificateChains,
               this._secureEmailFactory.getPreferredKeyStore(),
               TrustedKeyStore.getInstance(),
               this._creationDate,
               this._secureEmailFactory.getCryptoSystemProperties()
            );
            int bestCertificateChainIndex = CertificateChainProperties.selectBestCertificateChain(certificateChainProperties);
            this._signerCertificateChain = certificateChains[bestCertificateChainIndex];
            this._signerCertificateChainProperties = certificateChainProperties[bestCertificateChainIndex];
            this._displaySignerCertificateMenuItem = new VerbMenuItem(
               new DisplayCertificateVerb(
                  displayCertificateVerbDescription,
                  this._signerCertificate,
                  this.getIncludedCertificates(),
                  this._secureEmailFactory.getPreferredKeyStore(),
                  this._secureEmailFactory.getCryptoSystemProperties(),
                  null
               ),
               10
            );
         } else {
            for (int i = 0; i < numSecureEmailCertificateServers; i++) {
               try {
                  Long certificateProperties = this._secureEmailCertificateServers[i].getCertificateProperties(this._signerCertificate, this._creationDate);
                  if (certificateProperties != null) {
                     this._signerCertificateChain = new Certificate[]{this._signerCertificate};
                     this._signerCertificateChainProperties = certificateProperties;
                     this._displaySignerCertificateMenuItem = new VerbMenuItem(
                        new DisplayServerCertificateVerb(
                           displayCertificateVerbDescription, this._signerCertificate, this._signerCertificateChainProperties, this._secureEmailFactory
                        ),
                        10
                     );
                     return;
                  }
               } catch (SecureEmailServerEnrollmentException var9) {
               } finally {
                  continue;
               }
            }
         }

         if (this._signerCertificateChain == null) {
            this._signerCertificateChain = new Certificate[]{this._signerCertificate};
            this._signerCertificateChainProperties = 8;
            this._displaySignerCertificateMenuItem = new VerbMenuItem(
               new DisplayServerCertificateVerb(
                  displayCertificateVerbDescription, this._signerCertificate, this._signerCertificateChainProperties, this._secureEmailFactory
               ),
               10
            );
         }
      }
   }

   public int getOverallSignatureStatusColourRGB() {
      boolean isColourDevice = Graphics.isColor();
      int signatureOverallStatus = this._signatureStatus.getOverallSignatureStatus();
      int trustOverallStatus = this._trustStatus.getOverallSignatureStatus();
      if (signatureOverallStatus != 0 && trustOverallStatus != 0) {
         if (signatureOverallStatus != 1 && trustOverallStatus != 1) {
            return isColourDevice ? 1831945 : 0;
         } else {
            return isColourDevice ? 16770048 : 16777215;
         }
      } else {
         return isColourDevice ? 16711680 : 16777215;
      }
   }

   public void updateManagerExtentIndicator() {
      if (this._manager != null) {
         this._manager.setExtentIndicatorColourRGB(this.getOverallSignatureStatusColourRGB());
      }
   }

   protected MatchProvider createSignerCertificateMatchProvider() {
      throw null;
   }

   protected SecureEmailSignatureField$TrustStatusField createTrustStatusField(Application _1, int _2, String _3) {
      throw null;
   }

   protected SecureEmailSignatureField$SignatureStatusField createSignatureStatusField(Application _1, int _2, String _3, String _4) {
      throw null;
   }

   protected String getSignatureDigestName() {
      throw null;
   }

   protected void verifySignature() {
      throw null;
   }

   public StatusField getSignatureStatusField() {
      return this._signatureStatus;
   }

   public StatusField getTrustStatusField() {
      return this._trustStatus;
   }

   protected boolean fireBackgroundStatusRequest(Certificate[] certChain) {
      if (certChain != null && certChain.length != 0 && SecureEmailCache.getInstance().shouldAttemptAutoFetchStatus(certChain[0])) {
         CertificateStatusRequest request = new CertificateStatusRequest(certChain, true, this._secureEmailFactory.getPreferredKeyStore(), null, null);
         int returnValue = CertificateStatusProvider.fetchCertificateStatus(request, this);
         this._requestedStatus = true;
         return returnValue != 4;
      } else {
         return false;
      }
   }

   protected boolean isSignatureVerificationPossible() {
      throw null;
   }

   protected void verifyTrust() {
      if ((this._signerCertificateChainProperties & 1024) != 0) {
         this._trustStatus.setStatus(4);
      } else if ((this._signerCertificateChainProperties & 256) != 0) {
         this._trustStatus.setStatus(3);
      } else if ((this._signerCertificateChainProperties & 8) != 0) {
         this._trustStatus.setStatus(2);
      } else if ((this._signerCertificateChainProperties & 22) != 0) {
         this._trustStatus.setStatus(13);
      } else if ((this._signerCertificateChainProperties & 32) != 0) {
         this._trustStatus.setStatus(12);
      } else if ((this._signerCertificateChainProperties & 2048) != 0) {
         if (!this._requestedStatus && this.fireBackgroundStatusRequest(this._signerCertificateChain)) {
            this._trustStatus.setStatus(11);
         } else {
            this._trustStatus.setStatus(10);
         }
      } else {
         this._trustStatus.setStatus(1);
      }
   }

   protected void getSignerCertificateWithoutVerifying() {
      throw null;
   }

   protected Certificate[] getIncludedCertificates() {
      throw null;
   }

   @Override
   public void receiveStatusResponse(CertificateStatusRequest request) {
      this.verify();
      SecureEmailCache.getInstance().recordAutoFetchStatusResult(request);
   }

   @Override
   public void reset(Collection collection) {
      this.verify();
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.verify();
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      this.verify();
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      if (this._signatureStatus.getStatus() != 1 || this._trustStatus.getStatus() != 1) {
         this.verify();
      }
   }

   @Override
   public void makeDelegateContextMenu(ContextMenu contextMenu) {
      String[] containerStringUpperSingularArray = new String[]{this._secureEmailFactory.getPublicKeyContainerString(true, false)};
      if (this._signerCertificate != null) {
         contextMenu.addItem(this._displaySignerCertificateMenuItem);
      } else {
         if (this._fetchSignerCertificateMenuItem == null) {
            String fetchCertificateVerbDescription = MessageFormat.format(SecureEmailResources.getString(58), containerStringUpperSingularArray);
            String senderEmailAddress;
            if (this._isPINMessage && this._senderEmailAddress != null) {
               String[] emailAddresses = SecureEmailUtilities.convertPINToEmailAddress(this._senderEmailAddress);
               int numEmailAddresses = emailAddresses != null ? emailAddresses.length : 0;
               StringBuffer senderToEmailAddresses = new StringBuffer();

               for (int i = 0; i < numEmailAddresses; i++) {
                  if (i > 0) {
                     senderToEmailAddresses.append(";");
                  }

                  senderToEmailAddresses.append(emailAddresses[i]);
               }

               senderEmailAddress = senderToEmailAddresses.toString();
            } else {
               senderEmailAddress = this._senderEmailAddress;
            }

            this._fetchSignerCertificateMenuItem = new VerbMenuItem(
               new LDAPFetchCertificatesVerb(
                  fetchCertificateVerbDescription,
                  this._secureEmailFactory.getLDAPBrowserContextString(),
                  senderEmailAddress,
                  this.createSignerCertificateMatchProvider(),
                  true
               ),
               10
            );
         }

         contextMenu.addItem(this._fetchSignerCertificateMenuItem);
      }
   }

   public SecureEmailSignatureField(
      SecureEmailFactory secureEmailFactory,
      ServiceRecord serviceRecord,
      boolean inbound,
      boolean moreAvailable,
      long creationDate,
      String senderEmailAddress,
      boolean isPINMessage,
      int besVerificationState,
      byte[] besSignerCertificateHash,
      String besNoVerifyReason,
      Manager manager,
      Object context,
      SecureEmailSignatureField replacedField
   ) {
      this._secureEmailFactory = secureEmailFactory;
      this._serviceRecord = serviceRecord;
      this._inbound = inbound;
      this._moreAvailable = moreAvailable;
      this._creationDate = creationDate;
      this._senderEmailAddress = senderEmailAddress;
      this._isPINMessage = isPINMessage;
      this._besVerificationState = besVerificationState;
      this._besSignerCertificateHash = besSignerCertificateHash;
      this._besNoVerifyReason = besNoVerifyReason;
      if (manager instanceof VerticalExtentIndicatorFieldManager) {
         this._manager = (VerticalExtentIndicatorFieldManager)manager;
      }

      this._messageModel = (EmailMessageModel)ContextObject.get(context, 246);
      this._secureEmailCertificateServers = SecureEmailServerManager.getInstance().getCertificateServers(this._serviceRecord);
      this._verifyThreadLock = new Object();
      this._threadWaitingLock = new Object();
      SecureEmailListener secureEmailListener = SecureEmailListener.getInstance();
      if (replacedField != null) {
         secureEmailListener.deregisterCollectionListener(this._messageModel, replacedField);
      }

      secureEmailListener.registerCollectionListener(this._messageModel, this);
   }

   public SecureEmailSignatureField(
      SecureEmailFactory secureEmailFactory,
      ServiceRecord serviceRecord,
      boolean inbound,
      boolean moreAvailable,
      long creationDate,
      String senderEmailAddress,
      boolean isPINMessage,
      int besVerificationState,
      byte[] besSignerCertificateHash,
      String besNoVerifyReason,
      Manager manager,
      Object context
   ) {
      this(
         secureEmailFactory,
         serviceRecord,
         inbound,
         moreAvailable,
         creationDate,
         senderEmailAddress,
         isPINMessage,
         besVerificationState,
         besSignerCertificateHash,
         besNoVerifyReason,
         manager,
         context,
         null
      );
   }
}
