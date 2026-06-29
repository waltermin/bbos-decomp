package net.rim.device.apps.internal.blackberryemail.email;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.file.FileSelector;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.CancelMoreMessagingTransmission;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.internal.io.file.FileUtilities;

public class NativeAttachmentRequestProcessor$Helper {
   public static final long GUID_PROCESSOR;
   public static final int LOG_REQUEST_ADDED_IS_NULL;
   public static final int LOG_REQUEST_ADDED_TO_CACHE;
   public static final int LOG_CANCEL_REQUEST_CURRENT_REQUEST_ONLY;
   public static final int LOG_CANCEL_MESSAGE;
   public static final int LOG_MORE_COMPLETED_FOR_MSG;
   public static final int LOG_PROCESS_CALLED_BUT_NO_REQUESTS_TO_SERVE;
   public static final int LOG_PERFORM_SERVING_REQUEST;
   public static final int LOG_PERFORM_REQUEST_COMPLETED;
   public static final int LOG_CANCEL_REQUEST;
   public static final int LOG_CANCEL_REQUEST_RETRY;
   public static final int LOG_RESEND_REQUEST_CHUNKS;
   public static final int LOG_RESEND_REQUEST_COMPLETED;
   public static final int LOG_REQUEST_TRANSMISSION_FAILED;
   public static final long KEY_IS_INVOKED_BY_NATIVE_ATTACHMENT_PROCESSOR;
   public static final int NATIVE_ATTACHMENT_VALIDATION_SUCCESSFUL;
   public static final int NATIVE_ATTACHMENT_VALIDATION_ERROR_NO_SERVICE;
   public static final int NATIVE_ATTACHMENT_VALIDATION_ERROR_SERVICE_DOES_NOT_ALLOW_LARGE_ATTACHMENT;
   public static final int NATIVE_ATTACHMENT_VALIDATION_ERROR_ATTACHMENT_SIZE_VIOLATED;
   public static final int NATIVE_ATTACHMENT_VALIDATION_ERROR_ATTACHMENT_TOTAL_SIZE_VIOLATED;
   public static final int NATIVE_ATTACHMENT_VALIDATION_ERROR_NO_MODELS;
   public static final int NATIVE_ATTACHMENT_VALIDATION_ERROR_CURRENT_ATTACHMENT_SIZE_VIOLATED;

   private NativeAttachmentRequestProcessor$Helper() {
   }

   public static LargeAttachmentModel createAttachment(ServiceRecord serviceRecord, EmailMessageModel messageModel, String filePath) {
      return createAttachment(serviceRecord, getLargeAttachmentSizes(getNativeAttachments(messageModel)), filePath);
   }

   public static LargeAttachmentModel createAttachment(ServiceRecord serviceRecord, long[] existingTotalAttachmentSizes, String filePath) {
      if (serviceRecord != null && filePath != null) {
         FileConnection fc = (FileConnection)Connector.open(FileUtilities.makeFileURL(filePath));
         if (!fc.exists() || fc.isDirectory()) {
            displayMessageToUserAndLog(-1237457833540244999L, 227);
            return null;
         }

         if (!fc.canRead()) {
            throw new Object("Can not read the file...");
         }

         long currentSelectionSize = fc.fileSize();
         int validationCode = validateServiceSupportsMessageForLargeAttachments(serviceRecord, existingTotalAttachmentSizes, currentSelectionSize);
         switch (validationCode) {
            case 2:
            case 5:
               LargeAttachmentModel attachmentModel = null;
               attachmentModel = new LargeAttachmentModel();
               attachmentModel.setContentType(getFileContentType(fc));
               attachmentModel.setDisplayName(FileUtilities.getFileNameAndStripEncryptionExt(fc.getName()));
               attachmentModel.setFile(fc.getURL());
               attachmentModel.setFileSize(fc.fileSize());
               return attachmentModel;
            case 3:
            case 6:
            default:
               displayMessageToUserAndLog(-1237457833540244999L, 213);
               return null;
            case 4:
               displayMessageToUserAndLog(-1237457833540244999L, 214);
               return null;
         }
      } else {
         return null;
      }
   }

   public static LargeAttachmentModel$LargeCachedAttachmentModel createCachedAttachment(
      ServiceRecord serviceRecord, EmailMessageModel messageModel, byte[] data, String mimeType, String friendlyName
   ) {
      if (serviceRecord != null && messageModel != null && data != null && data.length >= 1) {
         long currentSelectionSize = data.length;
         long[] existingTotalAttachmentSizes = getLargeAttachmentSizes(getNativeAttachments(messageModel));
         int validationCode = validateServiceSupportsMessageForLargeAttachments(serviceRecord, existingTotalAttachmentSizes, currentSelectionSize);
         switch (validationCode) {
            case 2:
            case 5:
               long[] existingTotalAttachmentSizesForCachedAttachments = getLargeAttachmentSizes(getCachedNativeAttachments(messageModel));
               validationCode = validateServiceSupportsMessageForLargeAttachments(
                  serviceRecord, existingTotalAttachmentSizesForCachedAttachments, currentSelectionSize
               );
               switch (validationCode) {
                  case 2:
                  case 5:
                     if (friendlyName == null) {
                        friendlyName = getUnknownFileName(mimeType);
                     }

                     LargeAttachmentModel$LargeCachedAttachmentModel model = new LargeAttachmentModel$LargeCachedAttachmentModel();
                     model.setContentType(mimeType);
                     model.setData(data);
                     model.setDisplayName(friendlyName);
                     model.setFile(friendlyName);
                     model.setFileSize(data.length);
                     return model;
                  case 3:
                  case 6:
                  default:
                     displayMessageToUserAndLog(-1237457833540244999L, 229);
                     return null;
                  case 4:
                     displayMessageToUserAndLog(-1237457833540244999L, 230);
                     return null;
               }
            case 3:
            case 6:
            default:
               displayMessageToUserAndLog(-1237457833540244999L, 213);
               return null;
            case 4:
               displayMessageToUserAndLog(-1237457833540244999L, 214);
               return null;
         }
      } else {
         return null;
      }
   }

   public static String getUnknownFileName(String mimeType) {
      String ext = MIMETypeAssociations.getExtensionFromMIMEType(mimeType);
      return ext == null
         ? EmailResources.getString(73)
         : ((StringBuffer)(new Object())).append(EmailResources.getString(73)).append(".").append(ext).toString();
   }

   public static String getUnknownMimeType() {
      return "application/octet-stream";
   }

   public static String getFileContentType(FileConnection fc) {
      if (fc == null) {
         return null;
      }

      String mimeType = MIMETypeAssociations.getMIMEType(fc.getName());
      return mimeType == null ? getUnknownMimeType() : mimeType;
   }

   public static String getFilePath() {
      FileSelector fileSelector = (FileSelector)(new Object(null));
      fileSelector.onlySelectForwardUnlocked();
      return fileSelector.selectFile(null);
   }

   public static void displayMessageToUserAndLog(long guid, int msgKey) {
      displayMessageToUserAndLog(guid, msgKey, 5);
   }

   public static void displayMessageToUserAndLog(long guid, int msgKey, int errorLevel) {
      String msg = MessageResources.getString(msgKey);
      Dialog.alert(msg);
      EventLogger.logEvent(guid, msg.getBytes(), errorLevel);
   }

   public static long getInMemoryAttachmentMaxSize() {
      return 153600;
   }

   public static long getInMemoryAttachmentsTotalMaxSize() {
      return 512000;
   }

   public static EmailMessageModel getMessageFromParameters(int cmimeReferenceId, Object context) {
      ServiceRecord serviceRecord = (ServiceRecord)ContextObject.get(context, -6095803566992128485L);
      EmailHierarchy emailHierarchy = EmailTransmissionServiceListener.getEmailHierarchy(serviceRecord, context);
      EmailMessageModel emailMessageModel = (EmailMessageModel)emailHierarchy.getMessage(cmimeReferenceId);
      return emailMessageModel;
   }

   public static LargeAttachmentModel[] getNativeAttachments(EmailMessageModel messageModel) {
      LargeAttachmentModel[] models = new LargeAttachmentModel[0];
      if (messageModel == null) {
         return models;
      }

      int length = messageModel.size();

      for (int i = 0; i < length; i++) {
         Object object = messageModel.getAt(i);
         if (object instanceof LargeAttachmentModel) {
            Arrays.add(models, object);
         }
      }

      return models;
   }

   public static void updateMessageStatus(EmailMessageModel model, int status) {
      if (model != null) {
         ContextObject contextObject = ContextObject.castOrCreate(null);
         contextObject.put(4619344424211138694L, Boolean.TRUE);
         model.changeStatus(0, 0, status, 0, true, true, false, false, contextObject);
      }
   }

   public static LargeAttachmentModel$LargeCachedAttachmentModel[] getCachedNativeAttachments(EmailMessageModel messageModel) {
      LargeAttachmentModel$LargeCachedAttachmentModel[] models = new LargeAttachmentModel$LargeCachedAttachmentModel[0];
      if (messageModel == null) {
         return models;
      }

      int length = messageModel.size();

      for (int i = 0; i < length; i++) {
         Object object = messageModel.getAt(i);
         if (object instanceof LargeAttachmentModel$LargeCachedAttachmentModel) {
            Arrays.add(models, object);
         }
      }

      return models;
   }

   public static boolean messageContainsLargeAttachments(EmailMessageModel emailMessageModel) {
      if (emailMessageModel == null) {
         return false;
      }

      int length = emailMessageModel.size();

      for (int i = 0; i < length; i++) {
         Object object = emailMessageModel.getAt(i);
         if (object instanceof LargeAttachmentModel) {
            return true;
         }
      }

      return false;
   }

   public static void transmitMessageMoreCancel(
      TransmissionService transmissionService, int refId, ServiceRecord serviceRecord, byte errorCode, byte[] errorMsg
   ) {
      transmitMessageMoreCancel(transmissionService, refId, serviceRecord, errorCode, errorMsg, Integer.MIN_VALUE);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static void transmitMessageMoreCancel(
      TransmissionService transmissionService, int refId, ServiceRecord serviceRecord, byte errorCode, byte[] errorMsg, int contentPartId
   ) {
      CancelMoreMessagingTransmission cancelTx = (CancelMoreMessagingTransmission)(new Object(contentPartId, refId, errorCode, errorMsg));
      ContextObject contextObject = ContextObject.castOrCreate(null);
      contextObject.put(-6095803566992128485L, serviceRecord);

      try {
         transmissionService.transmitObject(null, cancelTx, null, UIDGenerator.getUID(), contextObject);
      } catch (Throwable var10) {
         EventLogger.logEvent(-1237457833540244999L, e.getMessage().getBytes(), 2);
         return;
      }
   }

   public static int validateServiceSupportsMessageForLargeAttachments(ServiceRecord sr, long[] largeAttachmenModelsSizes, long currentAttachmentSize) {
      if (sr == null) {
         return 1;
      }

      long allowedAttachmentSizeByService = CMIMEUtilities.getNativeAttachmentMfhMaxSize(sr);
      long totalAllowedAttachmentSizeByService = CMIMEUtilities.getNativeAttachmentMfhMaxTotalSize(sr);
      boolean hasLargeAttachments = largeAttachmenModelsSizes != null && largeAttachmenModelsSizes.length > 0;
      boolean largeAttachmentsAllowedByService = CMIMEUtilities.isLargeAttachmentUploadAllowed(sr);
      if (largeAttachmentsAllowedByService) {
         if (!hasLargeAttachments) {
            return currentAttachmentSize > allowedAttachmentSizeByService ? 6 : 0;
         }

         long totalSize = currentAttachmentSize;
         long currentSize = -1;

         for (int i = 0; i < largeAttachmenModelsSizes.length; i++) {
            currentSize = largeAttachmenModelsSizes[i];
            if (currentSize > allowedAttachmentSizeByService) {
               return 3;
            }

            totalSize += currentSize;
            if (totalSize > totalAllowedAttachmentSizeByService) {
               return 4;
            }
         }

         return 0;
      } else {
         return 2;
      }
   }

   public static int validateServiceSupportsMessageForLargeAttachments(
      ServiceRecord sr, LargeAttachmentModel[] largeAttachmenModels, long currentAttachmentSize
   ) {
      return validateServiceSupportsMessageForLargeAttachments(sr, getLargeAttachmentSizes(largeAttachmenModels), currentAttachmentSize);
   }

   public static long[] getLargeAttachmentSizes(LargeAttachmentModel[] models) {
      long[] sizes = new long[models == null ? 0 : models.length];

      for (int i = 0; i < sizes.length; i++) {
         sizes[i] = models[i].getFileSize();
      }

      return sizes;
   }
}
