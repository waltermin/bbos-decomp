package net.rim.device.apps.internal.blackberryemail.folder;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.messaging.implus.IMPlusComposeModel;
import net.rim.device.apps.api.messaging.implus.IMPlusServiceModel;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.email.recipientcache.RecipientCache;
import net.rim.device.apps.internal.blackberryemail.email.recipientcache.RecipientCacheData;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

public class ServiceRecordSelector {
   private static final long ID;
   private static ServiceRecordSelector _instance;

   private ServiceRecordSelector() {
   }

   public static ServiceRecordSelector getInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (ServiceRecordSelector)applicationRegistry.getOrWaitFor(2499056756025585853L);
         if (_instance == null) {
            _instance = new ServiceRecordSelector();
            applicationRegistry.put(2499056756025585853L, _instance);
         }
      }

      return _instance;
   }

   public ServiceRecord[] getServiceRecords(EmailMessageModel emailMessageModel, Object context) {
      ServiceRecord imPlusServiceRecord = this.getIMPlusServiceRecord(emailMessageModel, context);
      if (imPlusServiceRecord != null) {
         return new Object[]{imPlusServiceRecord};
      }

      boolean isPin = emailMessageModel.flagsSet(8192);
      if (isPin) {
         return null;
      }

      byte messageType = emailMessageModel.getType();
      boolean forwardMessage = messageType == 16;
      boolean replyMessage = messageType == 1 || messageType == 4 || messageType == 8 || messageType == 2;
      boolean newMessage = messageType == 32;
      if (!newMessage && !forwardMessage) {
         ServiceRecord serviceRecord = EmailMessageUtilities.getServiceRecordForMessage(emailMessageModel);
         if (serviceRecord != null) {
            return new Object[]{serviceRecord};
         }

         EmailHierarchy hierarchyAlreadyAssigned = EmailHierarchy.getEmailHierarchyForFolder(emailMessageModel.getFolderId());
         if (hierarchyAlreadyAssigned != null && hierarchyAlreadyAssigned != EmailHierarchy.getAnonymousEmailHierarchy()) {
            return null;
         }
      }

      boolean disableForwardingBetweenServerices = ITPolicy.getBoolean(24, 38, false);
      if (replyMessage || forwardMessage && disableForwardingBetweenServerices) {
         ServiceRecord serviceRecord = null;
         Object originalMessage = ContextObject.get(context, 245);
         if (originalMessage instanceof EmailMessageModel) {
            serviceRecord = EmailMessageUtilities.getServiceRecordForMessage((EmailMessageModel)originalMessage);
         }

         return serviceRecord != null ? new Object[]{serviceRecord} : null;
      } else {
         return this.getAllOutgoingServiceRecords();
      }
   }

   private ServiceRecord[] getAllOutgoingServiceRecords() {
      TransmissionService transmissionService = TransmissionServiceManager.get(8399767144006445082L);
      if (!(transmissionService instanceof Object)) {
         return null;
      }

      ServiceRecord[] allOutgoingServiceRecords = ((RIMMessagingService)transmissionService).getAllOutgoingServiceRecords();
      int numOutgoingServiceRecords = allOutgoingServiceRecords != null ? allOutgoingServiceRecords.length : 0;
      ServiceRecord[] allowedServiceRecords = new Object[numOutgoingServiceRecords];
      System.arraycopy(allOutgoingServiceRecords, 0, allowedServiceRecords, 0, numOutgoingServiceRecords);
      return allowedServiceRecords;
   }

   public int getNumAllOutgoingServiceRecords() {
      ServiceRecord[] serviceRecords = this.getAllOutgoingServiceRecords();
      return serviceRecords == null ? 0 : serviceRecords.length;
   }

   public int selectDefaultServiceRecord(
      ServiceRecord[] serviceRecords,
      EmailMessageModel emailMessageModel,
      int preferredServiceRecordUIDHash,
      int preferredServiceRecordUserID,
      String firstRecipientAddress,
      Object context
   ) {
      int numServiceRecords = serviceRecords != null ? serviceRecords.length : 0;
      switch (numServiceRecords) {
         case -1:
            int preferredServiceRecord = this.getPreferredServiceRecord(serviceRecords, preferredServiceRecordUIDHash, preferredServiceRecordUserID);
            if (preferredServiceRecord >= 0) {
               return preferredServiceRecord;
            } else {
               int originalItemServiceRecord = this.getOriginalItemServiceRecord(serviceRecords, context);
               if (originalItemServiceRecord >= 0) {
                  return originalItemServiceRecord;
               } else {
                  int messageListServiceRecord = this.getMessageListServiceRecord(serviceRecords, context);
                  if (messageListServiceRecord >= 0) {
                     return messageListServiceRecord;
                  } else {
                     int boundServiceRecord = this.getBoundServiceRecord(serviceRecords, context);
                     if (boundServiceRecord >= 0) {
                        return boundServiceRecord;
                     } else {
                        int firstRecipientServiceRecord = this.getFirstRecipientServiceRecord(serviceRecords, firstRecipientAddress);
                        if (firstRecipientServiceRecord >= 0) {
                           return firstRecipientServiceRecord;
                        }

                        return -1;
                     }
                  }
               }
            }
         case 0:
         default:
            return -1;
         case 1:
            return 0;
      }
   }

   private int getPreferredServiceRecord(ServiceRecord[] serviceRecords, int preferredServiceRecordUIDHash, int preferredServiceRecordUserID) {
      int numServiceRecords = serviceRecords.length;
      if (preferredServiceRecordUserID != -1) {
         for (int i = 0; i < numServiceRecords; i++) {
            if (serviceRecords[i].getUserId() == preferredServiceRecordUserID) {
               return i;
            }
         }
      }

      if (preferredServiceRecordUIDHash != -1) {
         for (int i = 0; i < numServiceRecords; i++) {
            if (serviceRecords[i].getUidHash() == preferredServiceRecordUIDHash) {
               return i;
            }
         }
      }

      return -1;
   }

   private int getOriginalItemServiceRecord(ServiceRecord[] serviceRecords, Object context) {
      Object originalItem = ContextObject.get(context, 245);
      if (!(originalItem instanceof EmailMessageModel)) {
         return -1;
      }

      EmailMessageModel originalMessage = (EmailMessageModel)originalItem;
      ServiceRecord originalItemServiceRecord = EmailMessageUtilities.getServiceRecordForMessage(originalMessage);
      return this.getServiceRecordIndex(serviceRecords, originalItemServiceRecord);
   }

   private int getBoundServiceRecord(ServiceRecord[] serviceRecords, Object context) {
      Object boundServiceRecord = ContextObject.get(context, -6095803566992128485L);
      if (!(boundServiceRecord instanceof Object)) {
         return -1;
      }

      ServiceRecord sr = (ServiceRecord)boundServiceRecord;
      return this.getServiceRecordIndex(serviceRecords, sr);
   }

   private int getMessageListServiceRecord(ServiceRecord[] serviceRecords, Object context) {
      Long emailHierarchyLuid = (Long)ContextObject.get(context, -953487338188658393L);
      if (emailHierarchyLuid != null) {
         EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchy(emailHierarchyLuid);
         if (hierarchy != null) {
            ServiceRecord messageListServiceRecord = ServiceBook.getSB()
               .getRecordByCidAndUserId("CMIME", hierarchy.getServiceUserId(), hierarchy.getServiceNameHash(), hierarchy.getServiceUidHash());
            return this.getServiceRecordIndex(serviceRecords, messageListServiceRecord);
         }
      }

      return -1;
   }

   private int getFirstRecipientServiceRecord(ServiceRecord[] serviceRecords, String firstRecipientAddress) {
      if (firstRecipientAddress == null) {
         return -1;
      }

      RecipientCacheData recipientCacheData = RecipientCache.getInstance().get(firstRecipientAddress);
      if (recipientCacheData != null) {
         ServiceRecord firstRecipientServiceRecord = ServiceBook.getSB()
            .getRecordByCidAndUserId("CMIME", recipientCacheData.getServiceUserID(), 0, recipientCacheData.getServiceUIDHash());
         int index = this.getServiceRecordIndex(serviceRecords, firstRecipientServiceRecord);
         if (index > -1) {
            return index;
         }
      }

      TransmissionService transmissionService = TransmissionServiceManager.get(8399767144006445082L);
      if (!(transmissionService instanceof Object)) {
         return -1;
      }

      ServiceRecord firstRecipientServiceRecord = ((RIMMessagingService)transmissionService).getOutgoingServiceRecord();
      return this.getServiceRecordIndex(serviceRecords, firstRecipientServiceRecord);
   }

   private int getServiceRecordIndex(ServiceRecord[] serviceRecords, ServiceRecord serviceRecord) {
      return serviceRecord != null ? Arrays.getIndex(serviceRecords, serviceRecord) : -1;
   }

   private ServiceRecord getIMPlusServiceRecord(EmailMessageModel message, Object contextObject) {
      IMPlusServiceModel implusService = (IMPlusServiceModel)ApplicationRegistry.getApplicationRegistry().get(-2205884509140292945L);
      ServiceRecord returnSR = null;
      if (implusService != null) {
         IMPlusComposeModel[] composeModels = implusService.getComposeModels();
         boolean foundIMPlusAddress = false;
         int numRecipients = 0;

         for (int j = message.size() - 1; j >= 0; j--) {
            RIMModel model = (RIMModel)message.getAt(j);
            if (model instanceof EmailHeaderModel) {
               EmailHeaderModel headerModel = (EmailHeaderModel)model;
               int headerType = headerModel.getHeaderType();
               if (headerType == 0 || headerType == 1 || headerType == 2 || headerType == 5) {
                  Object insideModel = headerModel.getInsideModel();
                  numRecipients++;

                  for (int i = composeModels.length - 1; i >= 0; i--) {
                     Recognizer addressRecognizer = composeModels[i].getRecognizer();
                     if (addressRecognizer.recognize(insideModel)) {
                        foundIMPlusAddress = true;
                        break;
                     }
                  }

                  if (foundIMPlusAddress) {
                     returnSR = (ServiceRecord)implusService.getIMPlusServiceRecord();
                     break;
                  }
               }
            }
         }

         if ((numRecipients > 1 || message.getType() != 16) && message.flagsSet(8388608) && returnSR == null) {
            returnSR = (ServiceRecord)implusService.getIMPlusServiceRecord();
         }

         if (returnSR != null) {
            ContextObject.setPrivateFlag(contextObject, -3859986508589425865L, 1);
            message.setFlags(8388608);
         } else {
            ContextObject.clearPrivateFlag(contextObject, -3859986508589425865L, 1);
            message.clearFlags(8388608);
         }
      }

      return returnSR;
   }
}
