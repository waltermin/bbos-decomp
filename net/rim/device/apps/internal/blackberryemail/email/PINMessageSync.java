package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncDefaultProvider;
import net.rim.device.api.synchronization.OTASyncEventOptimizationProvider;
import net.rim.device.api.synchronization.OTASyncListener;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.MessageFilter;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.vm.Array;

final class PINMessageSync
   extends MessageSync
   implements OTASyncCapable,
   CollectionListener,
   CollectionEventSource,
   OTASyncListener,
   OTASyncPriorityProvider,
   OTASyncDefaultProvider,
   OTASyncEventOptimizationProvider {
   private CollectionListenerManager _listeners = (CollectionListenerManager)(new Object());
   private static final String SYNC_COLLECTION_NAME;
   private static final long PIN_MESSAGES_BACKUP_MERGE_ID;

   PINMessageSync() {
      EmailHierarchy anonymousEmailHierarchy = EmailHierarchy.getAnonymousEmailHierarchy();
      this.listenForChangesToFolder(anonymousEmailHierarchy.getUnfiledFolder());
      this.listenForChangesToFolder(anonymousEmailHierarchy.getFiledFolder());
      this.listenForChangesToFolder(anonymousEmailHierarchy.getOrphanedSavedFolder());
   }

   private final void listenForChangesToFolder(EmailFolder folder) {
      CollectionEventSource containedItems = (CollectionEventSource)folder.getContainedItems();
      containedItems.addCollectionListener(this);
   }

   static final void removeAllPINMessages() {
      EmailMessageModel[] messages = MessageSync.getMessagesInAnonymousEmailHierarchy();

      for (int i = messages.length - 1; i >= 0; i--) {
         EmailMessageModel message = messages[i];
         if (message.flagsSet(8192)) {
            MessageSync.removeMessage(message);
         }
      }
   }

   @Override
   public final String getSyncName() {
      return "PIN Messages";
   }

   @Override
   public final boolean removeAllSyncObjects() {
      removeAllPINMessages();
      return true;
   }

   @Override
   public final int getSyncObjectCount() {
      int syncObjectCount = 0;
      EmailMessageModel[] messages = MessageSync.getMessagesInAnonymousEmailHierarchy();

      for (int i = messages.length - 1; i >= 0; i--) {
         if (messages[i].flagsSet(8192)) {
            syncObjectCount++;
         }
      }

      return syncObjectCount;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      this.removeSyncObject(oldObject);
      this.addSyncObject(newObject);
      this.elementUpdated(this, oldObject, newObject);
      return true;
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      SyncObject syncObject = null;
      EmailHierarchy hierarchy = EmailHierarchy.getAnonymousEmailHierarchy();
      synchronized (FolderHierarchies.getLockObject()) {
         syncObject = this.getSyncObject(uid, hierarchy.getUnfiledFolder());
         if (syncObject == null) {
            syncObject = this.getSyncObject(uid, hierarchy.getOrphanedSavedFolder());
            if (syncObject == null) {
               syncObject = this.getSyncObject(uid, hierarchy.getFiledFolder());
            }
         }

         return syncObject;
      }
   }

   private final SyncObject getSyncObject(int uid, EmailFolder emailFolder) {
      ReadableList messages = (ReadableList)emailFolder.getContainedItems();

      for (int i = messages.size() - 1; i >= 0; i--) {
         EmailMessageModel message = (EmailMessageModel)messages.getAt(i);
         if (message.getUID() == uid && message.flagsSet(8192)) {
            return (SyncObject)message;
         }
      }

      return null;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      EmailHierarchy anonymousEmailHierarchy = EmailHierarchy.getAnonymousEmailHierarchy();
      FolderMerge.registerMergedFolder(-8297684192922704087L, anonymousEmailHierarchy.getUnfiledFolder());
      FolderMerge.registerMergedFolder(-8297684192922704087L, anonymousEmailHierarchy.getFiledFolder());
      FolderMerge.registerMergedFolder(-8297684192922704087L, anonymousEmailHierarchy.getOrphanedSavedFolder());
      ReadableList messages = (ReadableList)FolderMerge.getMergeCollection(-8297684192922704087L);
      MessageFilter filter = (MessageFilter)(new Object(messages, (byte)2));
      ((CollectionEventSource)messages).addCollectionListener(new Object(filter));
      messages = filter;
      int numMessages = messages.size();
      SyncObject[] syncObjects = new Object[numMessages];
      LowMemoryManager.poll();
      synchronized (FolderHierarchies.getLockObject()) {
         numMessages = messages.size();
         Array.resize(syncObjects, numMessages);
         messages.getAt(0, numMessages, syncObjects, 0);
      }

      FolderMerge.deregisterMergedFolder(-8297684192922704087L, anonymousEmailHierarchy.getUnfiledFolder());
      FolderMerge.deregisterMergedFolder(-8297684192922704087L, anonymousEmailHierarchy.getFiledFolder());
      FolderMerge.deregisterMergedFolder(-8297684192922704087L, anonymousEmailHierarchy.getOrphanedSavedFolder());
      return syncObjects;
   }

   @Override
   final void commitChanges() {
      EmailHierarchy.getAnonymousEmailHierarchy().commitSubtree(false);
   }

   @Override
   final void cleanupAllFolderPreselectorsAfterSync() {
      FolderPreselector.getInstance(EmailHierarchy.getAnonymousEmailHierarchy()).cleanupRecommendedFolderQuickly();
   }

   private final boolean isPINMessage(Object element) {
      return !(element instanceof EmailMessageModel) ? false : ((EmailMessageModel)element).flagsSet(8192);
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return null;
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._listeners.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._listeners.removeCollectionListener(listener);
   }

   @Override
   public final void reset(Collection collection) {
      this._listeners.fireReset(this);
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      if (this.isPINMessage(element)) {
         this._listeners.fireElementAdded(this, element);
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (this.isPINMessage(oldElement)) {
         this._listeners.fireElementUpdated(this, oldElement, newElement);
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (this.isPINMessage(element)) {
         this._listeners.fireElementRemoved(this, element);
      }
   }

   @Override
   public final void otaSyncOperationStarted(SyncCollection syncCollection, int type) {
      if (type == 1) {
         this.syncTransactionStarted();
      }
   }

   @Override
   public final void otaSyncOperationStopped(SyncCollection syncCollection, int type) {
      if (type == 1) {
         this.syncTransactionStopped();
      }
   }

   @Override
   public final int getSyncPriority() {
      return 10;
   }

   @Override
   public final boolean isDisabledByDefault() {
      return true;
   }

   @Override
   public final boolean getEventOptimizationDisabled() {
      return true;
   }
}
