package net.rim.device.apps.internal.blackberryemail.classification;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.recipientcache.RecipientCache;
import net.rim.device.apps.internal.blackberryemail.email.recipientcache.RecipientCacheData;
import net.rim.device.apps.internal.blackberryemail.properties.MessagePropertiesDefaults;
import net.rim.device.internal.system.ITPolicyInternal;

public class MessageClassificationSelector {
   private static final long ID;
   private static MessageClassificationSelector _instance;
   private static final int NUM_LINES_IN_BODY_PREFIX;

   private MessageClassificationSelector() {
   }

   public static MessageClassificationSelector getInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (MessageClassificationSelector)applicationRegistry.getOrWaitFor(1495286560450551762L);
         if (_instance == null) {
            _instance = new MessageClassificationSelector();
            applicationRegistry.put(1495286560450551762L, _instance);
         }
      }

      return _instance;
   }

   public MessageClassification[] getClassifications(EmailMessageModel emailMessageModel, ServiceRecord serviceRecord, Object context) {
      String besUIDS = ITPolicy.getString(24, 51);
      if (besUIDS == null || serviceRecord != null && ITPolicyInternal.verifyITAdminService(serviceRecord.getUid(), false)) {
         MessageClassification[] allClassifications = MessageClassification.getMessageClassifications();
         int numClassifications = allClassifications != null ? allClassifications.length : 0;
         if (numClassifications == 0) {
            return null;
         }

         MessageClassification[] allowedClassifications = new MessageClassification[numClassifications];
         System.arraycopy(allClassifications, 0, allowedClassifications, 0, numClassifications);
         return allowedClassifications;
      } else {
         return null;
      }
   }

   public int selectDefaultClassification(
      MessageClassification[] classifications,
      EmailMessageModel emailMessageModel,
      int preferredMessageClassificationHash,
      String firstRecipientAddress,
      Object context
   ) {
      int numClassifications = classifications != null ? classifications.length : 0;
      switch (numClassifications) {
         case -1:
            int preferredClassification = this.getPreferredClassification(classifications, preferredMessageClassificationHash);
            if (preferredClassification >= 0) {
               return preferredClassification;
            } else {
               int originalItemClassification = this.getOriginalItemClassification(classifications, context);
               if (originalItemClassification >= 0) {
                  return originalItemClassification;
               } else {
                  int firstRecipientClassification = this.getFirstRecipientClassification(classifications, firstRecipientAddress);
                  if (firstRecipientClassification >= 0) {
                     return firstRecipientClassification;
                  }

                  return -1;
               }
            }
         case 0:
         default:
            return -1;
         case 1:
            return 0;
      }
   }

   private int getPreferredClassification(MessageClassification[] classifications, int preferredClassificationHash) {
      if (preferredClassificationHash == -1) {
         return -1;
      }

      int numClassifications = classifications.length;

      for (int i = 0; i < numClassifications; i++) {
         if (classifications[i].hashCode() == preferredClassificationHash) {
            return i;
         }
      }

      return -1;
   }

   private int getOriginalItemClassification(MessageClassification[] classifications, Object context) {
      Object originalItem = ContextObject.get(context, 245);
      if (originalItem instanceof EmailMessageModel) {
         EmailMessageModel originalMessage = (EmailMessageModel)originalItem;
         int numClassifications = classifications.length;
         String messageBody = originalMessage.getBody();
         if (messageBody != null) {
            String messageBodyPrefix = this.getBodyPrefix(messageBody);

            for (int i = 0; i < numClassifications; i++) {
               if (classifications[i].matchesBodyPrefix(messageBodyPrefix)) {
                  return i;
               }
            }
         }

         String messageSubject = originalMessage.getSubject();
         if (messageSubject != null) {
            for (int i = 0; i < numClassifications; i++) {
               if (classifications[i].matchesSubject(messageSubject)) {
                  return i;
               }
            }
         }
      }

      return -1;
   }

   private String getBodyPrefix(String entireBody) {
      int bodyPrefixEndPos = -1;

      for (int i = 0; i < 3; i++) {
         int currentLineEndPos = this.findLineEnd(entireBody, bodyPrefixEndPos + 1);
         if (currentLineEndPos < 0) {
            bodyPrefixEndPos = entireBody.length();
            break;
         }

         bodyPrefixEndPos = currentLineEndPos;
      }

      return entireBody.substring(0, bodyPrefixEndPos);
   }

   private int findLineEnd(String string, int startPos) {
      int lineEndPos = string.indexOf(10, startPos);
      if (lineEndPos < 0) {
         lineEndPos = string.indexOf(13, startPos);
      }

      return lineEndPos;
   }

   private int getFirstRecipientClassification(MessageClassification[] classifications, String firstRecipientAddress) {
      if (firstRecipientAddress == null) {
         return -1;
      }

      int numClassifications = classifications.length;
      RecipientCacheData recipientCacheData = RecipientCache.getInstance().get(firstRecipientAddress);
      if (recipientCacheData != null) {
         int recipientCacheClassification = recipientCacheData.getMessageClassification();

         for (int i = 0; i < numClassifications; i++) {
            if (classifications[i].hashCode() == recipientCacheClassification) {
               return i;
            }
         }
      }

      MessagePropertiesDefaults messagePropertiesDefaults = MessagePropertiesDefaults.getInstance();
      int defaultMessageClassification = messagePropertiesDefaults.getMessageClassification();

      for (int i = 0; i < numClassifications; i++) {
         if (classifications[i].hashCode() == defaultMessageClassification) {
            return i;
         }
      }

      return 0;
   }
}
