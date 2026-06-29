package net.rim.device.apps.internal.mms;

import java.util.Enumeration;
import net.rim.device.api.collection.IntRangedActionTarget;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.api.utility.lowMemory.PurgeManager;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.resources.MMSResources;

public final class MMSStorage {
   private static final long MMS_HIERARCHY_ID;
   private static final long MMS_FAMILY;
   public static final long MMS_FOLDER_INBOX;
   public static final long MMS_FOLDER_OUTBOX;
   public static final long MMS_FOLDER_ORPHANED_SAVED;

   private MMSStorage() {
   }

   static final void registerOnceOnSystemStart() {
      SimpleFolder hierarchy = SimpleFolder.getInstance(3704547669295631919L, -5085817815977500958L);
      SimpleFolder inboxFolder = null;
      SimpleFolder outboxFolder = null;
      SimpleFolder orphanedSavedFolder = null;
      if (hierarchy == null) {
         hierarchy = SimpleFolder.createInstance(3704547669295631919L, -5085817815977500958L, MMSResources.getResourceBundle(), 0, 1);
      }

      inboxFolder = getFolderFromHierarchy(hierarchy, 8244211460627721111L, 1, 1);
      outboxFolder = getFolderFromHierarchy(hierarchy, 949632297110531729L, 2, 1);
      orphanedSavedFolder = getFolderFromHierarchy(hierarchy, -7297051376619864492L, 3, 0);
      if (hierarchy != null && inboxFolder != null && outboxFolder != null && orphanedSavedFolder != null) {
         PurgeManager purgeManager = PurgeManager.getInstance();
         FolderHierarchies.registerFolderHierarchy(hierarchy);
         boolean addToMessageList = MMSUtilities.addToMessageList();
         if (addToMessageList) {
            FolderMerge.registerMergedFolder(-5581791943352753293L, inboxFolder);
            FolderMerge.registerMergedFolder(2993144521330132876L, inboxFolder);
         }

         FolderMerge.registerMergedFolder(7509894771240321003L, inboxFolder);
         FolderMerge.registerMergedFolder(-942103673428357213L, inboxFolder);
         FolderMerge.registerMergedFolder(-4696470826620059293L, inboxFolder);
         inboxFolder.initializeOnSystemStart();
         purgeManager.addCollection(inboxFolder.getContainedItems());
         if (addToMessageList) {
            FolderMerge.registerMergedFolder(-5581791943352753293L, outboxFolder);
            FolderMerge.registerMergedFolder(2993144521330132876L, outboxFolder);
         }

         FolderMerge.registerMergedFolder(7509894771240321003L, outboxFolder);
         FolderMerge.registerMergedFolder(-942103673428357213L, outboxFolder);
         FolderMerge.registerMergedFolder(-4696470826620059293L, outboxFolder);
         outboxFolder.initializeOnSystemStart();
         purgeManager.addCollection(outboxFolder.getContainedItems());
         FolderMerge.registerMergedFolder(6368823655991217730L, orphanedSavedFolder);
         FolderMerge.registerMergedFolder(7509894771240321003L, orphanedSavedFolder);
         orphanedSavedFolder.initializeOnSystemStart();
         PersistentContent.addListener(new MMSStorage$MyPersistentContentListener(null));
      } else {
         throw new Object("Unable to create MMS folder hierarchy.");
      }
   }

   static final void addToMessageList(boolean add) {
      SimpleFolder hierarchy = SimpleFolder.getInstance(3704547669295631919L, -5085817815977500958L);
      if (hierarchy != null) {
         SimpleFolder inboxFolder = getFolderFromHierarchy(hierarchy, 8244211460627721111L, 1, 1);
         SimpleFolder outboxFolder = getFolderFromHierarchy(hierarchy, 949632297110531729L, 2, 1);
         if (add) {
            FolderMerge.registerMergedFolder(-5581791943352753293L, inboxFolder);
            FolderMerge.registerMergedFolder(2993144521330132876L, inboxFolder);
            FolderMerge.registerMergedFolder(-5581791943352753293L, outboxFolder);
            FolderMerge.registerMergedFolder(2993144521330132876L, outboxFolder);
         } else {
            FolderMerge.deregisterMergedFolder(-5581791943352753293L, inboxFolder);
            FolderMerge.deregisterMergedFolder(2993144521330132876L, inboxFolder);
            FolderMerge.deregisterMergedFolder(-5581791943352753293L, outboxFolder);
            FolderMerge.deregisterMergedFolder(2993144521330132876L, outboxFolder);
         }
      }
   }

   private static final SimpleFolder getFolderFromHierarchy(SimpleFolder hierarchy, long folderId, int folderName, int flags) {
      SimpleFolder folder = (SimpleFolder)hierarchy.getFolder(folderId);
      if (folder == null) {
         folder = (SimpleFolder)(new Object(
            3704547669295631919L,
            folderId,
            MMSResources.getResourceBundle(),
            folderName,
            "net.rim.device.apps.api.messaging.util.PersistedSortedCollection",
            hierarchy,
            flags
         ));
         hierarchy.putFolder(folder);
      }

      return folder;
   }

   static final void removeAll() {
      System.out.println("MMSStorage.removeAll");
      synchronized (FolderHierarchies.getLockObject()) {
         emptyFolder(8244211460627721111L);
         emptyFolder(949632297110531729L);
         emptyFolder(-7297051376619864492L);
      }
   }

   public static final Folder getMMSFolder(long folderId) {
      return FolderHierarchies.getFolder(-5085817815977500958L, folderId);
   }

   private static final void emptyFolder(long folder) {
      Folder messageFolder = getMMSFolder(folder);
      if (messageFolder != null) {
         IntRangedActionTarget target = (IntRangedActionTarget)messageFolder.getContainedItems();
         target.apply(Integer.MIN_VALUE, Integer.MAX_VALUE, -198247372487919817L, null);
         ((WritableSet)target).removeAll();
      }
   }

   public static final long getFolderForMessage(MMSMessageModel message) {
      if (message.isSavedThenOrphaned()) {
         return -7297051376619864492L;
      } else {
         return message.isInbound() ? 8244211460627721111L : 949632297110531729L;
      }
   }

   public static final void fileMessage(MMSMessageModel message, long folderId) {
      synchronized (FolderHierarchies.getLockObject()) {
         message.perform(6760675856762529805L, new Object(folderId));
         Folder messageFolder = getMMSFolder(folderId);
         WritableSet folder = (WritableSet)messageFolder.getContainedItems();
         folder.add(message);
      }
   }

   public static final boolean isFiled(MMSMessageModel message) {
      return message.getFolderId() != 0;
   }

   public static final void moveMessage(MMSMessageModel message, long toFolderId) {
      synchronized (FolderHierarchies.getLockObject()) {
         removeMessage(message);
         fileMessage(message, toFolderId);
      }
   }

   public static final void removeMessage(MMSMessageModel message) {
      synchronized (FolderHierarchies.getLockObject()) {
         Folder folder = getMMSFolder(message.getFolderId());
         WritableSet collection = (WritableSet)folder.getContainedItems();
         collection.remove(message);
      }
   }

   public static final long assignFolderIdForMessage(MMSMessageModel message, Object context) {
      return message.getFolderId() == 0 ? 949632297110531729L : message.getFolderId();
   }

   public static final MMSMessageModel findMessageByTransactionID(String transactionID) {
      if (transactionID != null && transactionID.length() != 0) {
         MMSMessageModel message = findMessageByTransactionID(getMMSFolder(8244211460627721111L), transactionID);
         if (message == null) {
            message = findMessageByTransactionID(getMMSFolder(949632297110531729L), transactionID);
         }

         if (message == null) {
            message = findMessageByTransactionID(getMMSFolder(-7297051376619864492L), transactionID);
         }

         return message;
      } else {
         return null;
      }
   }

   private static final MMSMessageModel findMessageByTransactionID(Folder folder, String transactionID) {
      ReadableList list = (ReadableList)folder.getContainedItems();

      for (int idx = list.size() - 1; idx >= 0; idx--) {
         MMSMessageModel message = (MMSMessageModel)list.getAt(idx);
         String xid = message.getPayload().getAttribute("x-mms-transaction-id");
         if (transactionID.equals(xid)) {
            return message;
         }
      }

      return null;
   }

   public static final MMSMessageModel findMessageByMessageID(long folderID, String messageID) {
      return messageID != null && messageID.length() != 0 ? findMessageByMessageID(getMMSFolder(folderID), messageID) : null;
   }

   private static final MMSMessageModel findMessageByMessageID(Folder folder, String messageID) {
      ReadableList list = (ReadableList)folder.getContainedItems();

      for (int idx = list.size() - 1; idx >= 0; idx--) {
         MMSMessageModel message = (MMSMessageModel)list.getAt(idx);
         if (messageID.equals(message.getPayload().getAttribute("message-id"))) {
            return message;
         }
      }

      return null;
   }

   public static final Enumeration getMessages(long folderID) {
      Folder folder = getMMSFolder(folderID);
      if (folder != null) {
         ReadableList list = (ReadableList)folder.getContainedItems();
         return new ReadableListEnumeration(list);
      } else {
         return null;
      }
   }
}
