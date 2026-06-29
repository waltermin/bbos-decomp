package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.notification.NotificationsEngineListener;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.api.messaging.implus.IMPlusServiceModel;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceListener;
import net.rim.device.apps.api.transmission.TransmissionStatusListener;
import net.rim.device.apps.api.transmission.rim.CMIMEConstants;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.CancelMoreMessagingTransmission;
import net.rim.device.apps.api.transmission.rim.MoreMessageCompleteTransmission;
import net.rim.device.apps.api.transmission.rim.RIMMessagingDeliveryToAddress;
import net.rim.device.apps.api.transmission.rim.RIMMessagingFolderManagement;
import net.rim.device.apps.api.transmission.rim.RIMMessagingIncomingMessage;
import net.rim.device.apps.api.transmission.rim.RIMMessagingIncomingMoreRequest;
import net.rim.device.apps.api.transmission.rim.RIMMessagingMessageError;
import net.rim.device.apps.api.transmission.rim.RIMMessagingMoreMessage;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailModifier;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMoreVerb;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.otasync.OTAMessageSync;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.cldc.io.gme.GMEAddress;
import net.rim.device.cldc.io.gme.GMEDatagram;
import net.rim.device.cldc.io.gme.GMETarget;
import net.rim.device.internal.firewall.Firewall;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.UiInternal;
import net.rim.vm.Memory;

final class EmailTransmissionServiceListener
   implements TransmissionServiceListener,
   TransmissionStatusListener,
   EmailEventLoggerEvents,
   NotificationsEngineListener {
   private static EmailTransmissionServiceListener _instance;
   private static final long EMAIL_TRANSMISSION_SERVICE_LISTENER;
   private static EmailMessageModel _displayedMessage;

   @Override
   public final void updateTransmissionStatus(TransmissionService service, int tag, int code, Object context) {
      EventLogger.logEvent(-1237457833540244999L, 1431589699, 5);
      EmailMessageModel messageModel = (EmailMessageModel)MessageLookups.get(431630751329425149L, tag);
      if (messageModel == null) {
         if (code == 0) {
            UiInternal.setRadioIconsVisible(true);
         }

         EventLogger.logEvent(-1237457833540244999L, 1430736454, 5);
      } else if (!messageModel.inbound()) {
         int newStatus = 0;
         switch (code) {
            case -1:
               EventLogger.logEvent(-1237457833540244999L, 1431590227, 3);
               newStatus = 8191;
               break;
            case 0:
            default:
               newStatus = 33554431;
               break;
            case 1:
               newStatus = 134217727;
               break;
            case 2:
               newStatus = 67108863;
               break;
            case 3:
               newStatus = 268435455;
               break;
            case 4:
               newStatus = 1073741823;
               break;
            case 5:
               newStatus = 4194303;
               break;
            case 6:
               newStatus = 2097151;
               break;
            case 7:
               newStatus = 8388607;
         }

         String errorMessage = null;
         if (context instanceof Object) {
            errorMessage = (String)context;
         }

         if (newStatus == 8191) {
            if (this.autoResend(messageModel, code)) {
               newStatus = 32767;
            } else if (messageModel.autoResendAttempts() > 0) {
               String resendFailedString = EmailResources.getString(205);
               if (errorMessage == null) {
                  errorMessage = resendFailedString;
               } else {
                  errorMessage = ((StringBuffer)(new Object())).append(errorMessage).append(". ").append(resendFailedString).toString();
               }
            }
         }

         if (newStatus == 8191 || newStatus == 1) {
            if (errorMessage != null) {
               errorMessage = Memory.stringIntern(errorMessage);
               messageModel.setTransmissionErrorMessage(errorMessage);
               PersistentObject.commit(messageModel);
            }

            EventLogger.logEvent(-1237457833540244999L, 1414418260, 5);
            EmailMessageUtilities.triggerNotifications(messageModel, false, true, 0, null);
         }

         messageModel.changeStatus(0, 0, newStatus, code, true, true, false, false, context);
      }
   }

   protected final boolean receiveIncomingMessage(TransmissionService service, RIMMessagingIncomingMessage incomingTransmission, Object context) {
      EventLogger.logEvent(-1237457833540244999L, 538989133, 5);
      Factory factory = null;
      ContextObject initialData = ContextObject.clone(context);
      initialData.setFlag(38);
      boolean isPin = initialData.getFlag(94);
      IMPlusServiceModel implusService = (IMPlusServiceModel)ApplicationRegistry.getApplicationRegistry().get(-2205884509140292945L);
      int attachmentCount = incomingTransmission.getAttachmentCount();
      if (implusService != null) {
         for (int index = attachmentCount - 1; index >= 0; index--) {
            Object attachment = incomingTransmission.getAttachment(index);
            String attachmentType = incomingTransmission.getAttachmentType(index);
            if (attachmentType != null && attachmentType.compareTo("message/delivery-status") == 0) {
               implusService.receiveReceipt(attachment);
               return true;
            }
         }
      }

      EmailMessageModel message = null;
      ServiceRecord serviceRecord = (ServiceRecord)ContextObject.get(context, -6095803566992128485L);
      EmailHierarchy emailHierarchy = getEmailHierarchy(serviceRecord, context);
      int cmimeReferenceId = incomingTransmission.getReferenceIdentifier();
      if (isPin) {
         Firewall fw = Firewall.getInstance();
         if (initialData.getFlag(109)) {
            if (fw.isBlockingEnabled((byte)4)) {
               fw.incrementBlockedCount((byte)4);
               return true;
            }
         } else if (fw.isBlockingEnabled((byte)5)) {
            fw.incrementBlockedCount((byte)5);
            return true;
         }
      } else if (!initialData.getFlag(126)
         && (serviceRecord != null && !ITPolicyInternal.verifyITAdminService(serviceRecord.getUid(), false) || initialData.getFlag(135))) {
         Firewall fw = Firewall.getInstance();
         if (fw.isBlockingEnabled((byte)3)) {
            fw.incrementBlockedCount((byte)3);
            return true;
         }
      }

      short cmimeFolderId = (short)incomingTransmission.getFolderIdentifier();
      if (cmimeFolderId > 0) {
         cmimeFolderId = 0;
      }

      EmailMessageModel existingMessage = (EmailMessageModel)emailHierarchy.getMessage(cmimeReferenceId);
      if (existingMessage != null) {
         if (existingMessage.inbound() && !incomingTransmission.isTransmitted()) {
            return true;
         }

         if (!existingMessage.inbound() && incomingTransmission.isTransmitted()) {
            return true;
         }

         existingMessage = null;
      }

      Object o;
      if (existingMessage != null) {
         o = new EmailMessageModelImpl(initialData, true, true);
      } else {
         o = this.createEmailMessageModelFromIncomingMessage(incomingTransmission, initialData);
      }

      if (!(o instanceof EmailMessageModel)) {
         return ContextObject.getFlag(o, 39);
      }

      message = (EmailMessageModel)o;
      Parameters parameters = incomingTransmission.getTextParameters();
      if (parameters != null) {
         byte[] securityEncoding = parameters.getFirst((byte)-11);
         if (securityEncoding != null) {
            message.setIsNNE(Arrays.equals(securityEncoding, CMIMEConstants.SECURITY_ENCODING_NNE_BYTES));
         }
      }

      if (initialData.getFlag(110)) {
         message.setFlags(32768);
      }

      if (initialData.getFlag(126)) {
         message.setFlags(16777216);
      }

      if (initialData.getFlag(109)) {
         message.setFlags(1048576);
      }

      message.setSensitivity(incomingTransmission.getSensitivity());
      message.setPriority(incomingTransmission.getImportance());
      message.setNotificationLevel(incomingTransmission.getNotificationLevel());

      label732:
      try {
         EmailBuilder.createTextObjectForString((String)incomingTransmission.getSubject(), 3928489455534245796L, initialData, message);
      } finally {
         break label732;
      }

      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      message.setTimestamp(incomingTransmission.getDate());
      factory = (Factory)ar.waitFor(-8034039608019345282L);
      String addressToMatch = null;
      if (isPin) {
         addressToMatch = EmailSendUtility.getDevicePINString();
      }

      boolean devicePINIsInTO = EmailBuilder.createHeaderForStringPairs(incomingTransmission.getTo(), 0, factory, initialData, message, addressToMatch);
      boolean devicePINIsInCC = EmailBuilder.createHeaderForStringPairs(incomingTransmission.getCc(), 1, factory, initialData, message, addressToMatch);
      EmailBuilder.createHeaderForStringPairs(incomingTransmission.getBcc(), 2, factory, initialData, message, addressToMatch);
      byte recipientType = incomingTransmission.getRecipientType();
      if (isPin && !devicePINIsInTO) {
         if (devicePINIsInCC) {
            recipientType = 2;
         } else {
            recipientType = 4;
         }
      }

      message.setRecipientType(recipientType);
      boolean isPage = incomingTransmission.getMessageIconCharacter() == '\uf3f1';

      label725:
      try {
         String subject = (String)incomingTransmission.getSubject();
         if ((!InternalServices.isDeviceSecure() || Branding.getData(16) != null) && subject != null && subject.startsWith("Page: ")) {
            isPage = true;
         }
      } finally {
         break label725;
      }

      if (isPage) {
         message.setFlags(524288);
         PagingSupport.enablePagingSupport();
      }

      EmailBuilder.createHeaderForStringPairs(incomingTransmission.getFrom(), 3, factory, initialData, message, null);
      EmailBuilder.createHeaderForStringPairs(incomingTransmission.getReplyTo(), 5, factory, initialData, message, null);
      String[] sender = incomingTransmission.getSender();
      if (sender != null) {
         String[][][] arrayOfStringPairs = new Object[][][]{sender};
         EmailBuilder.createHeaderForStringPairs(arrayOfStringPairs, 4, factory, initialData, message, null);
      } else {
         EventLogger.logEvent(-1237457833540244999L, 542264915, 5);
      }

      Object obj = incomingTransmission.getText();
      int bodyLength = 0;
      if (obj == null) {
         EventLogger.logEvent(-1237457833540244999L, 542264898, 5);
      } else {
         Object body = null;
         if (!(obj instanceof Object)) {
            body = obj;
            if (body instanceof Object) {
               message.add(obj);
            } else {
               EventLogger.logEvent(-1237457833540244999L, 542264898, 5);
            }
         } else {
            String bodyText = (String)obj;
            body = EmailBuilder.createTextObjectForString(bodyText, 5987399499453925075L, initialData, message);
         }

         if (!(body instanceof MorePartModel)) {
            EventLogger.logEvent(-1237457833540244999L, 542264898, 5);
         } else {
            MorePartModel morePart = (MorePartModel)body;
            bodyLength = morePart.getLengthOnDevice();
            int reportedBodyLength = incomingTransmission.getBodyLength();
            if (bodyLength == 0 && reportedBodyLength != -1) {
               morePart.setLengthOnDevice(-1);
            } else {
               if (parameters != null) {
                  bodyLength = CMIMEUtilities.decodeInteger(parameters, (byte)-7);
               }

               morePart.setLengthOnDevice(bodyLength != -1 ? bodyLength : reportedBodyLength);
            }

            morePart.setTrueLength(CMIMEUtilities.decodeInteger(parameters, (byte)-13));
            morePart.setAvailableLength(CMIMEUtilities.decodeInteger(parameters, (byte)-16));
            morePart.setMorePartID(CMIMEUtilities.decodeInteger(parameters, (byte)-15));
            if (morePart.isTruncated()) {
               message.setFlags(4096);
            }

            if (morePart.isMoreAvailable()) {
               message.setFlags(256);
            }
         }
      }

      message.setCMIMEReferenceIdentifier(cmimeReferenceId);
      if (incomingTransmission.isReplyProhibited()) {
         message.clearFlags(32);
      } else {
         message.setFlags(32);
      }

      if (serviceRecord == null && parameters != null) {
         DataBuffer dataBuffer = parameters.getDataBuffer();
         if (dataBuffer instanceof Object) {
            GMEDatagram gmeDatagram = (GMEDatagram)dataBuffer;
            GMEAddress gmeAddress = gmeDatagram.getGMEAddress();
            GMETarget gmeTarget = gmeAddress.getSrc();
            if (gmeTarget != null && gmeTarget.address != null) {
               serviceRecord = (ServiceRecord)(new Object());
               serviceRecord.setUid(gmeTarget.address);
            }
         }
      }

      long messageFolder = emailHierarchy.getEmailFolder(cmimeFolderId);
      message.setFolderId(messageFolder);
      if (incomingTransmission.isSynchronized() && this.SavedDuplicateExists(message, messageFolder)) {
         return false;
      }

      Object attachment = null;
      int actualAttachmentCount = 0;

      for (int index = 0; index < attachmentCount; index++) {
         attachment = incomingTransmission.getAttachment(index);
         if (isPin && attachment instanceof Object) {
            EmailBuilder.createTextObjectForString((String)attachment, 5987399499453925075L, initialData, message);
         } else {
            if (attachment instanceof CMIMEReferenceIdInterested) {
               ((CMIMEReferenceIdInterested)attachment).setCMIMEReferenceIdentifier(cmimeReferenceId, serviceRecord != null ? serviceRecord.getUserId() : -1);
            }

            if (attachment instanceof Object) {
               message.add(attachment);
               actualAttachmentCount++;
            } else {
               EventLogger.logEvent(-1237457833540244999L, 1380011600, 5);
            }
         }
      }

      message.setAttachmentCount(actualAttachmentCount);
      EmailPayloadModel payload = message.getPayload();
      payload.setCopyInsteadOfReference(incomingTransmission.getOriginalTextRequestSettings() == 1);
      if (incomingTransmission.isTransmitted()) {
         message.setType((byte)0);
         message.setInbound(false);
         message.changeStatus(1, 0, 33554431, 0, false, false, false, false, context);
      } else if ((message.getFlags() & 1) != 0) {
         if (EmailHierarchy.isInPersonalFolder(message)) {
            message.changeStatus(2, 0, 0, 0, false, false, false, false, context);
         } else if (!EmailHierarchy.isInInboxOrSentItemsFolder(message)) {
            emailHierarchy.updateMissingFolderTimestamp(message.getTimestamp());
         }
      }

      if (incomingTransmission.isSynchronized()) {
         payload.setCreationDate(message.getTimestamp());
      }

      message.setEncoding(incomingTransmission.getEncoding());
      if (existingMessage != null) {
         long oldFolderId = existingMessage.getFolderId();
         boolean needToMoveMessage = messageFolder != 0 && oldFolderId != messageFolder;
         message = existingMessage;
         if (ModelViewListenerRegistry.isViewerUp(0, message, null)) {
            ModelViewListenerRegistry.notifyOfOpenedModelChange(message, message, context);
         }

         if (needToMoveMessage) {
            EmailHierarchy.fileMessage(message, oldFolderId, messageFolder);
         }
      } else if (messageFolder != 0) {
         boolean dropMessage = !EmailHierarchy.addMessage(message, messageFolder);
         if (dropMessage) {
            EventLogger.logEvent(-1237457833540244999L, 1296323145, 0);
            return true;
         }
      } else {
         EventLogger.logEvent(-1237457833540244999L, 1380861510, 2);
      }

      if (implusService != null) {
         byte dsnRequest = incomingTransmission.getAcknowledgementRequestSettings();
         if (dsnRequest != 0) {
            int cmimeOriginalReferenceId = incomingTransmission.getOriginalReferenceIdentifier();
            if (cmimeOriginalReferenceId != 0) {
               MessageLookups.put(4530015158237739359L, cmimeReferenceId, new Object(cmimeOriginalReferenceId));
            }

            if ((dsnRequest & 4) != 0) {
               String[][][] originalRecipient = incomingTransmission.getOriginalRecipient();
               if (originalRecipient != null) {
                  boolean foundMatchingHeader = false;

                  for (int i = 0; i < message.size(); i++) {
                     RIMModel model = (RIMModel)message.getAt(i);
                     if (model instanceof EmailHeaderModel) {
                        EmailHeaderModel headerModel = (EmailHeaderModel)model;
                        int headerType = headerModel.getHeaderType();
                        if (headerType == 0 || headerType == 1 || headerType == 2) {
                           String[] nameStrings = new Object[2];
                           headerModel.extractNames(nameStrings);
                           if (StringUtilities.compareToIgnoreCase(originalRecipient[0][0], nameStrings[0], 1701707776) == 0) {
                              headerModel.setFlags((byte)8);
                              foundMatchingHeader = true;
                              break;
                           }
                        }
                     }
                  }

                  if (!foundMatchingHeader) {
                     EmailBuilder.createHeaderForStringPairs(originalRecipient, 6, factory, initialData, message, null);
                  }
               }
            }

            if ((dsnRequest & 2) != 0) {
               message.setFlags(64);
            }

            if ((dsnRequest & 1) != 0) {
               implusService.sendReceipt(message, false);
            }
         }

         if (serviceRecord != null && serviceRecord == implusService.getIMPlusServiceRecord()) {
            message.setFlags(8388608);
         }
      }

      EmailModifier.endChanges(message, null, initialData);
      MessageLookups.put(-4420850319371185992L, cmimeReferenceId, message);
      LowMemoryManager.poll();
      if (!incomingTransmission.isMarkedAsRead() && !incomingTransmission.isNotificationDisabled() && existingMessage == null) {
         EventLogger.logEvent(-1237457833540244999L, 1414418260, 5);
         EmailMessageUtilities.triggerNotifications(message, false, 0, null);
      }

      return true;
   }

   protected final Object createEmailMessageModelFromIncomingMessage(RIMMessagingIncomingMessage incomingTransmission, Object initialData) {
      boolean isMarkedAsRead = incomingTransmission.isTransmitted() ? true : incomingTransmission.isMarkedAsRead();
      return new EmailMessageModelImpl(initialData, isMarkedAsRead, !isMarkedAsRead);
   }

   @Override
   public final void proceedWithDeferredEvent(long sourceIdLong, long eventIdLong, Object eventReferenceObject, Object context) {
      long parentSourceIdLong = NotificationsManager.getParentSourceID(sourceIdLong);
      if (sourceIdLong == -1845850106795451018L
         || parentSourceIdLong == -1845850106795451018L
         || sourceIdLong == -327746170160875990L
         || sourceIdLong == 6432934947797527350L) {
         EventLogger.logEvent(-1237457833540244999L, 1347896389, 5);
         if (!Phone.getInstance().isActive() && eventReferenceObject instanceof EmailMessageModel) {
            _displayedMessage = (EmailMessageModel)eventReferenceObject;
            ContextObject contextObject = (ContextObject)(new Object(64));
            ContextObject.put(contextObject, 250, _displayedMessage);
            ShowMessageApp.displayMessage(_displayedMessage, contextObject);
         }
      }
   }

   @Override
   public final void statusChanged(TransmissionService aTransmissionService, int statusInt, Object contextObject) {
      EventLogger.logEvent(-1237457833540244999L, 1414742856, 5);
      if ((statusInt & 1) != 0) {
      }
   }

   @Override
   public final boolean receiveObject(TransmissionService service, Object anObject, Object contextObject) {
      synchronized (RIMPersistentStore.getSynchObject()) {
         boolean var10000;
         synchronized (FolderHierarchies.getLockObject()) {
            EventLogger.logEvent(-1237457833540244999L, 538989135, 5);
            if (anObject instanceof Object) {
               return this.receiveIncomingMessage(service, (RIMMessagingIncomingMessage)anObject, contextObject);
            }

            if (anObject instanceof Object) {
               return this.receiveMoreMessage(service, (RIMMessagingMoreMessage)anObject, contextObject);
            }

            if (anObject instanceof Object) {
               return this.receiveMessageError(service, (RIMMessagingMessageError)anObject, contextObject);
            }

            if (anObject instanceof Object) {
               return this.receiveDeliveryToAddress(service, (RIMMessagingDeliveryToAddress)anObject, contextObject);
            }

            if (anObject instanceof Object) {
               return OTAMessageSync.getInstance().receiveFolderManagementCommand(service, (RIMMessagingFolderManagement)anObject, contextObject);
            }

            if (anObject instanceof Object) {
               NativeAttachmentRequest nativeAttachmentRequest = NativeAttachmentRequest.createNativeAttachmentRequest(
                  (RIMMessagingIncomingMoreRequest)anObject, contextObject
               );
               return NativeAttachmentRequestProcessor.getInstance().addRequest(nativeAttachmentRequest);
            }

            if (anObject instanceof Object) {
               CancelMoreMessagingTransmission tx = (CancelMoreMessagingTransmission)anObject;
               return NativeAttachmentRequestProcessor.getInstance()
                  .cancelRequest(
                     tx.getCMimeRefId(), tx.getContentId(), tx.getErrorCode(), tx.getAbortReasonData(), tx.cancelAttachmentOnly(), tx.retryAttachment()
                  );
            }

            if (anObject instanceof Object) {
               MoreMessageCompleteTransmission tx = (MoreMessageCompleteTransmission)anObject;
               return NativeAttachmentRequestProcessor.getInstance()
                  .moreCompleted(tx.getCMimeRefId(), tx.getContentId(), tx.markMessageComplete(), contextObject);
            }

            var10000 = false;
         }

         return var10000;
      }
   }

   @Override
   public final void notificationsEngineStateChanged(int stateInt, long sourceIdLong, long eventIdLong, Object eventReferenceObject, Object contextObject) {
      if (stateInt == 1 && eventReferenceObject == _displayedMessage && eventReferenceObject != null) {
         _displayedMessage = null;
         ShowMessageApp.postEvent(-6275418955626563374L, 0, 0, eventReferenceObject, null);
         NotificationsManager.cancelDeferredEvent(sourceIdLong, eventIdLong, eventReferenceObject, 0, contextObject);
      }
   }

   @Override
   public final void deferredEventWasSuperseded(long soureIdLong, long eventIdLong, Object eventReferenceObject, Object object) {
   }

   private final boolean receiveMessageError(TransmissionService service, RIMMessagingMessageError errorTransmission, Object context) {
      EventLogger.logEvent(-1237457833540244999L, 538989125, 5);
      int CMIMEIdentifier = errorTransmission.getReferenceIdentifier();
      int error = errorTransmission.getErrorCode();
      boolean preventShortcut = false;
      if (error == 80 || error == 81) {
         ServiceRecord serviceRecord = (ServiceRecord)ContextObject.get(context, -6095803566992128485L);
         if (serviceRecord != null) {
            NNEPasswordManager.clearCachedPassword(serviceRecord);
         }

         preventShortcut = true;
      }

      ServiceRecord serviceRecord = (ServiceRecord)ContextObject.get(context, -6095803566992128485L);
      EmailHierarchy emailHierarchy = getEmailHierarchy(serviceRecord, context);
      EmailMessageModel messageModel = (EmailMessageModel)emailHierarchy.getMessage(CMIMEIdentifier);
      if (messageModel == null) {
         EventLogger.logEvent(-1237457833540244999L, 1380273734, 2);
         return false;
      }

      String errorMessage = (String)errorTransmission.getText();
      if (errorMessage != null && errorMessage.length() > 0) {
         errorMessage = Memory.stringIntern(errorMessage);
         messageModel.setTransmissionErrorMessage(errorMessage);
         PersistentObject.commit(messageModel);
      }

      if (!messageModel.inbound()) {
         EventLogger.logEvent(-1237457833540244999L, 1414418260, 5);
         EmailMessageUtilities.triggerNotifications(messageModel, false, true, 0, null);
      }

      ContextObject contextObject = (ContextObject)(new Object());
      if (messageModel.inbound() && EmailMessageUtilities.moreRequestSent(messageModel)) {
         contextObject.setFlag(98);
      }

      if (messageModel.inbound()) {
         messageModel.changeStatus(0, 0, 1, error, true, true, false, preventShortcut, contextObject);
         return true;
      } else {
         messageModel.changeStatus(0, 0, 8191, error, true, true, false, preventShortcut, contextObject);
         return true;
      }
   }

   private final boolean receiveDeliveryToAddress(TransmissionService service, RIMMessagingDeliveryToAddress deliveryToAddressTransmission, Object context) {
      EventLogger.logEvent(-1237457833540244999L, 542262337, 5);
      int CMIMEIdentifier = deliveryToAddressTransmission.getReferenceIdentifier();
      EmailMessageModel messageModel = (EmailMessageModel)MessageLookups.get(-4420850319371185992L, CMIMEIdentifier);
      if (messageModel == null) {
         EventLogger.logEvent(-1237457833540244999L, 1380208198, 2);
         return false;
      }

      int numRecipients = 0;
      int numDelivered = 0;
      ContextObject changeContext = (ContextObject)(new Object());
      EmailPayloadModel oldPayload = EmailModifier.beginChanges(messageModel, changeContext);
      String[][][] addresses = deliveryToAddressTransmission.getAddresses();
      if (addresses == null) {
         return false;
      }

      for (int j = messageModel.size() - 1; j >= 0; j--) {
         RIMModel model = (RIMModel)messageModel.getAt(j);
         if (model instanceof EmailHeaderModel) {
            EmailHeaderModel headerModel = (EmailHeaderModel)model;
            int headerType = headerModel.getHeaderType();
            if (headerType == 0 || headerType == 1 || headerType == 2 || headerType == 5) {
               String[] nameStrings = new Object[2];
               headerModel.extractNames(nameStrings);
               numRecipients++;
               String addressJ = nameStrings[0];
               IMPlusServiceModel implusService = (IMPlusServiceModel)ApplicationRegistry.getApplicationRegistry().get(-2205884509140292945L);
               if (implusService != null) {
                  addressJ = implusService.stripIMPlusPrefix(addressJ, headerModel.getInsideModel());
               }

               for (int i = addresses.length - 1; i >= 0; i--) {
                  String addressI = addresses[i][0];
                  if (implusService != null) {
                     addressI = implusService.stripIMPlusPrefix(addressI, null);
                  }

                  if (StringUtilities.compareToIgnoreCase(addressI, addressJ, 1701707776) == 0) {
                     headerModel.clearFlags((byte)4);
                     headerModel.setFlags((byte)1);
                     break;
                  }
               }

               if (headerModel.flagsSet(1)) {
                  numDelivered++;
               }
            }
         }
      }

      EmailModifier.endChanges(messageModel, oldPayload, changeContext);
      if (numRecipients != 0 && numDelivered == numRecipients && messageModel.getStatus() != 2097151) {
         messageModel.setStatus(134217727, 0);
         messageModel.changeStatus(0, 0, 4194303, 0, true, true, false, false, context);
      }

      return true;
   }

   protected EmailTransmissionServiceListener() {
   }

   static final synchronized EmailTransmissionServiceListener createInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (EmailTransmissionServiceListener)applicationRegistry.getOrWaitFor(-7341137093681909706L);
         if (_instance == null) {
            _instance = new EmailTransmissionServiceListener();
            applicationRegistry.put(-7341137093681909706L, _instance);
         }
      }

      return _instance;
   }

   private final boolean SavedDuplicateExists(EmailMessageModel email, long folderId) {
      ReadableList collection = (ReadableList)FolderMerge.getMergeCollection(6368823655991217730L);
      synchronized (FolderHierarchies.getLockObject()) {
         int len = collection.size();
         int refId = email.getCMIMEReferenceIdentifier();

         for (int j = 0; j < len; j++) {
            Object obj = collection.getAt(j);
            if (obj instanceof EmailMessageModelImpl) {
               EmailMessageModelImpl currEmail = (EmailMessageModelImpl)obj;
               if (currEmail != email && currEmail.getCMIMEReferenceIdentifier() == refId) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   static final EmailHierarchy getEmailHierarchy(ServiceRecord sr, Object context) {
      return sr != null && !ContextObject.getFlag(context, 94) ? EmailHierarchy.getEmailHierarchy(sr, true) : EmailHierarchy.getAnonymousEmailHierarchy();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean receiveMoreMessage(TransmissionService service, RIMMessagingMoreMessage incomingTransmission, Object context) {
      ContextObject contextObject = ContextObject.clone(context);
      int refId = incomingTransmission.getReferenceIdentifier();
      EmailMessageModelImpl messageModel = (EmailMessageModelImpl)MessageLookups.get(-4420850319371185992L, refId);
      if (messageModel == null) {
         return false;
      }

      LowMemoryManager.poll();
      int partIdentifier = incomingTransmission.getPartIdentifier();
      MorePartModel morePartModel = EmailMoreVerb.findMorePartByIdentifier(messageModel, partIdentifier);
      if (morePartModel == null) {
         return true;
      }

      EmailPayloadModel oldPayload = null;
      int currentOffset = incomingTransmission.getOffset();
      int oldLengthOnDevice = morePartModel.getLengthOnDevice();
      if (currentOffset < oldLengthOnDevice) {
         return true;
      }

      boolean var19 = false /* VF: Semaphore variable */;

      try {
         var19 = true;
         oldPayload = EmailModifier.beginChanges(messageModel, contextObject);
         morePartModel = EmailMoreVerb.findMorePartByIdentifier(messageModel, partIdentifier);
         if (!DeviceInfo.isInHolster()
            && !ModelViewListenerRegistry.isViewerUp(0, messageModel, null)
            && !messageModel.flagsSet(65536)
            && !morePartModel.suppressNotification()) {
            EmailMessageUtilities.triggerNotifications(messageModel, true, refId, null);
         }

         morePartModel.receiveMore(contextObject, incomingTransmission);
         morePartModel.clearMoreRequestSent();
         messageModel.resetStatus();
         int newLengthOnDevice = 0;
         byte encoding = incomingTransmission.getEncoding();
         if (encoding != -1) {
            messageModel.setEncoding(encoding);
         } else {
            newLengthOnDevice = incomingTransmission.getBodyLength();
         }

         if (newLengthOnDevice <= 0) {
            newLengthOnDevice = incomingTransmission.getLength();
         }

         if (oldLengthOnDevice > 0) {
            newLengthOnDevice += oldLengthOnDevice;
         }

         morePartModel.setLengthOnDevice(newLengthOnDevice);
         Parameters parameters = incomingTransmission.getParameters();
         if (parameters.has((byte)-13)) {
            morePartModel.setTrueLength(CMIMEUtilities.decodeInteger(parameters, (byte)-13));
         }

         if (parameters.has((byte)-16)) {
            morePartModel.setAvailableLength(CMIMEUtilities.decodeInteger(parameters, (byte)-16));
         }

         BodyModel bodyModel = messageModel.getBodyModel();
         if (bodyModel instanceof MorePartModel) {
            MorePartModel bodyMorePartModel = (MorePartModel)bodyModel;
            if (bodyMorePartModel.getMorePartID() == morePartModel.getMorePartID()) {
               if (morePartModel.isMoreAvailable()) {
                  messageModel.setFlags(256);
               } else {
                  messageModel.clearFlags(256);
               }

               if (morePartModel.isTruncated()) {
                  messageModel.setFlags(4096);
               } else {
                  messageModel.clearFlags(4096);
               }

               contextObject.setPrivateFlag(-4104667787783617270L, 0);
            }
         }

         contextObject.setFlag(98);
         if (!ModelViewListenerRegistry.isViewerUp(0, messageModel, null)) {
            if (!messageModel.flagsSet(65536)) {
               messageModel.changeStatus(0, 1, messageModel.inbound() ? 0 : 33554431, 0, false, false, false, false, contextObject);
               var19 = false;
            } else {
               var19 = false;
            }
         } else {
            var19 = false;
         }
      } finally {
         if (var19) {
            EmailModifier.endChanges(messageModel, oldPayload, contextObject);
         }
      }

      EmailModifier.endChanges(messageModel, oldPayload, contextObject);
      return true;
   }

   private final boolean autoResend(EmailMessageModel message, int code) {
      return !this.shouldResend(message, code) ? false : message.scheduleResend(code);
   }

   private final boolean shouldResend(EmailMessageModel message, int code) {
      switch (code) {
         case 4560:
            if (message.flagsSet(8192)) {
               return false;
            }
         case 4243:
            return true;
         default:
            return false;
      }
   }
}
