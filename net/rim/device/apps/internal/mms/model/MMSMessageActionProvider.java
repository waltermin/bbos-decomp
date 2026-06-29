package net.rim.device.apps.internal.mms.model;

import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.NonpersistedUtilityFolders;
import net.rim.device.apps.api.messaging.util.MessagingUtil;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.device.apps.internal.mms.MMSStorage;
import net.rim.device.apps.internal.mms.api.MMSPayloadModel;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;
import net.rim.device.apps.internal.mms.options.MMSOptions;
import net.rim.device.apps.internal.mms.service.MMSProtocolDataUnit;
import net.rim.device.apps.internal.mms.service.MMSService;
import net.rim.device.apps.internal.mms.service.MMSServiceUtil;
import net.rim.device.apps.internal.mms.verbs.MMSOpenVerb;

final class MMSMessageActionProvider {
   public static final boolean perform(MMSMessageModelImpl message, long actionId, Object context) {
      if (actionId == 4951292880494466830L) {
         initializeMessage(message, context);
         return true;
      }

      if (actionId == 6099736323056465049L) {
         new MMSOpenVerb(message).invoke(context);
         return true;
      }

      if (actionId == -198247372487919817L) {
         performActionsWhenMessageIsReallyAboutToBeBlownAway(message);
         return true;
      }

      if (actionId == -6225946334564270161L) {
         markOpened(message, true);
         return true;
      }

      if (actionId == -3967872215949752466L) {
         deleteMessage(message, context, false);
         return true;
      }

      if (actionId == -8570780006855731756L) {
         if (!message.isSaved()) {
            NonpersistedUtilityFolders.addMessageToUtilityFolder(7175316403005034194L, message);
            setMessageFlags(message, 2, true);
         }

         return true;
      } else {
         if (actionId == 5803508244060051872L) {
            markOpened(message, true);
            return true;
         }

         if (actionId == -8629311385729242560L) {
            markUnopened(message, true);
            return true;
         }

         if (actionId == -5544992959212130441L) {
            return message.isInbound() && message.isUnopened();
         }

         if (actionId == 477896226347912237L) {
            return message.isInbound() && message.isOpened();
         }

         if (actionId == 6780594967363292755L) {
            deleteMessage(message, context, true);
            return true;
         }

         if (actionId == 635678369939227345L) {
            return message.getStatus() != Integer.MAX_VALUE;
         }

         if (actionId == 278390328807340479L) {
            if (message.isInbound() && message.isUnopened()) {
               return true;
            }
         } else if (actionId == 3103370408204507200L) {
            if (message.isSaved()) {
               return true;
            }
         } else if (actionId == 3456946836994320775L) {
            if (message.isSavedThenOrphaned()) {
               return true;
            }
         } else if (actionId == -4201671119995560115L) {
            int status = message.getStatus();
            if (status == 8191 || status == 16383 || status == 131071) {
               return true;
            }
         } else if (actionId == -1042102706756508802L) {
            if (!message.isSaved()) {
               return true;
            }
         } else {
            if (actionId == 6760675856762529805L) {
               if (!(context instanceof Long)) {
                  throw new IllegalArgumentException();
               }

               message.setFolderId((Long)context);
               return false;
            }

            if (actionId == -8063528366659490440L) {
               if (!(context instanceof MMSPayloadModel)) {
                  throw new IllegalArgumentException();
               }

               updatePayload(message, (MMSPayloadModel)context);
               return false;
            }

            if (actionId == -3923698019885371449L) {
               if (!(context instanceof int[])) {
                  throw new IllegalArgumentException();
               }

               int[] values = (int[])context;
               updateStatus(message, values[0], values[1], values[2], values[3], values[4], true);
               return false;
            }

            if (actionId == -8071174053402202672L) {
               if (!(context instanceof MMSProtocolDataUnit)) {
                  throw new IllegalArgumentException();
               }

               message.addDeliveryReport((MMSProtocolDataUnit)context);
               PersistentObject.commit(message);
               notifyCollection(message);
               return false;
            }

            if (actionId == -1919303899965957599L) {
               if (!(context instanceof MMSProtocolDataUnit)) {
                  throw new IllegalArgumentException();
               }

               message.addReadReport((MMSProtocolDataUnit)context);
               PersistentObject.commit(message);
               notifyCollection(message);
               return false;
            }

            if (actionId == 5213547777258110094L) {
               if (message.isNew()) {
                  markOld(message, true);
                  return true;
               }
            } else if (actionId == -6072303684925088654L) {
               return message.isNew();
            }
         }

         return false;
      }
   }

   private static final void initializeMessage(MMSMessageModelImpl message, Object context) {
      if (context instanceof Folder) {
         Folder folder = (Folder)context;
         long folderId = folder.getLUID();
         if (message.getFolderId() != folderId) {
            message.setFolderId(folderId);
         }
      }

      if (message.isInbound() && message.isUnopened()) {
         UnreadCountManager.incrementUnreadCount(5);
         UnreadCountManager.incrementUnreadCount(11);
      }

      if (message.isNotYetSent()) {
         message.setStatus(16383);
      }

      if (message.isInSavedMessagesCache()) {
         NonpersistedUtilityFolders.addMessageToUtilityFolder(7175316403005034194L, message);
      }
   }

   private static final void deleteMessage(MMSMessageModelImpl message, Object context, boolean commitChanges) {
      if (!ContextObject.getFlag(context, 52)) {
         if (message.isSaved()) {
            MMSStorage.moveMessage(message, -7297051376619864492L);
            if (!message.isSavedThenOrphaned()) {
               NonpersistedUtilityFolders.removeMessageFromUtilityFolder(7175316403005034194L, message);
               setMessageFlags(message, 4, commitChanges);
               return;
            }
         } else {
            performActionsWhenMessageIsReallyAboutToBeBlownAway(message);
            MMSStorage.removeMessage(message);
         }
      } else {
         if (ContextObject.getFlag(context, 22) || ContextObject.getFlag(context, 78)) {
            ContextObject c = ContextObject.clone(context);
            c.clearFlag(52);
            c.clearFlags(22, 78);
            if (!message.isSavedThenOrphaned()) {
               deleteMessage(message, c, commitChanges);
            }

            if (message.isSavedThenOrphaned()) {
               c.setFlag(52);
               deleteMessage(message, c, commitChanges);
            }

            return;
         }

         if (message.isSavedThenOrphaned()) {
            performActionsWhenMessageIsReallyAboutToBeBlownAway(message);
            MMSStorage.removeMessage(message);
            return;
         }

         if (message.isSaved()) {
            NonpersistedUtilityFolders.removeMessageFromUtilityFolder(7175316403005034194L, message);
            clearMessageFlags(message, 2, commitChanges);
            return;
         }
      }
   }

   private static final void markOld(MMSMessageModelImpl message, boolean commitChanges) {
      UnreadCountManager.decrementUnreadCount(5, true, false);
      UnreadCountManager.decrementUnreadCount(11, true, false);
      clearMessageFlags(message, 512, commitChanges);
   }

   private static final void markOpened(MMSMessageModelImpl message, boolean commitChanges) {
      if (!message.isOpened()) {
         if (message.isInbound()) {
            UnreadCountManager.decrementUnreadCount(5, true, true);
            UnreadCountManager.decrementUnreadCount(11, true, true);
            MMSService.cancelImmediateNotifications(message);
         }

         clearMessageFlags(message, 513, commitChanges);
      }

      sendReadNotificationIfRequired(message, 128);
   }

   private static final void markUnopened(MMSMessageModelImpl message, boolean commitChanges) {
      if (message.isOpened()) {
         if (message.isInbound()) {
            boolean isNew = message.isNew();
            UnreadCountManager.incrementUnreadCount(5, isNew, true);
            UnreadCountManager.incrementUnreadCount(11, isNew, true);
         }

         setMessageFlags(message, 1, commitChanges);
      }
   }

   private static final void updateStatus(
      MMSMessageModelImpl message,
      int messageStatus,
      int httpErrorCode,
      int mmsResponseCode,
      int wapIOExceptionError,
      int wapIOExceptionAdditionalData,
      boolean commitChanges
   ) {
      message.setStatus(messageStatus, httpErrorCode, mmsResponseCode, wapIOExceptionError, wapIOExceptionAdditionalData);
      if (commitChanges) {
         PersistentObject.commit(message);
         notifyCollection(message);
      }
   }

   private static final void setMessageFlags(MMSMessageModelImpl message, int flagsToSet, boolean commitChanges) {
      message.setFlags(flagsToSet);
      if (commitChanges) {
         PersistentObject.commit(message);
         notifyCollection(message);
      }
   }

   private static final void clearMessageFlags(MMSMessageModelImpl message, int flagsToClear, boolean commitChanges) {
      message.clearFlags(flagsToClear);
      if (commitChanges) {
         PersistentObject.commit(message);
         notifyCollection(message);
      }
   }

   private static final void updatePayload(MMSMessageModelImpl message, MMSPayloadModel payload) {
      message.setPayload(payload);
      PersistentObject.commit(message);
      ModelViewListenerRegistry.notifyOfOpenedModelChange(message, message, null);
      notifyCollection(message);
   }

   private static final void sendReadNotificationIfRequired(MMSMessageModelImpl message, int status) {
      if (message.isReadNotificationPending()) {
         if (message.getAttachmentDataProvider().hasAttachments()) {
            if (message.isInbound()) {
               if (message.getAttachmentDataProvider().hasAttachments() && message.getStatus() != 1) {
                  boolean allowReadNotifications = true;
                  if (MMSClientServiceBook.isLockedOption(2)) {
                     if ((MMSClientServiceBook.getDefaultOptionFlags() & 2) != 0) {
                        allowReadNotifications = false;
                     }
                  } else {
                     allowReadNotifications = !MMSOptions.getInstance().getOptionFlag(2);
                  }

                  if (allowReadNotifications && message.requestedReadNotification()) {
                     MMSServiceUtil.notifyMessageRead(message, status);
                  }

                  clearMessageFlags(message, 256, true);
               }
            }
         }
      }
   }

   private static final void notifyCollection(MMSMessageModelImpl message) {
      long folderId = message.getFolderId();
      Folder folder = MMSStorage.getMMSFolder(folderId);
      if (folder != null) {
         CollectionListener collection = (CollectionListener)folder.getContainedItems();
         MessagingUtil.robustElementUpdated(collection, message);
      }

      if (message.isInSavedMessagesCache()) {
         NonpersistedUtilityFolders.updateMessageInUtilityFolder(7175316403005034194L, message);
      }
   }

   private static final void performActionsWhenMessageIsReallyAboutToBeBlownAway(MMSMessageModelImpl message) {
      sendReadNotificationIfRequired(message, 129);
      markOpened(message, false);
      NonpersistedUtilityFolders.removeMessageFromUtilityFolder(7175316403005034194L, message);
   }
}
