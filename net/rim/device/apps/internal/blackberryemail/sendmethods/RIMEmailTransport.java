package net.rim.device.apps.internal.blackberryemail.sendmethods;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModelImpl;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailTransport;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailModifier;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;

public final class RIMEmailTransport implements EmailTransport {
   @Override
   public final EmailMessageModel sendMessage(EmailMessageModel m, ServiceRecord sr, Object context) {
      return EmailSendUtility.sendMessage(m, sr, context);
   }

   @Override
   public final boolean canSendEmail(EmailMessageModel o) {
      return EmailMessageUtilities.canSendEmail();
   }

   @Override
   public final EmailMessageModel resendMessage(EmailMessageModel m, ServiceRecord sr, Object context) {
      ContextObject contextObject = ContextObject.clone(context);
      EmailMessageModelImpl message = (EmailMessageModelImpl)m;
      if (message.flagsSet(8192)) {
         contextObject.setFlag(94);
         if (!EmailMessageUtilities.canSendPIN()) {
            return null;
         }
      } else if (!EmailMessageUtilities.canSendEmail()) {
         return null;
      }

      if (!message.isSuccessfullySent()) {
         if (message.isMissingMessageOnServer()) {
            contextObject.setFlag(70);
         }

         EmailPayloadModel oldPayload = EmailModifier.beginChanges(message, contextObject);
         EmailSendUtility.assignGMEReferenceIdentifierToMessage(message, contextObject);
         synchronized (RIMPersistentStore.getSynchObject()) {
            synchronized (FolderHierarchies.getLockObject()) {
               MessageLookups.put(-4420850319371185992L, message.getCMIMEReferenceIdentifier(), message);
               MessageLookups.put(431630751329425149L, message.getGMEReferenceIdentifier(), message);
               message.setStatus(134217727, 0, true);
               long folderToPutMessageIn = EmailSendUtility.assignFolderToMessage(message, contextObject);
               EmailSendUtility.transportMessage(message, contextObject);
               EmailModifier.endChanges(message, oldPayload, contextObject);
               EmailSendUtility.fileOutgoingMessageInFolder(message, folderToPutMessageIn, contextObject);
            }
         }
      } else {
         CloneProvider cloneProvider = message;
         EmailMessageModelImpl resendMessage = (EmailMessageModelImpl)cloneProvider.clone(contextObject);
         int originalRefId = message.getCMIMEReferenceIdentifier();
         if (originalRefId < 0) {
            contextObject.put(2691016377741031418L, m);
         }

         resendMessage.setCMIMEReferenceIdentifier(0);
         EmailSendUtility.assignCMIMEReferenceIdentifierToMessage(resendMessage, contextObject);
         EmailSendUtility.assignGMEReferenceIdentifierToMessage(resendMessage, contextObject);
         synchronized (RIMPersistentStore.getSynchObject()) {
            synchronized (FolderHierarchies.getLockObject()) {
               MessageLookups.put(-4420850319371185992L, resendMessage.getCMIMEReferenceIdentifier(), resendMessage);
               MessageLookups.put(431630751329425149L, resendMessage.getGMEReferenceIdentifier(), resendMessage);
               resendMessage.setStatus(134217727, 0, true);
               long folderToPutMessageIn = EmailSendUtility.assignFolderToMessage(resendMessage, contextObject);
               EmailSendUtility.transportMessage(resendMessage, contextObject);
               if (message.flagsSet(32)) {
                  resendMessage.setFlags(32);
               }

               EmailModifier.endChanges(resendMessage, null, contextObject);
               EmailSendUtility.fileOutgoingMessageInFolder(resendMessage, folderToPutMessageIn, contextObject);
            }
         }
      }

      return message;
   }

   @Override
   public final boolean isBccSupported() {
      return true;
   }

   @Override
   public final boolean isCcSupported() {
      return true;
   }

   @Override
   public final boolean isAttachmentSupported(long guid) {
      return true;
   }

   @Override
   public final void saveMessage(EmailMessageModel m, ServiceRecord sr, Object context) {
      EmailMessageModelImpl message = (EmailMessageModelImpl)m;
      boolean alreadyFiled = EmailSendUtility.determineWhetherMessageAlreadyFiled(message, context);
      EmailSendUtility.assignCMIMEReferenceIdentifierToMessage(message, context);
      EmailPayloadModel oldPayload = EmailModifier.beginChanges(message, context);
      message.changeStatus(0, 0, Integer.MAX_VALUE, 0, false, false, false, false, context);
      long folderToPutMessageIn = 0;
      if (!alreadyFiled) {
         Object assignFolderToMessageContext = context;
         if (sr != null && ContextObject.get(context, -6095803566992128485L) == null) {
            ContextObject tempContextObject = ContextObject.clone(context);
            tempContextObject.put(-6095803566992128485L, sr);
            assignFolderToMessageContext = tempContextObject;
         }

         folderToPutMessageIn = EmailSendUtility.assignFolderToMessage(message, assignFolderToMessageContext);
      }

      EmailModifier.endChanges(message, oldPayload, context);
      if (!alreadyFiled) {
         EmailSendUtility.fileOutgoingMessageInFolder(message, folderToPutMessageIn, context);
      }
   }
}
