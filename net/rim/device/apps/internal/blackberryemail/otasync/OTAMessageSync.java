package net.rim.device.apps.internal.blackberryemail.otasync;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.api.messaging.util.OTAFMControl;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.FolderManagementCommandListener;
import net.rim.device.apps.api.transmission.rim.RIMMessagingFolderManagement;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfiguration;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfigurationManager$Instance;
import net.rim.device.apps.internal.blackberryemail.EmailSyncState;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModelImpl;
import net.rim.device.apps.internal.blackberryemail.email.FolderPreselector;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolderBase;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.folder.GhostMessageData;
import net.rim.device.internal.proxy.Proxy;
import net.rim.vm.Array;
import net.rim.vm.PersistentInteger;

public final class OTAMessageSync
   extends OTAFMControl
   implements Runnable,
   FolderManagementCommandListener,
   GlobalEventListener,
   RealtimeClockListener,
   HolsterListener,
   SyncEventListener,
   ReliableTransmissionListener {
   private OTAFMConfigurationManagerImpl _configManager;
   private SyncWorkerThread _workerThread;
   private ReliableTransmissionHelper _reliableService;
   private int _clockCounter;
   private boolean _syncActive;
   private boolean _serviceBooksSynced;
   private boolean _messageListRestored;
   private boolean _folderListSynced;
   private ServiceBook _serviceBook;
   private Recognizer _messageListCommandRecognizer;
   private FlushBuffersThread _flushBuffersThread;
   private static final long TRANSMISSION_SERVICE_GUID;
   private static final long OTAFM_PRIVATE_FLAGS_GUID;
   private static final long ORIGINAL_THREAD_PRIORITY_GUID;
   private static final int FOLDER_LIST_REQUEST_REQUIRED_FLAG;
   private static final int BUFFER_LARGE_ENOUGH_TO_SEND;
   private static final int CLOCK_TICKS_FOR_BACKGROUND_WORK;
   private static OTAMessageSync _instance;

   public final void modifyFolderAttributesCommand(ServiceRecord serviceRecord, RIMMessagingFolderManagement request) {
      if (request != null) {
         OTAFMEvents.logEvent(1397573185, serviceRecord, 5);
         this.transmitObject(serviceRecord, request);
      }
   }

   public final boolean receiveFolderManagementCommand(TransmissionService param1, RIMMessagingFolderManagement param2, Object param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 3
      // 01: invokestatic net/rim/device/apps/api/framework/model/ContextObject.castOrCreate (Ljava/lang/Object;)Lnet/rim/device/apps/api/framework/model/ContextObject;
      // 04: astore 4
      // 06: aload 4
      // 08: ldc2_w 8797645593349626617
      // 0b: aload 1
      // 0c: invokevirtual net/rim/device/api/util/LongHashtable.put (JLjava/lang/Object;)Ljava/lang/Object;
      // 0f: pop
      // 10: aload 0
      // 11: aload 3
      // 12: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/OTAMessageSync.getServiceRecord (Ljava/lang/Object;)Lnet/rim/device/api/servicebook/ServiceRecord;
      // 15: astore 5
      // 17: aload 5
      // 19: ifnonnull 30
      // 1c: bipush 1
      // 1d: istore 6
      // 1f: aload 4
      // 21: ldc2_w 8797645593349626617
      // 24: invokevirtual net/rim/device/api/util/LongHashtable.remove (J)Ljava/lang/Object;
      // 27: pop
      // 28: aload 0
      // 29: aload 3
      // 2a: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/OTAMessageSync.restoreThreadPriority (Ljava/lang/Object;)V
      // 2d: iload 6
      // 2f: ireturn
      // 30: aload 0
      // 31: getfield net/rim/device/apps/internal/blackberryemail/otasync/OTAMessageSync._configManager Lnet/rim/device/apps/internal/blackberryemail/otasync/OTAFMConfigurationManagerImpl;
      // 34: aload 5
      // 36: invokevirtual net/rim/device/apps/internal/blackberryemail/otasync/OTAFMConfigurationManagerImpl.getServiceSyncInfo (Lnet/rim/device/api/servicebook/ServiceRecord;)Lnet/rim/device/apps/internal/blackberryemail/otasync/ServiceSyncInfo;
      // 39: astore 6
      // 3b: aload 6
      // 3d: ifnull 45
      // 40: aload 6
      // 42: invokevirtual net/rim/device/apps/internal/blackberryemail/otasync/ServiceSyncInfo.abortMessageList ()V
      // 45: aload 2
      // 46: aload 4
      // 48: aload 0
      // 49: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.processCommands (Ljava/lang/Object;Lnet/rim/device/apps/api/transmission/rim/FolderManagementCommandListener;)V
      // 4c: aload 4
      // 4e: ldc2_w -8291706733254734206
      // 51: bipush 0
      // 52: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.getPrivateFlag (JI)Z
      // 55: ifeq 61
      // 58: aload 0
      // 59: getfield net/rim/device/apps/internal/blackberryemail/otasync/OTAMessageSync._workerThread Lnet/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread;
      // 5c: aload 5
      // 5e: invokevirtual net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread.sendFolderListRequest (Lnet/rim/device/api/servicebook/ServiceRecord;)V
      // 61: aload 4
      // 63: ldc2_w 8797645593349626617
      // 66: invokevirtual net/rim/device/api/util/LongHashtable.remove (J)Ljava/lang/Object;
      // 69: pop
      // 6a: aload 0
      // 6b: aload 3
      // 6c: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/OTAMessageSync.restoreThreadPriority (Ljava/lang/Object;)V
      // 6f: bipush 1
      // 70: ireturn
      // 71: astore 5
      // 73: aload 4
      // 75: ldc2_w 8797645593349626617
      // 78: invokevirtual net/rim/device/api/util/LongHashtable.remove (J)Ljava/lang/Object;
      // 7b: pop
      // 7c: aload 0
      // 7d: aload 3
      // 7e: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/OTAMessageSync.restoreThreadPriority (Ljava/lang/Object;)V
      // 81: bipush 1
      // 82: ireturn
      // 83: astore 5
      // 85: aload 4
      // 87: ldc2_w 8797645593349626617
      // 8a: invokevirtual net/rim/device/api/util/LongHashtable.remove (J)Ljava/lang/Object;
      // 8d: pop
      // 8e: aload 0
      // 8f: aload 3
      // 90: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/OTAMessageSync.restoreThreadPriority (Ljava/lang/Object;)V
      // 93: bipush 1
      // 94: ireturn
      // 95: astore 7
      // 97: aload 4
      // 99: ldc2_w 8797645593349626617
      // 9c: invokevirtual net/rim/device/api/util/LongHashtable.remove (J)Ljava/lang/Object;
      // 9f: pop
      // a0: aload 0
      // a1: aload 3
      // a2: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/OTAMessageSync.restoreThreadPriority (Ljava/lang/Object;)V
      // a5: aload 7
      // a7: athrow
      // try (8 -> 16): 56 null
      // try (25 -> 47): 56 null
      // try (8 -> 16): 66 null
      // try (25 -> 47): 66 null
      // try (8 -> 16): 76 null
      // try (25 -> 47): 76 null
      // try (56 -> 57): 76 null
      // try (66 -> 67): 76 null
      // try (76 -> 77): 76 null
   }

   final void sendFolderListRequest(ServiceRecord serviceRecord) {
      FolderListRequestCommand request = new FolderListRequestCommand();
      OTAFMEvents.logEvent(1044794450, serviceRecord, 5);
      if (!this._reliableService.packetStillInStack(serviceRecord, request)) {
         this._reliableService.purgeTransactions(serviceRecord, request);
         this.transmitObject(serviceRecord, request);
      }
   }

   public final void sendPurgedMessageList(int[] refIds, int[] statuses, int messageListId, ServiceRecord serviceRecord) {
      SendPurgedMessageListCommand request = new SendPurgedMessageListCommand(refIds, statuses, messageListId);
      OTAFMEvents.logEvent(1045450060, serviceRecord, 5);
      if (!this._reliableService.packetStillInStack(serviceRecord, request)) {
         this._reliableService.purgeTransactions(serviceRecord, request);
         this.transmitObject(serviceRecord, request);
      }
   }

   final void sendPurgeDeletedMessages(ServiceRecord serviceRecord) {
      PurgeDeletedMessagesCommand request = new PurgeDeletedMessagesCommand();
      OTAFMEvents.logEvent(1045447757, serviceRecord, 5);
      this._reliableService.purgeTransactions(serviceRecord, request);
      this.transmitObject(serviceRecord, request);
   }

   public final void messageListRestored() {
      OTAFMEvents.logEvent(1297303339, 5);
      this._messageListRestored = true;
   }

   public final void folderListSynced() {
      OTAFMEvents.logEvent(1179403307, 5);
      this._folderListSynced = true;
   }

   final boolean syncMessageDeletes(ServiceRecord serviceRecord) {
      OTAFMConfiguration configuration = this._configManager.getConfiguration(serviceRecord);
      return configuration.getWirelessDeletesEnabled();
   }

   final void purgeDeletedMessages(ServiceRecord serviceRecord) {
      this.flushTransmitBuffer(serviceRecord, false);
      this.sendPurgeDeletedMessages(serviceRecord);
   }

   final void sendMessageList(ServiceRecord serviceRecord) {
      ServiceSyncInfo syncInfo = this._configManager.getServiceSyncInfo(serviceRecord);
      this.flushTransmitBuffer(serviceRecord, false);
      syncInfo.clearAbortMessageList();
      MessageListCommand request = this.buildMessageListCommand(serviceRecord);
      if (!syncInfo.getAbortMessageList() && request != null) {
         if (this._reliableService.packetStillInStack(serviceRecord, request)) {
            return;
         }

         if (request.isRestoreMessageList()) {
            OTAFMEvents.logEvent(1045581139, serviceRecord, 5);
         } else {
            OTAFMEvents.logEvent(1045254983, serviceRecord, 5);
         }

         syncInfo.setRestoreMessageList(false);
         this._reliableService.purgeTransactions(serviceRecord, request);
         this.transmitObject(serviceRecord, request);
      } else {
         OTAFMEvents.logEvent(1045253153, serviceRecord, 5);
         this._workerThread.sendMessageList(serviceRecord);
      }

      PersistentObject.commit(syncInfo);
   }

   public final boolean messageDeletedOnDevice(EmailMessageModel message, EmailFolder folder, Object context) {
      if (this.commandFromServer(context)) {
         return false;
      }

      ServiceRecord serviceRecord = EmailMessageUtilities.getServiceRecordForMessage(message);
      int refId = message.getCMIMEReferenceIdentifier();
      return this.messageDeletedOnDevice(serviceRecord, refId);
   }

   public final boolean messageDeletedOnDevice(ServiceRecord serviceRecord, int refId) {
      if (serviceRecord != null && this.syncMessageDeletes(serviceRecord)) {
         ServiceSyncInfo syncInfo = this._configManager.getServiceSyncInfo(serviceRecord);
         OTAFMEvents.logEvent(1044661580, serviceRecord, refId, 5);
         syncInfo.deleteMessage(refId);
         PersistentObject.commit(syncInfo);
         this.checkBufferReadyToSend(serviceRecord, syncInfo);
         this._flushBuffersThread.activateFlush();
         return true;
      } else {
         return false;
      }
   }

   public final void messageMovedOnDevice(EmailMessageModel message, EmailFolder oldFolder, EmailFolder newFolder, Object context) {
      if (!this.commandFromServer(context)) {
         if (oldFolder != null && newFolder != null && !ContextObject.getFlag(context, 19)) {
            if ((oldFolder.getFolderAttributes() & 1) == 0 && (newFolder.getFolderAttributes() & 1) == 0) {
               ServiceRecord serviceRecord = EmailMessageUtilities.getServiceRecordForMessage(message);
               if (serviceRecord != null && this.syncMessageMoves(serviceRecord)) {
                  int oldFolderId = oldFolder.getFolderId();
                  int newFolderId = newFolder.getFolderId();
                  if (oldFolderId != newFolderId) {
                     ServiceSyncInfo syncInfo = this._configManager.getServiceSyncInfo(serviceRecord);
                     int refId = message.getCMIMEReferenceIdentifier();
                     OTAFMEvents.logEvent(1045253974, serviceRecord, refId, 5);
                     if (OTAFMEvents.getDebugLevel() >= 1) {
                        OTAFMEvents.logEvent(1179799373, oldFolderId, 5);
                        OTAFMEvents.logEvent(538989647, newFolderId, 5);
                     }

                     syncInfo.moveMessage(refId, oldFolderId, newFolderId);
                     PersistentObject.commit(syncInfo);
                     this.checkBufferReadyToSend(serviceRecord, syncInfo);
                     this._flushBuffersThread.activateFlush();
                  }
               }
            }
         }
      }
   }

   public final void messageReadStatusChangeOnDevice(EmailMessageModel message, boolean read, Object context) {
      if (!this.commandFromServer(context)) {
         if (!ContextObject.getFlag(context, 19)) {
            ServiceRecord serviceRecord = EmailMessageUtilities.getServiceRecordForMessage(message);
            int refId = message.getCMIMEReferenceIdentifier();
            this.messageReadStatusChangeOnDevice(serviceRecord, refId, read);
         }
      }
   }

   public final void messageReadStatusChangeOnDevice(ServiceRecord serviceRecord, int refId, boolean read) {
      if (serviceRecord != null && this.syncMessageStatusUpdates(serviceRecord)) {
         ServiceSyncInfo syncInfo = this._configManager.getServiceSyncInfo(serviceRecord);
         OTAFMEvents.logEvent(1045648449, serviceRecord, refId, 5);
         syncInfo.readStatusChange(refId, read);
         PersistentObject.commit(syncInfo);
         this.checkBufferReadyToSend(serviceRecord, syncInfo);
         this._flushBuffersThread.activateFlush();
      }
   }

   final void flushTransmitBuffers(boolean userAction) {
      this._workerThread.flushTransmitBuffers(userAction);
   }

   final void flushTransmitBuffer(ServiceRecord serviceRecord, boolean userAction) {
      if (ReliableTransmissionHelper.canTransmit(serviceRecord)) {
         RIMMessagingFolderManagement request = this._configManager.getTransmitBuffer(serviceRecord);
         if (request == null && userAction) {
            request = (RIMMessagingFolderManagement)(new Object());
         }

         if (request != null) {
            OTAFMEvents.logEvent(1179407176, serviceRecord, 5);
            if (userAction) {
               request.addSendChangesRequest();
            }

            this.transmitObject(serviceRecord, request);
         }
      }
   }

   final void sendConfigurationQuery(ServiceRecord serviceRecord) {
      ConfigurationQueryCommand request = new ConfigurationQueryCommand();
      if (this._reliableService.packetStillInStack(serviceRecord, request)) {
         OTAFMEvents.logEvent(558057041, serviceRecord, 0);
      } else {
         OTAFMEvents.logEvent(1044596305, serviceRecord, 0);
         this._reliableService.purgeTransactions(serviceRecord, request);
         this.transmitObject(serviceRecord, request);
      }
   }

   final void sendConfiguration(ServiceRecord serviceRecord, OTAFMConfiguration configuration) {
      ConfigurationCommand request = new ConfigurationCommand(configuration);
      ServiceSyncInfo syncInfo = this._configManager.getServiceSyncInfo(serviceRecord);
      configuration.clearAcknowledgementReceived();
      syncInfo.setConfigurationSentTimestamp(System.currentTimeMillis());
      PersistentObject.commit(syncInfo);
      if (!this._reliableService.packetStillInStack(serviceRecord, request)) {
         OTAFMEvents.logEvent(1044596295, serviceRecord, 0);
         this._reliableService.purgeTransactions(serviceRecord, request);
         this.transmitObject(serviceRecord, request);
      }
   }

   @Override
   public final void failedTransmission(ServiceRecord serviceRecord, Object payload, int code) {
      if (code == 4229) {
         OTAFMEvents.logEvent(
            1414681389,
            serviceRecord,
            ((StringBuffer)(new Object("Code="))).append(String.valueOf(code)).append(", ").append(payload.getClass().getName()).toString(),
            2
         );
         this.serviceRecordRemoved(serviceRecord);
      } else if (payload instanceof ConfigurationQueryCommand) {
         OTAFMEvents.logEvent(1414677293, serviceRecord, ((StringBuffer)(new Object("Code="))).append(String.valueOf(code)).toString(), 2);
         this.serviceRecordRemoved(serviceRecord);
      } else {
         OTAFMEvents.logEvent(
            1414680365,
            serviceRecord,
            ((StringBuffer)(new Object("Code="))).append(String.valueOf(code)).append(", ").append(payload.getClass().getName()).toString(),
            2
         );
         this._workerThread.sendConfigurationQuery(serviceRecord);
      }
   }

   @Override
   public final void successfulTransmission(ServiceRecord serviceRecord, Object payload) {
   }

   @Override
   public final void clockUpdated() {
      if (++this._clockCounter >= 5) {
         this._clockCounter = 0;
         this._workerThread.performPeriodicTasks();
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid != -4220058463650496006L && guid != 8288627527798139133L && guid != 2522898683889177438L) {
         if (guid == 8508406279413621091L || guid == -594020114676189989L) {
            this.itpolicyChanged();
            return;
         }

         if (guid == -2728804572467266390L) {
            this._workerThread.forceConfigurationQuery();
            return;
         }

         if (guid == -2728804572467266389L) {
            this._workerThread.enableAllServiceRecords();
            return;
         }

         if (guid == -2728804572467266387L) {
            this._workerThread.sendAllMessageLists();
            return;
         }

         if (guid == -2728804572467266388L) {
            this._workerThread.sendAllFolderListRequests();
            return;
         }

         if (guid == -2728804572467266384L) {
            OTAFMEvents.setDebugLevel(0);
            return;
         }

         if (guid == -2728804572467266383L) {
            OTAFMEvents.setDebugLevel(1);
            return;
         }

         if (guid == -2728804572467266382L) {
            int id = PersistentInteger.getId(-2728804572467266382L, 1);
            int value = PersistentInteger.get(id);
            PersistentInteger.set(id, 1 - value);
            this.itpolicyChanged();
         }
      } else if (object0 instanceof Object) {
         ServiceRecord targetSR = (ServiceRecord)object0;
         if (targetSR == null || !StringUtilities.strEqualIgnoreCase(targetSR.getCid(), "CMIME", 1701707776)) {
            return;
         }

         if (guid == -4220058463650496006L) {
            this.serviceRecordAdded(targetSR);
            return;
         }

         if (guid == 8288627527798139133L) {
            this.serviceRecordUpdated((ServiceRecord)object1, targetSR);
            return;
         }

         if (guid == 2522898683889177438L) {
            this.serviceRecordRemoved(targetSR);
            return;
         }
      }
   }

   @Override
   public final void outOfHolster() {
   }

   @Override
   public final void inHolster() {
      this.flushBatchedCommands();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void moveMessageCommand(int messageRefId, int fromFolderId, int toFolderId, Object context) {
      ServiceRecord serviceRecord = this.getServiceRecord(context);
      if (this.syncMessageMoves(serviceRecord)) {
         EmailHierarchy hierarchy = this.getHierarchy(serviceRecord);
         EmailMessageModelImpl message = this.getMessage(messageRefId);
         if (hierarchy != null && message != null) {
            EmailFolder currentFolder = (EmailFolder)hierarchy.getFolder(message.getFolderId());
            EmailFolder targetFolder = (EmailFolder)hierarchy.getFolder(toFolderId);
            if (toFolderId != 0 && targetFolder == null || targetFolder != null && !targetFolder.isInFolderDatabase()) {
               ContextObject.setPrivateFlag(context, -8291706733254734206L, 0);
               return;
            }

            if (targetFolder != null) {
               if (currentFolder != null) {
                  int currentFolderId = currentFolder.getFolderId();
                  if (currentFolderId == targetFolder.getFolderId()) {
                     return;
                  }

                  if (fromFolderId != 0 && currentFolderId != fromFolderId) {
                     OTAFMConfiguration configuration = this._configManager.getConfiguration(serviceRecord);
                     if (configuration.getConflictResolutionSetting() == 0) {
                        ServiceSyncInfo syncInfo = this._configManager.getServiceSyncInfo(serviceRecord);
                        syncInfo.moveMessage(messageRefId, currentFolderId, currentFolderId);
                        return;
                     }
                  }
               }

               ContextObject contextObject = ContextObject.castOrCreate(context);
               boolean var15 = false /* VF: Semaphore variable */;

               try {
                  var15 = true;
                  OTAFMEvents.logEvent(1011699542, serviceRecord, messageRefId, 5);
                  if (OTAFMEvents.getDebugLevel() >= 1) {
                     OTAFMEvents.logEvent(1179799373, fromFolderId, 5);
                     OTAFMEvents.logEvent(538989647, toFolderId, 5);
                  }

                  contextObject.put(-1219344331000926502L, targetFolder);
                  message.perform(1092577344890817449L, contextObject);
                  FolderPreselector.updateDefaultFolder(message, (EmailFolder)hierarchy.getFolder(message.getFolderId()), false);
                  var15 = false;
               } finally {
                  if (var15) {
                     contextObject.remove(-1219344331000926502L);
                  }
               }

               contextObject.remove(-1219344331000926502L);
               return;
            }
         }
      }
   }

   @Override
   public final void deleteMessageCommand(int messageRefId, int folderId, Object context) {
      ServiceRecord serviceRecord = this.getServiceRecord(context);
      if (this.syncMessageDeletes(serviceRecord)) {
         EmailMessageModelImpl message = this.getMessage(messageRefId);
         EmailHierarchy hierarchy = this.getHierarchy(serviceRecord);
         if (message != null) {
            if (hierarchy != null) {
               int currentFolderId = this.getMessageFolderId(hierarchy, message);
               OTAFMConfiguration configuration = this._configManager.getConfiguration(serviceRecord);
               if (folderId != 0 && folderId != currentFolderId && configuration.getConflictResolutionSetting() == 0) {
                  OTAFMEvents.logEvent(1011108909, serviceRecord, messageRefId, 5);
                  return;
               }
            }

            ServiceSyncInfo syncInfo = this._configManager.getServiceSyncInfo(serviceRecord);
            OTAFMEvents.logEvent(1011107148, serviceRecord, messageRefId, 5);
            message.perform(-8494690080715024104L, context);
            syncInfo.messageDeleted(messageRefId);
         }
      }
   }

   @Override
   public final void messageStatusCommand(int messageRefId, int status, Object context) {
      ServiceRecord serviceRecord = this.getServiceRecord(context);
      if (this.syncMessageStatusUpdates(serviceRecord)) {
         EmailHierarchy hierarchy = this.getHierarchy(serviceRecord);
         EmailMessageModelImpl message = this.getMessage(messageRefId);
         if (hierarchy != null && message != null) {
            OTAFMEvents.logEvent(1012094017, serviceRecord, messageRefId, 5);
            if (OTAFMEvents.getDebugLevel() >= 1) {
               OTAFMEvents.logEvent(1398030676, status, 5);
            }

            if ((status & 1) != 0) {
               message.perform(5803508244060051872L, context);
               return;
            }

            message.perform(-8629311385729242560L, context);
         }
      }
   }

   @Override
   public final void otafmConfigCommand(OTAFMConfiguration newConfiguration, Object context) {
      ServiceRecord serviceRecord = this.getServiceRecord(context);
      if (serviceRecord != null) {
         OTAFMEvents.logEvent(1011041863, serviceRecord, 0);
         this._configManager.updateConfiguration(serviceRecord, newConfiguration, true);
      }
   }

   @Override
   public final void otafmConfigAckCommand(OTAFMConfiguration configuration, Object context) {
      ServiceRecord serviceRecord = this.getServiceRecord(context);
      if (serviceRecord != null) {
         OTAFMConfiguration currentConfiguration = this._configManager.getConfiguration(serviceRecord);
         if (currentConfiguration.userConfigurationEqual(configuration)) {
            OTAFMEvents.logEvent(1011041835, serviceRecord, 0);
            currentConfiguration.setAcknowledgementReceived();
            if (currentConfiguration.getFolderListRequired()) {
               currentConfiguration.setFolderListRequired(false);
               this._workerThread.sendFolderListRequest(serviceRecord);
            }

            PersistentObject.commit(currentConfiguration);
            return;
         }

         OTAFMEvents.logEvent(1011041837, serviceRecord, 0);
         this._workerThread.sendConfiguration(serviceRecord, currentConfiguration);
      }
   }

   @Override
   public final void configurationRequestCommand(Object context) {
      ServiceRecord serviceRecord = this.getServiceRecord(context);
      if (serviceRecord != null) {
         OTAFMConfiguration configuration = this._configManager.getConfiguration(serviceRecord);
         OTAFMEvents.logEvent(1011041873, serviceRecord, 0);
         if (configuration != OTAFMConfiguration.getDisabledConfiguration()) {
            this._workerThread.sendConfiguration(serviceRecord, configuration);
         }
      }
   }

   @Override
   public final void messageListRequestCommand(Object context) {
      ServiceRecord serviceRecord = this.getServiceRecord(context);
      if (serviceRecord != null) {
         OTAFMEvents.logEvent(1011700561, serviceRecord, 5);
         this._workerThread.sendMessageList(serviceRecord);
      }
   }

   @Override
   public final void removeAllFoldersCommand(Object context) {
      if (!this._folderListSynced) {
         ServiceRecord serviceRecord = this.getServiceRecord(context);
         EmailHierarchy hierarchy = this.getHierarchy(serviceRecord);
         if (hierarchy != null) {
            OTAFMEvents.logEvent(1011240002, serviceRecord, 5);
            this.lowerThreadPriority(context);
            this.removeAllSyncableFolders(hierarchy, hierarchy, true, false);
            hierarchy.commitSubtree(true);
         }
      }
   }

   @Override
   public final void folderSyncCompleteCommand(Object context) {
      if (!this._folderListSynced) {
         ServiceRecord serviceRecord = this.getServiceRecord(context);
         EmailHierarchy hierarchy = this.getHierarchy(serviceRecord);
         OTAFMEvents.logEvent(1011240005, serviceRecord, 5);
         EmailHierarchy.purge();
         if (hierarchy != null) {
            hierarchy.ensureFiledMessagesInFiledCollection();
         }

         this._workerThread.sendMessageList(serviceRecord);
      }
   }

   @Override
   public final void run() {
      Proxy proxy = Proxy.getInstance();
      this.initialize();
      Object lockObject = RIMPersistentStore.getPersistentObject(8822586609645349199L);
      synchronized (lockObject) {
         this.register();
         lockObject.notifyAll();
      }

      TransmissionService cmimeTransmissionService = TransmissionServiceManager.get(8399767144006445082L);
      this._reliableService = new ReliableTransmissionHelper(
         this, "net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_FOLDER_MANAGEMENT", cmimeTransmissionService
      );
      this._workerThread.setReliableTransmissionService(this._reliableService);
      proxy.addGlobalEventListener(this);
      proxy.addRealtimeClockListener(this);
      proxy.addHolsterListener(this);
      SyncManager.getInstance().addSyncEventListener(this);
      this._configManager.start(this._workerThread);
   }

   @Override
   public final void createFolderCommand(int folderId, int parentFolderId, String name, int type, int attributes, Object context) {
      if (!this._folderListSynced) {
         ServiceRecord serviceRecord = this.getServiceRecord(context);
         EmailHierarchy hierarchy = this.getHierarchy(serviceRecord);
         if (hierarchy == null || name == null) {
            return;
         }

         if (!hierarchy.isInFolderDatabase()) {
            String hierarchyName = serviceRecord.getName();
            int desktopUID = this.computeDesktopUID(0, 0, hierarchyName);
            hierarchy.establishEmailFolder(0, 0, 0, hierarchyName, desktopUID);
         }

         int folderType = 5;
         switch (type) {
            case -1:
               break;
            case 0:
            default:
               folderType = 0;
               break;
            case 1:
               folderType = 1;
               break;
            case 2:
               folderType = 2;
               break;
            case 3:
               folderType = 3;
               break;
            case 4:
               folderType = 4;
               break;
            case 5:
               folderType = 5;
         }

         if (OTAFMEvents.getDebugLevel() >= 1) {
            OTAFMEvents.logEvent(1011041868, serviceRecord, name, 5);
         }

         int desktopUID = this.computeDesktopUID(parentFolderId, folderId, name);
         EmailFolderBase folder = (EmailFolderBase)hierarchy.establishEmailFolder(folderId, parentFolderId, folderType, name, desktopUID);
         folder.setFolderAttributes(this.convertOTAFMAttributes(attributes));
      }
   }

   @Override
   public final void deleteFolderCommand(int folderId, int deleteFlags, Object context) {
      if (!this._folderListSynced) {
         ServiceRecord serviceRecord = this.getServiceRecord(context);
         EmailHierarchy hierarchy = this.getHierarchy(serviceRecord);
         if (hierarchy != null) {
            EmailFolder folder = (EmailFolder)hierarchy.getFolder(folderId);
            if (folder != null && folder.isInFolderDatabase()) {
               OTAFMEvents.logEvent(1011107404, serviceRecord, folderId, 5);
               synchronized (FolderHierarchies.getLockObject()) {
                  boolean deleteSubFolders = (deleteFlags & 2) != 0;
                  boolean deleteMessages = (deleteFlags & 1) != 0;
                  this.removeAllSyncableFolders(hierarchy, folder, deleteSubFolders, deleteMessages);
                  hierarchy.commitSubtree(true);
               }

               OTAFMEvents.logEvent(558255172, 5);
            }
         }
      }
   }

   @Override
   public final void modifyFolderCommand(int folderId, int parentFolderId, String name, int type, int attributes, Object context) {
      if (!this._folderListSynced) {
         ServiceRecord serviceRecord = this.getServiceRecord(context);
         EmailHierarchy hierarchy = this.getHierarchy(serviceRecord);
         if (hierarchy != null) {
            EmailFolder folder = (EmailFolder)hierarchy.getFolder(folderId);
            if (folder != null && folder.isInFolderDatabase()) {
               OTAFMEvents.logEvent(1011697228, serviceRecord, folderId, 5);
               synchronized (FolderHierarchies.getLockObject()) {
                  Folder parentFolder = hierarchy.getFolder(parentFolderId);
                  if (parentFolder instanceof EmailFolderBase && parentFolder != folder.getParentFolder()) {
                     folder.setParentFolder((EmailFolderBase)parentFolder);
                  }

                  folder.setFriendlyName(name);
                  folder.setFolderType(type);
                  folder.setFolderAttributes(this.convertOTAFMAttributes(attributes));
                  hierarchy.updateEmailFolder(folder);
                  return;
               }
            }
         }
      }
   }

   @Override
   public final void messageListAckCommand(int messageListId, Object context) {
      ServiceRecord serviceRecord = this.getServiceRecord(context);
      if (serviceRecord != null) {
         Object payload = this._reliableService.purgeTransactions(serviceRecord, this._messageListCommandRecognizer);
         boolean processed = false;
         if (payload instanceof MessageListCommand) {
            MessageListCommand command = (MessageListCommand)payload;
            EmailHierarchy hierarchy = this.getHierarchy(serviceRecord);
            if (hierarchy != null) {
               GhostMessageData ghostData = (GhostMessageData)hierarchy.getGhostMessageData();
               synchronized (ghostData.getLock()) {
                  if (ghostData.getMessageListId() == command.getMessageListId()) {
                     processed = true;
                  }
               }
            }
         }

         if (processed) {
            OTAFMEvents.logEvent(1296843051, serviceRecord, 5);
            return;
         }

         OTAFMEvents.logEvent(1296843053, serviceRecord, 5);
      }
   }

   @Override
   public final void unknownCommand(int commandId, Object context) {
      ServiceRecord serviceRecord = this.getServiceRecord(context);
      OTAFMEvents.logEvent(1012223563, serviceRecord, commandId, 2);
   }

   @Override
   public final void syncEventOccurred(int eventId, Object object) {
      switch (eventId) {
         case 1:
         default:
            OTAFMEvents.logEvent(1398361643, 5);
            this._syncActive = true;
            this._serviceBooksSynced = false;
            this._messageListRestored = false;
            this._folderListSynced = false;
            return;
         case 2:
            OTAFMEvents.logEvent(1398361645, 5);
            this._syncActive = false;
            if (this._serviceBooksSynced) {
               this._reliableService.flushTransactions();
               this._workerThread.serviceRecordsChanged();
               this._workerThread.sendConfigurationQuery();
               this._serviceBooksSynced = false;
            }

            if (this._messageListRestored) {
               this._workerThread.setRestoreMessageListFlag();
               if (!this._folderListSynced) {
                  this._workerThread.messageListRestored();
               }

               this._configManager.messageListRestored();
               this._messageListRestored = false;
            }

            if (this._folderListSynced) {
               this._workerThread.folderListSynced();
               this._folderListSynced = false;
            }
         case 0:
      }
   }

   private final void serviceRecordAdded(ServiceRecord serviceRecord) {
      OTAFMEvents.logEvent(1397899588, serviceRecord, 0);
      if (this._syncActive) {
         this._serviceBooksSynced = true;
      } else {
         this._configManager.serviceRecordAdded(serviceRecord);
         this._workerThread.serviceRecordsChanged();
      }
   }

   public static final void intializeOTAMessageSync() {
      Proxy proxy = Proxy.getInstance();
      OTAFMConfigurationManagerImpl.register();
      OTAMessageSync instance = new OTAMessageSync();
      proxy.submitRunnable(instance);
   }

   private final void lowerThreadPriority(Object context) {
      if (ContextObject.get(context, -4249701573962589967L) == null) {
         Thread thread = Thread.currentThread();
         int priority = thread.getPriority();
         ContextObject.put(context, -4249701573962589967L, new Object(priority));
         priority -= 2;
         if (priority < 1) {
            priority = 1;
         }

         thread.setPriority(priority);
      }
   }

   private final void restoreThreadPriority(Object context) {
      Integer originalPriority = (Integer)ContextObject.get(context, -4249701573962589967L);
      if (originalPriority != null) {
         Thread thread = Thread.currentThread();
         thread.setPriority(originalPriority);
      }
   }

   private final EmailMessageModelImpl getMessage(int messageRefId) {
      return (EmailMessageModelImpl)MessageLookups.get(-4420850319371185992L, messageRefId);
   }

   private final EmailHierarchy getHierarchy(ServiceRecord serviceRecord) {
      return serviceRecord != null ? EmailHierarchy.getEmailHierarchy(serviceRecord, true) : null;
   }

   private final ServiceRecord getServiceRecord(Object context) {
      return (ServiceRecord)ContextObject.get(context, -6095803566992128485L);
   }

   private final void itpolicyChanged() {
      OTAFMEvents.logEvent(1011438672, 0);
      ServiceRecord[] serviceRecords = this._serviceBook.findRecordsByCid("CMIME");

      for (int i = serviceRecords.length - 1; i >= 0; i--) {
         ServiceRecord serviceRecord = serviceRecords[i];
         OTAFMConfiguration configuration = this._configManager.getConfiguration(serviceRecord);
         if (configuration != OTAFMConfiguration.getDisabledConfiguration()) {
            this._configManager.updateConfiguration(serviceRecord, configuration, false);
         }
      }
   }

   private final boolean commandFromServer(Object context) {
      return ContextObject.get(context, 8797645593349626617L) != null;
   }

   @Override
   public final void flushBatchedCommands() {
      this._workerThread.flushTransmitBuffers(false);
   }

   private final void checkBufferReadyToSend(ServiceRecord serviceRecord, ServiceSyncInfo syncInfo) {
      if (syncInfo.size() > 1250) {
         this._workerThread.flushTransmitBuffer(serviceRecord, false);
      }
   }

   private final int getMessageFolderId(EmailHierarchy hierarchy, EmailMessageModel emailMessage) {
      EmailFolder folder = EmailHierarchy.getEmailFolder(emailMessage.getFolderId());
      return folder == null ? 0 : folder.getFolderId();
   }

   private final void serviceRecordRemoved(ServiceRecord serviceRecord) {
      OTAFMEvents.logEvent(1397903949, serviceRecord, 0);
      this._configManager.serviceRecordRemoved(serviceRecord);
      this._workerThread.serviceRecordsChanged();
      this._reliableService.serviceRecordRemoved(serviceRecord);
   }

   private final void serviceRecordUpdated(ServiceRecord oldSR, ServiceRecord newSR) {
      OTAFMEvents.logEvent(1397904720, newSR, 0);
      if (this._syncActive) {
         this._serviceBooksSynced = true;
      } else {
         this._configManager.serviceRecordUpdated(oldSR, newSR);
         this._workerThread.serviceRecordsChanged();
      }
   }

   private final boolean syncMessageMoves(ServiceRecord serviceRecord) {
      OTAFMConfiguration configuration = this._configManager.getConfiguration(serviceRecord);
      return configuration.getWirelessFilingEnabled();
   }

   private final boolean syncMessageStatusUpdates(ServiceRecord serviceRecord) {
      OTAFMConfiguration configuration = this._configManager.getConfiguration(serviceRecord);
      return configuration.getWirelessStatusUpdatesEnabled();
   }

   private OTAMessageSync() {
   }

   private final void removeAllSyncableFolders(EmailHierarchy hierarchy, EmailFolderBase folderBase, boolean deleteSubFolders, boolean deleteMessages) {
      if (OTAFMEvents.getDebugLevel() >= 1) {
         OTAFMEvents.logEvent(759581764, folderBase.getFolderId(), 5);
      }

      synchronized (FolderHierarchies.getLockObject()) {
         if ((folderBase.getFolderAttributes() & 1) == 0) {
            if (deleteSubFolders && folderBase.containsSubFolders()) {
               Enumeration e = folderBase.getSubFolders();

               while (e.hasMoreElements()) {
                  this.removeAllSyncableFolders(hierarchy, (EmailFolderBase)e.nextElement(), deleteSubFolders, deleteMessages);
               }
            }

            if (folderBase != hierarchy && folderBase.isInFolderDatabase()) {
               if (deleteMessages) {
                  this.deleteMessagesFromFolder(hierarchy, folderBase);
               }

               hierarchy.disestablishEmailFolder(folderBase);
            }
         }

         EmailHierarchy.purge();
         PersistentObject.commit(hierarchy);
      }
   }

   private final void deleteMessagesFromFolder(EmailHierarchy hierarchy, EmailFolderBase folder) {
      this.deleteMessagesFromFolder(hierarchy, folder, true);
      this.deleteMessagesFromFolder(hierarchy, folder, false);
   }

   private final void deleteMessagesFromFolder(EmailHierarchy hierarchy, EmailFolderBase folder, boolean filed) {
      int folderId = folder.getFolderId();
      ReadableList messages = null;
      if (filed) {
         messages = (ReadableList)((EmailFolder)hierarchy.getFiledFolder()).getContainedItems();
      } else {
         messages = (ReadableList)((EmailFolder)hierarchy.getUnfiledFolder()).getContainedItems();
      }

      for (int i = messages.size() - 1; i >= 0; i--) {
         Object element = messages.getAt(i);
         if (element instanceof EmailMessageModel) {
            EmailMessageModel message = (EmailMessageModel)element;
            EmailFolder messageFolder = EmailHierarchy.getEmailFolder(message.getFolderId());
            if (messageFolder != null && messageFolder.getFolderId() == folderId && message instanceof Object) {
               ActionProvider actionProvider = (ActionProvider)message;
               OTAFMEvents.logEvent(1044661580, message.getCMIMEReferenceIdentifier(), 5);
               actionProvider.perform(-3967872215949752466L, null);
            }
         }
      }
   }

   private final int convertOTAFMAttributes(int otafmAttributes) {
      int deviceAttributes = 0;
      if ((otafmAttributes & 1) != 0) {
         deviceAttributes |= 1;
      }

      if ((otafmAttributes & 2) != 0) {
         deviceAttributes |= 2;
      }

      if ((otafmAttributes & 8) != 0) {
         deviceAttributes |= 8;
      }

      if ((otafmAttributes & 4) != 0) {
         deviceAttributes |= 4;
      }

      return deviceAttributes;
   }

   private final MessageListCommand buildMessageListCommand(ServiceRecord serviceRecord) {
      EmailHierarchy hierarchy = this.getHierarchy(serviceRecord);
      if (hierarchy == null) {
         return null;
      }

      OTAFMConfiguration configuration = this._configManager.getConfiguration(serviceRecord);
      if (configuration == OTAFMConfiguration.getDisabledConfiguration()) {
         return null;
      }

      MessageListCommand request = new MessageListCommand();

      try {
         request.addConfiguration(configuration);
         ServiceSyncInfo syncInfo = this._configManager.getServiceSyncInfo(serviceRecord);
         int messageListId = CMIMEUtilities.newDeviceSideIdentifier();
         boolean restoreMessageList = syncInfo.getRestoreMessageList();
         if (!restoreMessageList) {
            TransactionRecord previousTransaction = (TransactionRecord)this._reliableService
               .locateTransaction(serviceRecord, this._messageListCommandRecognizer);
            if (previousTransaction != null
               && ((MessageListCommand)previousTransaction._payload).isRestoreMessageList()
               && !previousTransaction.sentSuccessfully()) {
               restoreMessageList = true;
            }
         }

         if (restoreMessageList) {
            request.beginRestoreMessageListCommand(messageListId);
         } else {
            request.beginMessageListCommand(messageListId);
         }

         EmailFolder unfiledFolder = (EmailFolder)hierarchy.getUnfiledFolder();
         Collection unfiledItems = unfiledFolder.getContainedItems();
         int messageCount = 0;
         int maxMessageCount = (62000 - 20 * hierarchy.getActiveFolderCount()) / 5 - 500;
         Object[] items = new Object[0];
         if (unfiledItems instanceof Object) {
            ReadableList list = (ReadableList)unfiledItems;
            Array.resize(items, list.size());
            int nextInboxIndex = 0;
            int nextSentIndex = items.length - 1;
            int inboxFolderId = EmailHierarchy.getEmailFolder(hierarchy.getInboxFolder()) != null
               ? EmailHierarchy.getEmailFolder(hierarchy.getInboxFolder()).getFolderId()
               : -1;
            int sentFolderId = EmailHierarchy.getEmailFolder(hierarchy.getSentFolder()) != null
               ? EmailHierarchy.getEmailFolder(hierarchy.getSentFolder()).getFolderId()
               : -1;

            for (int i = list.size() - 1; i >= 0 && messageCount < maxMessageCount; i--) {
               Object item = list.getAt(i);
               if (item instanceof EmailMessageModel) {
                  EmailMessageModel email = (EmailMessageModel)item;
                  int folderId = this.getMessageFolderId(hierarchy, (EmailMessageModel)item);
                  if (folderId == inboxFolderId) {
                     items[nextInboxIndex++] = item;
                     messageCount++;
                  } else if (folderId == sentFolderId) {
                     items[nextSentIndex--] = item;
                     messageCount++;
                  }
               }
            }

            this.addMessageListFolderData(request, inboxFolderId, items, 0, nextInboxIndex);
            this.addMessageListFolderData(request, sentFolderId, items, nextSentIndex + 1, items.length);
            if (nextInboxIndex > nextSentIndex) {
               Array.resize(items, 0);
            } else {
               int nextIndex = 0;

               for (int i = list.size() - 1; i >= 0 && messageCount < maxMessageCount; i--) {
                  Object item = list.getAt(i);
                  if (item instanceof EmailMessageModel) {
                     EmailMessageModel email = (EmailMessageModel)item;
                     int folderId = this.getMessageFolderId(hierarchy, (EmailMessageModel)item);
                     if (folderId != inboxFolderId && folderId != sentFolderId) {
                        EmailFolder folder = (EmailFolder)hierarchy.getFolder(folderId);
                        if (folder == null || (folder.getFolderAttributes() & 1) == 0) {
                           items[nextIndex++] = item;
                           messageCount++;
                        }
                     }
                  }
               }

               Array.resize(items, nextIndex);
            }
         }

         EmailFolder filedFolder = (EmailFolder)hierarchy.getFiledFolder();
         Collection filedItems = filedFolder.getContainedItems();
         if (filedItems instanceof Object) {
            ReadableList list = (ReadableList)filedItems;
            int nextIndex = items.length;
            Array.resize(items, nextIndex + list.size());

            for (int i = list.size() - 1; i >= 0 && messageCount < maxMessageCount; i--) {
               Object item = list.getAt(i);
               if (item instanceof EmailMessageModel) {
                  EmailMessageModel email = (EmailMessageModel)item;
                  EmailFolder folder = (EmailFolder)hierarchy.getFolder(email.getFolderId());
                  if (folder == null || (folder.getFolderAttributes() & 1) == 0) {
                     items[nextIndex++] = email;
                     messageCount++;
                  }
               }
            }

            Array.resize(items, nextIndex);
            Arrays.sort(items, new FolderIdComparator(hierarchy));
            int currentFolderId = -1;
            int startIndex = -1;

            for (int i = 0; i < items.length; i++) {
               int folderId = this.getMessageFolderId(hierarchy, (EmailMessageModel)items[i]);
               if (folderId != currentFolderId) {
                  if (currentFolderId != -1) {
                     this.addMessageListFolderData(request, currentFolderId, items, startIndex, i);
                  }

                  currentFolderId = folderId;
                  startIndex = i;
               }
            }

            if (currentFolderId != -1) {
               this.addMessageListFolderData(request, currentFolderId, items, startIndex, items.length);
            }
         }

         GhostMessageData ghostData = (GhostMessageData)hierarchy.getGhostMessageData();
         synchronized (ghostData.getLock()) {
            ghostData.removeDeletedEntries();
            int purgeCount = ghostData.size();
            if (purgeCount > 0) {
               int[] indicies = new int[purgeCount];
               int i = 0;

               while (i < purgeCount) {
                  indicies[i] = i++;
               }

               Arrays.sort(indicies, 0, purgeCount, new IndirectFolderIdComparator(hierarchy));
               i = -1;
               int startIndex = -1;

               for (int ix = 0; ix < purgeCount; ix++) {
                  int folderId = EmailSyncState.getFolderId(ghostData.getInfoAt(indicies[ix]));
                  if (folderId != i) {
                     if (i != -1) {
                        this.addPurgedMessageListFolderData(request, i, hierarchy, indicies, startIndex, ix);
                     }

                     i = folderId;
                     startIndex = ix;
                  }
               }

               if (i != -1) {
                  this.addPurgedMessageListFolderData(request, i, hierarchy, indicies, startIndex, purgeCount);
               }
            }

            ghostData.setMessageListId(messageListId);
            request.setGhostMessageCount(purgeCount);
         }
      } finally {
         ;
      }

      request.endMessageListCommand();
      return request;
   }

   private final int computeDesktopUID(int parentFolderId, int folderId, String name) {
      byte[] data = name.getBytes();
      Array.resize(data, data.length + 4);
      System.arraycopy(data, 0, data, 4, data.length - 4);
      data[0] = (byte)(folderId & 0xFF);
      data[1] = (byte)((folderId & 0xFF00) >> 8);
      data[2] = (byte)(parentFolderId & 0xFF);
      data[3] = (byte)((parentFolderId & 0xFF00) >> 8);
      SHA1Digest digest = (SHA1Digest)(new Object());
      digest.update(data, 0, data.length);
      byte[] digestData = new byte[digest.getDigestLength()];
      digest.getDigest(digestData, 0, false);
      return (digestData[3] & 0xFF) << 24 | (digestData[2] & 0xFF) << 16 | (digestData[1] & 0xFF) << 8 | digestData[0] & 0xFF;
   }

   private final void addMessageListFolderData(RIMMessagingFolderManagement request, int folderId, Object[] items, int startIndex, int endIndex) {
      int count = endIndex - startIndex;
      if (count > 0) {
         request.beginMessagesInFolder(folderId, count);

         for (int i = startIndex; count > 0; i++) {
            EmailMessageModel message = (EmailMessageModel)items[i];
            request.addMessageInFolderEntry(message.getCMIMEReferenceIdentifier(), (message.getFlags() & 1) != 0);
            count--;
         }

         request.endMessagesInFolder();
      }
   }

   private final void addPurgedMessageListFolderData(
      RIMMessagingFolderManagement request, int folderId, EmailHierarchy hierarchy, int[] items, int startIndex, int endIndex
   ) {
      int count = endIndex - startIndex;
      if (count > 0) {
         EmailFolder deletedFolder = (EmailFolder)hierarchy.getDeletedFolder();
         if (deletedFolder != null && folderId == deletedFolder.getFolderId()) {
            folderId = 0;
         }

         request.beginMessagesInFolder(folderId, count);

         for (int i = startIndex; count > 0; i++) {
            int refId = hierarchy.getGhostMessageTag(items[i]);
            int flags = EmailSyncState.getFlags(hierarchy.getGhostMessageInfo(items[i]));
            boolean opened = EmailSyncState.isRead(flags);
            boolean deleted = EmailSyncState.isDeleted(flags);
            request.addPurgedMessageInFolderEntry(refId, opened, deleted);
            count--;
         }

         request.endMessagesInFolder();
      }
   }

   private final void transmitObject(ServiceRecord param1, RIMMessagingFolderManagement param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: ldc_w 1414676814
      // 03: aload 1
      // 04: aload 2
      // 05: invokevirtual net/rim/device/apps/api/transmission/rim/RIMMessagingFolderManagement.size ()I
      // 08: bipush 5
      // 0a: bipush 1
      // 0b: invokestatic net/rim/device/apps/internal/blackberryemail/otasync/OTAFMEvents.logEvent (ILnet/rim/device/api/servicebook/ServiceRecord;IIZ)V
      // 0e: aload 0
      // 0f: getfield net/rim/device/apps/internal/blackberryemail/otasync/OTAMessageSync._reliableService Lnet/rim/device/apps/internal/blackberryemail/otasync/ReliableTransmissionHelper;
      // 12: aload 1
      // 13: aload 2
      // 14: invokevirtual net/rim/device/apps/internal/blackberryemail/otasync/ReliableTransmissionHelper.transmitObject (Lnet/rim/device/api/servicebook/ServiceRecord;Ljava/lang/Object;)V
      // 17: return
      // 18: astore 3
      // 19: return
      // 1a: astore 3
      // 1b: return
      // try (0 -> 12): 13 null
      // try (0 -> 12): 15 null
   }

   public static final OTAMessageSync getInstance() {
      if (_instance == null) {
         PersistentObject lockObject = RIMPersistentStore.getPersistentObject(8822586609645349199L);

         do {
            synchronized (lockObject) {
               _instance = (OTAMessageSync)OTAFMControl.getControlInstance();
               if (_instance == null) {
                  try {
                     lockObject.wait();
                  } finally {
                     continue;
                  }
               }
            }
         } while (_instance == null);
      }

      return _instance;
   }

   private final void initialize() {
      this._serviceBook = ServiceBook.getSB();
      this._messageListCommandRecognizer = new MessageListCommand();
      OTAFMEvents.register();
      this._configManager = (OTAFMConfigurationManagerImpl)OTAFMConfigurationManager$Instance.getInstance();
      VerbFactoryRepository.addFactory(2729258854446987021L, new MessageListScreenVerbFactory(this._configManager));
      VerbFactoryRepository.addFactory(4950224479719224677L, new MessageListOptionsScreenVerbFactory(this._configManager));
      this._workerThread = new SyncWorkerThread(this);
      this._workerThread.start();
      this._flushBuffersThread = new FlushBuffersThread(this);
      this._flushBuffersThread.start();
   }
}
