package net.rim.device.apps.internal.blackberryemail.email;

import java.util.Enumeration;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.MessageFilter;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.otasync.OTAMessageSync;
import net.rim.vm.Array;

public final class EmailMessageSync extends EmailMessageSyncBase implements SyncEventListener, OTASyncPriorityProvider {
   private boolean _removeAllEmailMessagesPerformed;
   private boolean _removeAllPINMessagesPerformed;
   private static final String SYNC_COLLECTION_NAME;
   private static final long EMAIL_MESSAGES_BACKUP_MERGE_ID;
   private static LongHashtable _folderCache;

   @Override
   public final String getSyncName() {
      return "Messages";
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      if (this._removeAllEmailMessagesPerformed && !this._removeAllPINMessagesPerformed && ((EmailMessageModel)object).flagsSet(8192)) {
         PINMessageSync.removeAllPINMessages();
         this._removeAllPINMessagesPerformed = true;
      }

      return super.addSyncObject(object);
   }

   @Override
   public final boolean removeAllSyncObjects() {
      this._removeAllEmailMessagesPerformed = true;
      EmailMessageModel[] messages = MessageSync.getMessagesInAnonymousEmailHierarchy();

      for (int i = messages.length - 1; i >= 0; i--) {
         EmailMessageModel message = messages[i];
         if (!message.flagsSet(8192)) {
            this.removeSyncObject((SyncObject)message);
         }
      }

      EmailHierarchy anonymousMessageHierarchy = EmailHierarchy.getAnonymousEmailHierarchy();

      for (int i = EmailHierarchy.getEmailHierarchyCount() - 1; i >= 0; i--) {
         EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchy(i);
         if (hierarchy != anonymousMessageHierarchy) {
            hierarchy.removeMessagesFromSubtree(false);
         }
      }

      OTAMessageSync.getInstance().messageListRestored();
      return true;
   }

   @Override
   public final int getSyncObjectCount() {
      int syncObjectCount = 0;
      EmailMessageModel[] messages = MessageSync.getMessagesInAnonymousEmailHierarchy();

      for (int i = messages.length - 1; i >= 0; i--) {
         if (!messages[i].flagsSet(8192)) {
            syncObjectCount++;
         }
      }

      EmailHierarchy anonymousMessageHierarchy = EmailHierarchy.getAnonymousEmailHierarchy();

      for (int i = EmailHierarchy.getEmailHierarchyCount() - 1; i >= 0; i--) {
         EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchy(i);
         if (hierarchy != null && hierarchy != anonymousMessageHierarchy) {
            syncObjectCount += hierarchy.getMessageCount();
         }
      }

      return syncObjectCount;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      ReadableList messages = null;
      int numHierarchies = EmailHierarchy.getEmailHierarchyCount();

      for (int i = 0; i < numHierarchies; i++) {
         EmailHierarchy.getEmailHierarchy(i).addMessagesToFolderMerge(-5870816783098540986L);
         if (i == 0) {
            messages = (ReadableList)FolderMerge.getMergeCollection(-5870816783098540986L);
            MessageFilter filter = (MessageFilter)(new Object(messages, (byte)4));
            ((CollectionEventSource)messages).addCollectionListener(new Object(filter));
            messages = filter;
         }
      }

      if (messages == null) {
         return new Object[0];
      }

      int numMessages = messages.size();
      SyncObject[] syncObjects = new Object[numMessages];
      LowMemoryManager.poll();
      synchronized (FolderHierarchies.getLockObject()) {
         numMessages = messages.size();
         Array.resize(syncObjects, numMessages);
         messages.getAt(0, numMessages, syncObjects, 0);
      }

      Folder[] mergedFolders = new Object[0];
      Enumeration e = FolderMerge.getMergedFolders(-5870816783098540986L);
      if (e != null) {
         while (e.hasMoreElements()) {
            Arrays.add(mergedFolders, e.nextElement());
         }

         for (int i = mergedFolders.length - 1; i >= 0; i--) {
            FolderMerge.deregisterMergedFolder(-5870816783098540986L, mergedFolders[i]);
         }
      }

      return syncObjects;
   }

   static final EmailFolder getEmailFolder(long folderId) {
      EmailFolder emailFolder = (EmailFolder)_folderCache.get(folderId);
      if (emailFolder == null) {
         Folder f = FolderHierarchies.getFolder(folderId);
         if (!(f instanceof EmailFolder)) {
            return null;
         }

         emailFolder = (EmailFolder)f;
         _folderCache.put(folderId, emailFolder);
      }

      return emailFolder;
   }

   @Override
   public final void endTransaction() {
      super.endTransaction();
      this._removeAllEmailMessagesPerformed = false;
      this._removeAllPINMessagesPerformed = false;
   }

   @Override
   public final void syncEventOccurred(int eventId, Object object) {
      switch (eventId) {
         case 1:
         default:
            _folderCache = (LongHashtable)(new Object());
            return;
         case 2:
            _folderCache = null;
         case 0:
      }
   }

   @Override
   public final int getSyncPriority() {
      return 10;
   }
}
