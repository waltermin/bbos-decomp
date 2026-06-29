package net.rim.device.apps.internal.secureemail.sendmethods;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.CryptoException;
import net.rim.device.api.crypto.CryptoIOException;
import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.CryptoTokenAccessDeniedException;
import net.rim.device.api.crypto.CryptoTokenCancelException;
import net.rim.device.api.crypto.PrivateKey;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChainProperties;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.status.CertificateStatusProvider;
import net.rim.device.api.crypto.certificate.status.CertificateStatusRequest;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreDataTicket;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.mime.MIMEOutputStream;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.messaging.EmailBodyProvider;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.internal.blackberryemail.body.EmailBodyModelImpl;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModel;
import net.rim.device.apps.internal.blackberryemail.email.ProxyModel;
import net.rim.device.apps.internal.blackberryemail.email.emailsetting.EmailSettingCollectionImpl;
import net.rim.device.apps.internal.blackberryemail.email.emailsetting.EmailSettingModelImpl;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.blackberryemail.unknown.UnknownMimePartModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.secureemail.AbortSendSecureEmailException;
import net.rim.device.apps.internal.secureemail.ContentCiphers;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.RecipientData$CertificateDetails;
import net.rim.device.apps.internal.secureemail.SecureEmailBodyModel;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;
import net.rim.device.apps.internal.secureemail.SecureEmailOptions;
import net.rim.device.apps.internal.secureemail.SecureEmailResources;
import net.rim.device.apps.internal.secureemail.SecureEmailUtilities;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerManager;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;
import net.rim.vm.Array;

public class SecureEmailMessageEncoder implements MessageEncoder {
   protected EmailMessageModel _message;
   protected ServiceRecord _serviceRecord;
   protected RecipientData[] _messageRecipientData;
   protected Certificate[] _additionalCertificates;
   protected int _encodingAction;
   protected SecureEmailSendDialog _secureEmailSendDialog;
   protected SecureEmailFactory _secureEmailFactory;
   protected SecureEmailUtilities _secureEmailUtilities;
   protected Object _sendContext;
   protected KeyStore _preferredKeyStore;
   protected boolean _isPINMessage;
   protected static final ResourceBundle _rb = ResourceBundle.getBundle(-6165272894895379810L, "net.rim.device.apps.internal.resource.secureemail.SecureEmail");
   private static final int DONT_SEND = 0;
   private static final int SEND_ANYWAY = 1;
   private static final int VIEW_CERT = 2;
   private static final int DONT_ASK = 3;
   private static final int NUM_USER_OPTIONS = 4;

   public boolean isAttachmentPresent(EmailMessageModel message) {
      int numMessageModels = message.size();

      for (int i = 0; i < numMessageModels; i++) {
         RIMModel currentMessageModel = (RIMModel)message.getAt(i);
         if (currentMessageModel instanceof EmailPayloadModel) {
            EmailPayloadModel emailPayloadModel = (EmailPayloadModel)currentMessageModel;
            int numOriginalMessageModels = emailPayloadModel.size();

            for (int j = 0; j < numOriginalMessageModels; j++) {
               Object currentOriginalMessageModel = emailPayloadModel.getAt(j);
               if ((currentOriginalMessageModel instanceof ProxyModel || currentOriginalMessageModel instanceof UnknownMimePartModel)
                  && !(currentOriginalMessageModel instanceof BodyModel)
                  && !(currentOriginalMessageModel instanceof EmailBodyProvider)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean isEmailBodyTruncated(EmailMessageModel message) {
      int numMessageModels = message.size();

      for (int i = 0; i < numMessageModels; i++) {
         RIMModel currentMessageModel = (RIMModel)message.getAt(i);
         if (currentMessageModel instanceof EmailPayloadModel) {
            EmailPayloadModel emailPayloadModel = (EmailPayloadModel)currentMessageModel;
            int numOriginalMessageModels = emailPayloadModel.size();

            for (int j = 0; j < numOriginalMessageModels; j++) {
               Object currentOriginalMessageModel = emailPayloadModel.getAt(j);
               if (!(currentOriginalMessageModel instanceof SecureEmailBodyModel)) {
                  if (currentOriginalMessageModel instanceof EmailBodyModelImpl) {
                     EmailBodyModelImpl bodyModel = (EmailBodyModelImpl)currentOriginalMessageModel;
                     if (bodyModel.isMoreAvailable() || bodyModel.isTruncated()) {
                        return true;
                     }
                  }
               } else {
                  SecureEmailBodyModel bodyModel = (SecureEmailBodyModel)currentOriginalMessageModel;
                  if (bodyModel.isMoreAvailable() || bodyModel.isTruncated()) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   protected void showTruncatedWarning(EmailMessageModel message) {
      SecureEmailOptions secureEmailOptions = this.getPerMessageOptions();
      if (secureEmailOptions == null) {
         secureEmailOptions = (SecureEmailOptions)this._secureEmailFactory.createGlobalOptionsCopy();
      }

      if (secureEmailOptions.getPromptTruncatedMessage() && this.isEmailBodyTruncated(message)) {
         String[] choices = _rb.getStringArray(181);
         int choice = BackgroundDialog.getChoice(_rb.getString(182), choices, 0);
         if (choice != 1) {
            if (choice == 2) {
               secureEmailOptions.setPromptTruncatedMessage(false);
               this._secureEmailFactory.saveGlobalOptions(secureEmailOptions);
            } else {
               throw new AbortSendSecureEmailException();
            }
         }
      }
   }

   protected void showIgnoredAttachmentWarning(EmailMessageModel message) {
      SecureEmailOptions secureEmailOptions = this.getPerMessageOptions();
      if (secureEmailOptions == null) {
         secureEmailOptions = (SecureEmailOptions)this._secureEmailFactory.createGlobalOptionsCopy();
      }

      if (secureEmailOptions.getPromptTruncatedMessage() && this.isAttachmentPresent(message)) {
         String[] choices = _rb.getStringArray(184);
         int choice = BackgroundDialog.getChoice(_rb.getString(185), choices, 0);
         if (choice != 1) {
            if (choice == 2) {
               secureEmailOptions.setPromptTruncatedMessage(false);
               this._secureEmailFactory.saveGlobalOptions(secureEmailOptions);
            } else {
               throw new AbortSendSecureEmailException();
            }
         }
      }
   }

   protected void showCertificateWarning(KeyStoreData keyStoreData, int encodingActions, SecureEmailOptions secureEmailOptions) {
      if (secureEmailOptions.getPromptProblemPersonalCerts()) {
         Certificate certificate = keyStoreData.getCertificate();
         Certificate[][] certificateChains = CertificateUtilities.buildCertificateChains(certificate, this._preferredKeyStore);
         long[] certificateChainProperties = CertificateChainProperties.getCertificateChainProperties(
            certificateChains,
            this._preferredKeyStore,
            TrustedKeyStore.getInstance(),
            System.currentTimeMillis(),
            (CryptoSystemProperties)this._secureEmailFactory.getCryptoSystemProperties()
         );
         int bestChainIndex = CertificateChainProperties.selectBestCertificateChain(certificateChainProperties);
         if (!this.isCertificateRecommended(certificate, certificateChains[bestChainIndex], certificateChainProperties[bestChainIndex], encodingActions)) {
            String[] containerStringLowerSingularArray = new String[]{this._secureEmailFactory.getPublicKeyContainerString(false, false)};
            String certificateName = null;
            if (encodingActions == 2) {
               certificateName = MessageFormat.format(SecureEmailResources.getString(173), containerStringLowerSingularArray);
            } else {
               certificateName = MessageFormat.format(SecureEmailResources.getString(174), containerStringLowerSingularArray);
            }

            String message = MessageFormat.format(SecureEmailResources.getString(175), new String[]{certificateName, keyStoreData.getLabel()});
            String[] userOptions = new String[4];
            String[] temp = SecureEmailResources.getStringArray(176);

            for (int i = 0; i < 4; i++) {
               userOptions[i] = temp[i];
            }

            userOptions[2] = MessageFormat.format(userOptions[2], containerStringLowerSingularArray);
            SimpleChoiceDialog scd = new SimpleChoiceDialog(message, userOptions, 0, null, 134217728);

            while (true) {
               BackgroundDialog.show(scd);
               if (scd.getCloseReason() == -1) {
                  throw new AbortSendSecureEmailException();
               }

               switch (scd.getSelectedIndex()) {
                  case -1:
                     break;
                  case 0:
                  default:
                     throw new AbortSendSecureEmailException();
                  case 1:
                     return;
                  case 2:
                     CertificateUtilities.displayCertificateDetails(
                        certificate, null, this._preferredKeyStore, (CryptoSystemProperties)this._secureEmailFactory.getCryptoSystemProperties(), true, null
                     );
                     break;
                  case 3:
                     secureEmailOptions.setPromptProblemPersonalCerts(false);
                     this._secureEmailFactory.saveGlobalOptions(secureEmailOptions);
                     return;
               }
            }
         }
      }
   }

   protected boolean handleThrowable(Throwable t) {
      if (t instanceof AbortSendSecureEmailException) {
         return false;
      }

      if (t instanceof CryptoIOException) {
         CryptoIOException cioe = (CryptoIOException)t;
         CryptoException ce = cioe.getCryptoException();
         if (ce instanceof CryptoTokenCancelException || ce instanceof CryptoTokenAccessDeniedException) {
            return false;
         }

         t = cioe.getCryptoException();
      }

      String throwableString = t.toString();
      Dialog.alert(SecureEmailResources.getString(8) + " (" + throwableString + ")");
      EventLogger.logEvent(this._secureEmailFactory.getEventLoggerGUID(), throwableString.getBytes());
      return false;
   }

   protected void setGlobalOptionsDefaults(SecureEmailOptions _1) {
      throw null;
   }

   protected OutputStream createOutputStream(OutputStream _1, SecureEmailOptions _2) {
      throw null;
   }

   protected void writeDataToOutputStream(OutputStream _1, StringBuffer _2, Object[] _3) {
      throw null;
   }

   public void doEncodeWork() {
      SecureEmailOptions secureEmailOptions = this.getPerMessageOptions();
      if (secureEmailOptions == null) {
         secureEmailOptions = (SecureEmailOptions)this._secureEmailFactory.createGlobalOptionsCopy();
         this.setGlobalOptionsDefaults(secureEmailOptions);
      }

      ByteArrayOutputStream innermostOutputStream = new ByteArrayOutputStream();
      OutputStream outermostOutputStream = this.createOutputStream(innermostOutputStream, secureEmailOptions);
      if (outermostOutputStream == null) {
         throw new AbortSendSecureEmailException();
      }

      this.updateSigningAndEncryptingMessage();
      Vector modelsToEncode = new Vector();
      StringBuffer bufferToEncode = new StringBuffer();
      String autoSignature = null;
      if (!this._isPINMessage) {
         ServiceRecord serviceRecord = ServiceBook.getSB().getRecordByUidAndCid(this._serviceRecord.getUid(), "SYNC");
         if (serviceRecord != null) {
            String userId = String.valueOf(serviceRecord.getUserId());
            EmailSettingModelImpl model = (EmailSettingModelImpl)EmailSettingCollectionImpl.getInstance(userId).getAt(0);
            if (model != null && model.getAutoSignatureEnabled()) {
               autoSignature = model.getAutoSignature();
            }
         }
      }

      SecureEmailUtilities.getDataToEncodeFromMessage(this._message, modelsToEncode, bufferToEncode, this._sendContext, autoSignature);
      if (bufferToEncode.length() > 32768) {
         bufferToEncode.setLength(32768);
         bufferToEncode.append('\n');
         bufferToEncode.append(EmailResources.getString(1008));
         bufferToEncode.append('\n');
      }

      PersistentContent.markAsPlaintext(bufferToEncode);
      Object[] attachments = SecureEmailUtilities.extractSecureEmailAttachments(this._message);
      this.writeDataToOutputStream(outermostOutputStream, bufferToEncode, attachments);
      SecureEmailBodyModel bodyModel = (SecureEmailBodyModel)this._secureEmailFactory.createBodyModel(innermostOutputStream.toByteArray());
      this._message.add(bodyModel);
      this.modifyRecipientList();
      Enumeration modelsToRemove = modelsToEncode.elements();

      while (modelsToRemove.hasMoreElements()) {
         this._message.remove((RIMModel)modelsToRemove.nextElement());
      }
   }

   protected PrivateKey locateMySigningPrivateKey(KeyStoreData signingKeyStoreData, SecureEmailOptions options, String ticketPrompt) {
      if (signingKeyStoreData == null) {
         String[] messageFormatArguments = new String[]{
            this._secureEmailFactory.getPublicKeyContainerString(false, false), this._secureEmailFactory.getEncodingString()
         };
         String noSigningString = MessageFormat.format(SecureEmailResources.getString(163), messageFormatArguments);
         int noSigningCertOptions = 1;
         if (this.getPerMessageOptions() == null) {
            noSigningCertOptions |= 512;
         }

         ProblemRecipientData noSigningCertProblem = new ProblemRecipientData(noSigningString, noSigningCertOptions, this._secureEmailFactory);
         noSigningCertProblem.promptUser(0, 0, this._sendContext);
      }

      KeyStoreDataTicket keyTicket = signingKeyStoreData.getTicket(ticketPrompt);
      PrivateKey privateKey = signingKeyStoreData.getPrivateKey(keyTicket);
      privateKey.verify();
      return privateKey;
   }

   protected SecureEmailOptions getPerMessageOptions() {
      return (SecureEmailOptions)SubmemberUtilities.getFirstSubmember(this._message, this._secureEmailFactory.getOptionsRecognizer());
   }

   protected RecipientData[] getMessageRecipientData() {
      return this._messageRecipientData;
   }

   protected Certificate[] getAdditionalCertificates() {
      return this._additionalCertificates;
   }

   protected void updateSigningAndEncryptingMessage() {
      String pleaseWaitMessage = null;
      if ((this._encodingAction & 1) != 0) {
         if ((this._encodingAction & 2) != 0) {
            pleaseWaitMessage = SecureEmailResources.getString(17);
         } else {
            pleaseWaitMessage = SecureEmailResources.getString(16);
         }
      } else {
         if ((this._encodingAction & 2) == 0) {
            return;
         }

         pleaseWaitMessage = SecureEmailResources.getString(14);
      }

      this._secureEmailSendDialog.setMessage(pleaseWaitMessage);
   }

   protected void modifyRecipientList() {
      if (!this._isPINMessage) {
         this._secureEmailUtilities.addAlwaysBCCEmailAddress(this._message, this._serviceRecord.getUid());
      }
   }

   protected int selectContentCipher(SecureEmailOptions secureEmailOptions) {
      int allowedContentCiphers = secureEmailOptions.getContentCipherBitfield();
      int recipientContentCiphers = 511;
      if (!this._isPINMessage) {
         Object[] headerModels = SubmemberUtilities.getSubmembers(this._message, new SecureEmailMessageEncoder$HeaderModelRecognizer());
         int numHeaderModels = headerModels.length;

         for (int i = 0; i < numHeaderModels; i++) {
            EmailHeaderModel emailHeaderModel = (EmailHeaderModel)headerModels[i];
            RecipientData[] recipientData = SecureEmailUtilities.getRecipientData(emailHeaderModel, false);
            if (recipientData != null) {
               int numRecipientData = recipientData.length;

               for (int j = 0; j < numRecipientData; j++) {
                  String[] emailAddresses = recipientData[j].getAddresses();
                  int numEmailAddresses = emailAddresses.length;

                  for (int k = 0; k < numEmailAddresses; k++) {
                     recipientContentCiphers &= this._secureEmailUtilities.getRecipientContentCiphers(emailAddresses[k]);
                  }
               }
            }
         }
      }

      RecipientData[] messageRecipientData = this.getMessageRecipientData();
      int numMessageRecipientData = messageRecipientData != null ? messageRecipientData.length : 0;

      for (int i = 0; i < numMessageRecipientData; i++) {
         RecipientData$CertificateDetails[] currentCertificateDetails = this._messageRecipientData[i].getSelectedCertificates();
         int numCurrentCertificateDetails = currentCertificateDetails != null ? currentCertificateDetails.length : 0;

         for (int j = 0; j < numCurrentCertificateDetails; j++) {
            recipientContentCiphers &= this._secureEmailUtilities.getCertificateContentCiphers(currentCertificateDetails[j].getCertificate());
         }
      }

      int availableContentCiphers = allowedContentCiphers & recipientContentCiphers;
      if (availableContentCiphers != 0) {
         return (availableContentCiphers ^ availableContentCiphers - 1) & availableContentCiphers;
      }

      String[] cipherLabels = ContentCiphers.getDialogLabels();
      String[] choices = new String[10];
      choices[0] = SecureEmailResources.getStringArray(153)[0];
      int userSelectableContentCiphers = recipientContentCiphers & this._secureEmailUtilities.getITPolicyContentCiphers();
      int choiceIndex = 1;

      for (int i = 0; i < 9; i++) {
         int currentBit = 1 << i;
         if ((userSelectableContentCiphers & currentBit) != 0) {
            choices[choiceIndex++] = cipherLabels[i];
         }
      }

      Array.resize(choices, choiceIndex);
      String dialogMessage = SecureEmailResources.getString(91);
      if (choiceIndex > 1) {
         dialogMessage = dialogMessage + SecureEmailResources.getString(92);
      }

      int selectedIndex = BackgroundDialog.getChoice(dialogMessage, choices, 0);
      if (selectedIndex != -1 && selectedIndex != 0) {
         int numSetBits = 0;

         for (int i = 0; i < 9; i++) {
            int currentBit = 1 << i;
            if ((userSelectableContentCiphers & currentBit) != 0) {
               if (++numSetBits == selectedIndex) {
                  return currentBit;
               }
            }
         }

         return 32;
      } else {
         throw new AbortSendSecureEmailException();
      }
   }

   protected void addAttachments(MIMEOutputStream outputStream, Object[] attachments) {
      for (Object model : attachments) {
         if (model instanceof ConversionProvider) {
            this._message.remove(model);
            ConversionProvider converter = (ConversionProvider)model;
            ContextObject contextObject = new ContextObject();
            contextObject.setFlag(43, 54);
            converter.convert(contextObject, outputStream);
         }
      }
   }

   @Override
   public long getEncodingUID() {
      return this._secureEmailFactory.getEncodingUID();
   }

   @Override
   public int getEncodingAction() {
      return this._encodingAction;
   }

   @Override
   public Object getSendContext() {
      return this._sendContext;
   }

   @Override
   public boolean encodeMessage() {
      SecureEmailMessageEncoder$EncodeWorkerThread encodeWorkerThread = new SecureEmailMessageEncoder$EncodeWorkerThread(this, null);
      this._secureEmailSendDialog = new SecureEmailSendDialog(encodeWorkerThread);
      this._secureEmailSendDialog.display();
      Throwable t = this._secureEmailSendDialog.getThrowable();
      return t != null ? this.handleThrowable(t) : true;
   }

   protected SecureEmailMessageEncoder(
      EmailMessageModel message,
      RecipientData[] messageRecipientData,
      Certificate[] additionalCertificates,
      ServiceRecord serviceRecord,
      int encodingAction,
      SecureEmailFactory secureEmailFactory,
      Object context
   ) {
      this._message = message;
      this._messageRecipientData = messageRecipientData;
      this._additionalCertificates = additionalCertificates;
      this._serviceRecord = serviceRecord;
      this._encodingAction = encodingAction;
      this._secureEmailFactory = secureEmailFactory;
      this._secureEmailUtilities = this._secureEmailFactory.getUtilities();
      this._preferredKeyStore = this._secureEmailFactory.getPreferredKeyStore();
      this._isPINMessage = this._message.flagsSet(8192);
      this._sendContext = ContextObject.clone(context);
      ContextObject.setFlag(this._sendContext, 70);
      this.showTruncatedWarning(message);
      this.showIgnoredAttachmentWarning(message);
   }

   private boolean isCertificateRecommended(Certificate certificate, Certificate[] bestChain, long bestProperties, int encodingActions) {
      boolean result = true;
      int operationType;
      if (encodingActions == 2) {
         operationType = 2;
      } else {
         operationType = 4;
      }

      if (!this._secureEmailUtilities.isCertificateRecommended(certificate, bestProperties, operationType, true)) {
         result = false;
      } else if (this._secureEmailUtilities.isCertificateStale(bestProperties)
         && SecureEmailServerManager.getInstance().getCertificateServers(this._serviceRecord).length == 0) {
         CertificateStatusRequest request = new CertificateStatusRequest(bestChain, true, this._preferredKeyStore, null, null);
         result = CertificateStatusProvider.requestCertificateStatusInternal(request, null);
      }

      return result;
   }
}
