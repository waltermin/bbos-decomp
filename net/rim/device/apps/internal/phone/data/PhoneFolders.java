package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollection;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollectionEncryptor;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.api.messaging.util.SortedCollection;
import net.rim.device.apps.api.utility.lowMemory.PurgeManager;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.phone.api.Out;
import net.rim.device.apps.internal.phone.api.PhoneCallInitialData;
import net.rim.device.apps.internal.phone.api.PhoneCallModel;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class PhoneFolders {
   public static final int MISSED_CALL_FOLDER;
   public static final int DEFAULT_FOLDER;
   public static final int DC_CALL_FOLDER;
   public static final int DC_ALERT_FOLDER;
   public static final int FOLDER_COUNT = PhoneUtilities.idenTypeNetwork() ? 4 : 2;
   public static final long PHONE_HIERARCHY_ID;
   public static final long MISSED_CALL_FOLDER_ID;
   public static final long DEFAULT_FOLDER_ID;
   public static final long DC_CALL_FOLDER_ID;
   public static final long DC_ALERT_FOLDER_ID;
   public static final long PHONE_FAMILY;

   public static final void initializePhoneFolderHierarchy() {
      SimpleFolder hierarchy = getPhoneFolderHierarchy();
      if (hierarchy != null) {
         SimpleFolder[] folders = new Object[FOLDER_COUNT];
         folders[1] = (SimpleFolder)hierarchy.getFolder(5390902206192375236L);
         folders[0] = (SimpleFolder)hierarchy.getFolder(7042951934619290849L);
         if (PhoneUtilities.idenTypeNetwork()) {
            folders[2] = (SimpleFolder)hierarchy.getFolder(-1859209320265783789L);
            folders[3] = (SimpleFolder)hierarchy.getFolder(-2025972805868361049L);
         }

         for (int idx = 0; idx < FOLDER_COUNT; idx++) {
            if (folders[idx] == null) {
               return;
            }

            folders[idx].initializeOnSystemStart();
         }

         doRegisterFoldersWithMergedCollection(folders);

         for (int idx = 0; idx < FOLDER_COUNT; idx++) {
            FolderMerge.registerMergedFolder(7509894771240321003L, folders[idx]);
         }

         for (int idx = 0; idx < FOLDER_COUNT; idx++) {
            PurgeManager.getInstance().addCollection((Collection)folders[idx].getContainedItems());
         }
      }

      FolderHierarchies.registerFolderHierarchy(hierarchy);
      CallLogCollection.getInstance().onInitializationComplete();
   }

   public static final int getCallLogCount() {
      int count = 0;
      SimpleFolder[] folders = getPhoneFolders();
      if (folders != null) {
         for (int i = 0; i < folders.length; i++) {
            SimpleFolder folder = folders[i];
            if (folder != null) {
               ReadableList items = (ReadableList)folder.getContainedItems();
               if (items != null) {
                  count += items.size();
               }
            }
         }
      }

      return count;
   }

   private static final SimpleFolder getPhoneFolderHierarchy() {
      SimpleFolder hierarchy = SimpleFolder.getInstance(1212521839578244703L, -6900425462863939467L);
      if (hierarchy == null) {
         hierarchy = SimpleFolder.createInstance(1212521839578244703L, -6900425462863939467L, PhoneResources.getResourceBundle(), 5000, 1);
      }

      synchronized (hierarchy) {
         if (hierarchy.getFolder(7042951934619290849L) == null) {
            hierarchy.putFolder(
               (Folder)(new Object(
                  1212521839578244703L,
                  7042951934619290849L,
                  PhoneResources.getResourceBundle(),
                  6243,
                  "net.rim.device.apps.api.messaging.util.PersistedSortedCollection",
                  hierarchy,
                  1
               ))
            );
         }

         if (hierarchy.getFolder(5390902206192375236L) == null) {
            hierarchy.putFolder(
               (Folder)(new Object(
                  1212521839578244703L,
                  5390902206192375236L,
                  PhoneResources.getResourceBundle(),
                  5001,
                  "net.rim.device.apps.api.messaging.util.PersistedSortedCollection",
                  hierarchy,
                  1
               ))
            );
         }

         if (PhoneUtilities.idenTypeNetwork()) {
            if (hierarchy.getFolder(-1859209320265783789L) == null) {
               hierarchy.putFolder(
                  (Folder)(new Object(
                     1212521839578244703L,
                     -1859209320265783789L,
                     PhoneResources.getResourceBundle(),
                     6247,
                     "net.rim.device.apps.api.messaging.util.PersistedSortedCollection",
                     hierarchy,
                     1
                  ))
               );
            }

            if (hierarchy.getFolder(-2025972805868361049L) == null) {
               hierarchy.putFolder(
                  (Folder)(new Object(
                     1212521839578244703L,
                     -2025972805868361049L,
                     PhoneResources.getResourceBundle(),
                     6248,
                     "net.rim.device.apps.api.messaging.util.PersistedSortedCollection",
                     hierarchy,
                     1
                  ))
               );
            }
         }

         return hierarchy;
      }
   }

   public static final SimpleFolder getDefaultFolder() {
      SimpleFolder hierarchy = getPhoneFolderHierarchy();
      return (SimpleFolder)(hierarchy != null ? hierarchy.getFolder(5390902206192375236L) : null);
   }

   public static final SimpleFolder getMissedCallFolder() {
      SimpleFolder hierarchy = getPhoneFolderHierarchy();
      return (SimpleFolder)(hierarchy != null ? hierarchy.getFolder(7042951934619290849L) : null);
   }

   private static final SimpleFolder getDirectConnectCallFolder() {
      SimpleFolder hierarchy = getPhoneFolderHierarchy();
      return (SimpleFolder)(hierarchy != null ? hierarchy.getFolder(-1859209320265783789L) : null);
   }

   private static final SimpleFolder getDirectConnectAlertFolder() {
      SimpleFolder hierarchy = getPhoneFolderHierarchy();
      return (SimpleFolder)(hierarchy != null ? hierarchy.getFolder(-2025972805868361049L) : null);
   }

   static final SimpleFolder[] getPhoneFolders() {
      SimpleFolder[] folders = new Object[FOLDER_COUNT];
      folders[1] = getDefaultFolder();
      folders[0] = getMissedCallFolder();
      if (PhoneUtilities.idenTypeNetwork()) {
         folders[2] = getDirectConnectCallFolder();
         folders[3] = getDirectConnectAlertFolder();
      }

      return folders;
   }

   public static final SimpleFolder getPhoneFolder(Object item) {
      if (!(item instanceof PhoneCallModelImpl)) {
         return null;
      }

      switch (((PhoneCallModelImpl)item).getType()) {
         case 1:
         case 4:
            return getDefaultFolder();
         case 2:
         case 3:
         default:
            return getMissedCallFolder();
         case 5:
         case 6:
            return getDirectConnectAlertFolder();
         case 7:
            return getDirectConnectCallFolder();
      }
   }

   public static final SimpleFolder getPhoneFolder(long id) {
      if (id == 7042951934619290849L) {
         return getMissedCallFolder();
      } else if (id == -1859209320265783789L) {
         return getDirectConnectCallFolder();
      } else {
         return id == -2025972805868361049L ? getDirectConnectAlertFolder() : getDefaultFolder();
      }
   }

   public static final SimpleFolder getPhoneFolder() {
      return getDefaultFolder();
   }

   private static final void doRegisterFoldersWithMergedCollection(SimpleFolder[] folders) {
      int showCallLogsOption = PhoneOptions.getOptions().getShowCallLogsOption();
      switch (showCallLogsOption) {
         case -1:
         case 2:
            break;
         case 0:
            registerMergedFolder(folders[0]);
            if (PhoneUtilities.idenTypeNetwork()) {
               registerMergedFolder(folders[3]);
            }
            break;
         case 1:
         default:
            registerMergedFolder(folders[1]);
            registerMergedFolder(folders[0]);
            if (PhoneUtilities.idenTypeNetwork()) {
               registerMergedFolder(folders[2]);
               registerMergedFolder(folders[3]);
               return;
            }
            break;
         case 3:
            registerMergedFolder(folders[1]);
            registerMergedFolder(folders[0]);
            if (PhoneUtilities.idenTypeNetwork()) {
               registerMergedFolder(folders[3]);
               return;
            }
      }
   }

   private static final void registerMergedFolder(SimpleFolder folder) {
      FolderMerge.registerMergedFolder(-5581791943352753293L, folder);
      FolderMerge.registerMergedFolder(2993144521330132876L, folder);
   }

   public static final void registerFoldersWithMergedMessageCollection() {
      SimpleFolder[] folders = getPhoneFolders();

      for (int idx = 0; idx < folders.length; idx++) {
         SimpleFolder folder = folders[idx];
         FolderMerge.deregisterMergedFolder(-5581791943352753293L, folder);
         FolderMerge.deregisterMergedFolder(2993144521330132876L, folder);
      }

      doRegisterFoldersWithMergedCollection(folders);
   }

   public static final void registerFoldersWithNewShowCallLogsOption(int oldOption, int newOption) {
      registerFoldersWithMergedMessageCollection();
   }

   public static final void deregisterPhoneFolderHierarchyWithMergedMessageCollection() {
      SimpleFolder folder = getDefaultFolder();
      if (folder != null) {
         FolderMerge.deregisterMergedFolder(-5581791943352753293L, folder);
         FolderMerge.deregisterMergedFolder(2993144521330132876L, folder);
      }

      folder = getMissedCallFolder();
      if (folder != null) {
         FolderMerge.deregisterMergedFolder(-5581791943352753293L, folder);
         FolderMerge.deregisterMergedFolder(2993144521330132876L, folder);
      }
   }

   public static final boolean purgeOldCallLogItems(long threshold) {
      SimpleFolder[] folders = getPhoneFolders();
      if (folders != null) {
         for (int idx = 0; idx < folders.length; idx++) {
            ReadableList collection = (ReadableList)folders[idx].getContainedItems();
            if (!PurgeManager.getInstance().purgeCollection(collection, 2, threshold)) {
               return false;
            }
         }
      }

      return true;
   }

   public static final Object replaceCallLogNotes(Object callLog, String newNotes) {
      if (!(callLog instanceof PhoneCallModelImpl)) {
         return null;
      }

      PhoneCallModelImpl writableCallLog = getWritableCallLog((PhoneCallModelImpl)callLog);
      BodyModel oldNotesModel = (BodyModel)writableCallLog.getAt(2);
      oldNotesModel.setText(newNotes);
      replaceItem(callLog, writableCallLog);
      return writableCallLog;
   }

   public static final void replaceItem(Object oldItem, Object newItem) {
      if (oldItem != null && newItem != null) {
         Folder folder = getPhoneFolder(oldItem);
         if (oldItem == newItem) {
            if (folder != null) {
               SortedCollection callLogs = (SortedCollection)folder.getContainedItems();
               callLogs.elementUpdated(callLogs, oldItem, newItem);
            }
         } else {
            if (folder != null) {
               WritableSet callLogs = (WritableSet)folder.getContainedItems();
               if (!(newItem instanceof PhoneCallModelImpl)) {
                  ObjectGroup.createGroupIgnoreTooBig(newItem);
               } else {
                  ((PhoneCallModelImpl)newItem).groupPayload();
               }

               callLogs.add(newItem);
               callLogs.remove(oldItem);
               VoiceUnopenedCount.getInstance().itemRestored(newItem);
               VoiceUnopenedCount.getInstance().itemDeleted(oldItem);
               CallLogCollection.getInstance().callLogReplaced((PhoneCallModelImpl)newItem);
            }
         }
      }
   }

   static final void addCallLogs(Object[] logs, boolean updateCounters) {
      for (int i = logs.length - 1; i >= 0; i--) {
         addItem(logs[i], updateCounters);
      }
   }

   public static final void addItem(Object item) {
      addItem(item, true);
   }

   public static final void addItem(Object item, boolean updateCounters) {
      if (updateCounters) {
         boolean isUnopened = VoiceUnopenedCount.isUnopenedItem(item);
         boolean isNew = VoiceUnopenedCount.isNewItem(item);
         int addressCardUID = (int)((CallerIDInfo)((PhoneCallModelImpl)item).getCallerIDInfo()).getUid();
         VoiceUnopenedCount.getInstance().itemAdded(isUnopened, isNew, addressCardUID);
      }

      if (PersistentContent.getTicket() == null) {
         PhoneCallCollection.getInstance().deferCallLogAddition(item);
      } else {
         addCallLog(item);
         CallLogCollection.getInstance().callLogAdded((PhoneCallModelImpl)item);
      }
   }

   public static final void restoreItem(Object item) {
      if (isValidRestoreItem(item)) {
         addCallLog(item);
         VoiceUnopenedCount.getInstance().itemRestored(item);
         CallLogCollection.getInstance().callLogRestored((PhoneCallModelImpl)item);
      }
   }

   private static final boolean isValidRestoreItem(Object item) {
      if (!(item instanceof PhoneCallModelImpl)) {
         return true;
      } else {
         PhoneCallModelImpl callLog = (PhoneCallModelImpl)item;
         if (callLog.getCallerIDInfo() == null) {
            Out.p("Phone: restored call log was missing caller id info.");
            return false;
         } else {
            return true;
         }
      }
   }

   public static final PhoneCallModelImpl logMissedCall(CallerIDInfo callerIDInfo, int lineId) {
      PhoneCallInitialData data = new PhoneCallInitialData(0, (byte)2, 0, callerIDInfo, null, lineId);
      PhoneCallModelImpl callLog = (PhoneCallModelImpl)PhoneUtilities.createPhoneCallModel(data);
      if (callLog == null) {
         System.out.println("failed to log missed call");
         return callLog;
      } else {
         callLog.setNew(true);
         logPhoneCall(callLog);
         return callLog;
      }
   }

   public static final void logPhoneCall(PhoneCallModel callLog) {
      if (callLog != null) {
         if (callLog instanceof Object) {
            EncryptableProvider encryptable = (EncryptableProvider)callLog;
            if (!encryptable.checkCrypt(true, true)) {
               encryptable.reCrypt(true, true);
            }
         }

         addItem(callLog, true);
      }
   }

   public static final PhoneCallModelImpl getWritableCallLog(PhoneCallModelImpl model) {
      model.ungroupPayload();
      return model;
   }

   public static final PhoneCallModelImpl changeCallLogElapsedTime(PhoneCallModelImpl callLog, int elapsedTime) {
      PhoneCallModelImpl writableModel = getWritableCallLog(callLog);
      writableModel.updateElapsedTime(elapsedTime);
      replaceItem(callLog, writableModel);
      return writableModel;
   }

   public static final PhoneCallModelImpl changeCallLogType(PhoneCallModelImpl callLog, byte newType) {
      PhoneCallModelImpl writableModel = getWritableCallLog(callLog);
      byte origType = callLog.getType();
      writableModel.updateCallType(newType);
      replaceItem(callLog, writableModel);
      if (origType == 2 && newType == 3) {
         VoiceUnopenedCount.getInstance().itemMarkedOpened();
         MissedCallIndicator.getInstance().disable();
         return writableModel;
      }

      if (origType == 3 && newType == 2) {
         VoiceUnopenedCount.getInstance().itemMarkedUnopened();
      }

      return writableModel;
   }

   public static final PhoneCallModelImpl updateCallerIDInfo(PhoneCallModelImpl callLog, boolean markOpened) {
      PhoneCallModelImpl newCallLog = getWritableCallLog(callLog);
      int size = callLog.size();

      for (int i = size - 1; i >= 3; i--) {
         CallerIDInfo oldCidi = (CallerIDInfo)callLog.getAt(i);
         CallerIDInfo newCidi = new CallerIDInfo(oldCidi.getNumber(), null, oldCidi.isIncomingCall(), false);
         newCallLog.remove(oldCidi);
         newCallLog.add(newCidi);
      }

      if (markOpened) {
         newCallLog.updateCallType((byte)3);
      }

      replaceItem(callLog, newCallLog);
      return newCallLog;
   }

   private static final void addCallLog(Object item) {
      if (item instanceof Object) {
         Folder phoneFolder = getPhoneFolder(item);
         if (phoneFolder != null) {
            if (item instanceof PhoneCallModelImpl) {
               PhoneCallModelImpl callLog = (PhoneCallModelImpl)item;
               WritableSet phoneItems = (WritableSet)phoneFolder.getContainedItems();
               callLog.groupPayload();
               phoneItems.add(callLog);
               MessageLookups.put(-7579072715623987642L, callLog.getRefId(), callLog);
               return;
            }

            ObjectGroup.createGroupIgnoreTooBig(item);
            WritableSet phoneItems = (WritableSet)phoneFolder.getContainedItems();
            phoneItems.add(item);
         }
      }
   }

   static final void removeItem(Object item, boolean notifyCallLogCollection) {
      Folder folder = getPhoneFolder(item);
      if (folder != null) {
         SortedCollection phoneItems = (SortedCollection)folder.getContainedItems();
         phoneItems.remove(item, true);
         if (item instanceof PhoneCallModelImpl) {
            PhoneCallModelImpl callLog = (PhoneCallModelImpl)item;
            if (notifyCallLogCollection) {
               CallLogCollection.getInstance().callLogRemoved(callLog);
            }

            MessageLookups.remove(-7579072715623987642L, callLog.getRefId());
            VoiceUnopenedCount.getInstance().itemDeleted(item);
            if (callLog.getType() == 2) {
               MissedCallIndicator.getInstance().disable();
            }
         }
      }
   }

   public static final void removeItem(Object item) {
      removeItem(item, true);
   }

   public static final PersistedSortedCollection getContainedItems() {
      return getContainedItems(5390902206192375236L);
   }

   public static final PersistedSortedCollection getContainedItems(long folderId) {
      Folder phoneFolder = getPhoneFolder(folderId);
      return (PersistedSortedCollection)(phoneFolder != null ? phoneFolder.getContainedItems() : null);
   }

   public static final void crypt(int contentProtectionGeneration) {
      PersistedSortedCollectionEncryptor.crypt(1212521839578244703L, contentProtectionGeneration);
   }
}
