package net.rim.device.apps.internal.blackberryemail.email;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.MergedCollection;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollection;
import net.rim.device.apps.internal.api.quincy.QuincyManager;
import net.rim.device.apps.internal.blackberryemail.EmailSyncState;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.vm.Array;

public class MessageSync extends EmailSyncState implements SyncCollection, SyncCollectionStatistics {
   EmailMessageConverter _converter = new EmailMessageConverter();
   boolean _inTransaction;
   boolean _inSerialTransaction;
   private Hashtable _batchedAddMessages = new Hashtable();
   private int _batchedMessageSize = 0;
   public static final long DELETE_WITHOUT_GHOST = -8494690080715024104L;
   public static final long DELETE_WITHOUT_GHOST_AND_ORPHAN = 2817016600554138331L;
   private static final int SYNC_VERSION = 3;
   static ContextObject _syncContextObject = new ContextObject(19);
   private static int EMAIL_BATCH_ADD_LIMIT = 100;

   void cleanupAllFolderPreselectorsAfterSync() {
      throw null;
   }

   void commitChanges() {
      throw null;
   }

   void syncTransactionStopped() {
      if (this._inTransaction) {
         this._inTransaction = false;
         this.addBatchedMessages();
         if (this._inSerialTransaction) {
            this.commitChanges();
         }

         MergedCollection.resetAllMergedCollectionsThatNeedIt(null);
         MessageLookups.commit(-4420850319371185992L);
         this.cleanupAllFolderPreselectorsAfterSync();
      }

      this._converter.endTransaction();
   }

   void syncTransactionStarted() {
      this._inTransaction = true;
      MergedCollection.suspendResetProcessing();
      ShowMessageApp.postEvent(2625733777879188248L, 0, 0, new ContextObject(19), null);
   }

   @Override
   public void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public SyncConverter getSyncConverter() {
      return this._converter;
   }

   @Override
   public boolean addSyncObject(SyncObject object) {
      EmailMessageModelImpl message = (EmailMessageModelImpl)object;
      WritableSet folderContents = (WritableSet)EmailHierarchy.getStorageCollection(message.getFolderId(), message.flagsSet(2));
      if (folderContents instanceof PersistedSortedCollection) {
         if (this._inSerialTransaction) {
            this.batchMessage(message, folderContents);
            if (this._batchedMessageSize >= EMAIL_BATCH_ADD_LIMIT) {
               this.addBatchedMessages();
            }
         } else {
            ((PersistedSortedCollection)folderContents).add(message);
         }
      } else {
         if (folderContents == null) {
            try {
               throw new Throwable();
            } finally {
               QuincyManager.sendJavaLogworthy("MessageSync:NoStorageForMessage");
               return true;
            }
         }

         folderContents.add(message);
      }

      MessageLookups.putWithoutCommit(-4420850319371185992L, message.getCMIMEReferenceIdentifier(), message);
      Object ticket = PersistentContent.getTicket();
      if (message.flagsSet(2) && ticket != null) {
         long folderId = message.getFolderId();
         EmailFolder emailFolder = (EmailHeaderModel)EmailHierarchy.getEmailHierarchyForFolder(folderId).getFolder(folderId);
         FolderPreselector.updateDefaultFolder(message, emailFolder, true);
      }

      return true;
   }

   @Override
   public boolean removeSyncObject(SyncObject object) {
      removeMessage(object);
      return true;
   }

   @Override
   public void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public boolean isSyncObjectDirty(SyncObject object) {
      return false;
   }

   @Override
   public void beginTransaction() {
      this._inSerialTransaction = true;
      this.syncTransactionStarted();
   }

   @Override
   public void endTransaction() {
      this.syncTransactionStopped();
      this._inSerialTransaction = false;
   }

   @Override
   public int getSyncVersion() {
      return 3;
   }

   @Override
   public String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public synchronized int getSyncCollectionSize() {
      return SyncCollectionStatisticsManager.getSyncCollectionSize(this);
   }

   @Override
   public String getSyncName() {
      throw null;
   }

   @Override
   public int getSyncObjectCount() {
      throw null;
   }

   @Override
   public SyncObject getSyncObject(int _1) {
      throw null;
   }

   @Override
   public SyncObject[] getSyncObjects() {
      throw null;
   }

   @Override
   public boolean removeAllSyncObjects() {
      throw null;
   }

   @Override
   public boolean updateSyncObject(SyncObject _1, SyncObject _2) {
      throw null;
   }

   static void removeMessagePermanently(Object message) {
      ((ActionProvider)message).perform(2817016600554138331L, _syncContextObject);
   }

   private void addBatchedMessages() {
      if (this._batchedMessageSize > 0) {
         Enumeration storageCollections = this._batchedAddMessages.keys();
         synchronized (PersistentStore.getSynchObject()) {
            while (storageCollections.hasMoreElements()) {
               Object storageCollection = storageCollections.nextElement();
               Vector messagesVector = (Vector)this._batchedAddMessages.get(storageCollection);
               Object[] messages = new Object[messagesVector.size()];
               messagesVector.copyInto(messages);
               ((PersistedSortedCollection)storageCollection).addBatchedMessagesWithoutCommittingFolder(messages);
            }
         }

         this._batchedAddMessages.clear();
         this._batchedMessageSize = 0;
      }
   }

   private void batchMessage(Object message, Object key) {
      Vector messages = (Vector)this._batchedAddMessages.get(key);
      if (messages == null) {
         messages = new Vector();
      }

      messages.addElement(message);
      this._batchedAddMessages.put(key, messages);
      this._batchedMessageSize++;
   }

   static void removeMessage(Object message) {
      ((ActionProvider)message).perform(-8494690080715024104L, _syncContextObject);
   }

   private static void getMessagesFromEmailFolder(EmailFolder emailFolder, EmailMessageModel[] messages) {
      int numExistingMessages = messages.length;
      ReadableList emailFolderMessages = (ReadableList)emailFolder.getContainedItems();
      int numNewMessages = emailFolderMessages.size();
      Array.resize(messages, numExistingMessages + numNewMessages);
      emailFolderMessages.getAt(0, numNewMessages, messages, numExistingMessages);
   }

   static EmailMessageModel[] getMessagesInAnonymousEmailHierarchy() {
      EmailHierarchy anonymousMessageHierarchy = EmailHierarchy.getAnonymousEmailHierarchy();
      EmailMessageModel[] messages = new EmailMessageModel[0];
      getMessagesFromEmailFolder(anonymousMessageHierarchy.getUnfiledFolder(), messages);
      getMessagesFromEmailFolder(anonymousMessageHierarchy.getFiledFolder(), messages);
      getMessagesFromEmailFolder(anonymousMessageHierarchy.getOrphanedSavedFolder(), messages);
      return messages;
   }
}
