package net.rim.device.apps.internal.sms;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncDefaultProvider;
import net.rim.device.api.synchronization.OTASyncEventOptimizationProvider;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.vm.Array;

final class SMSSync
   implements SyncCollection,
   SyncCollectionStatistics,
   OTASyncCapable,
   CollectionListener,
   CollectionEventSource,
   OTASyncDefaultProvider,
   OTASyncEventOptimizationProvider {
   private SyncConverter _converter = new SMSSyncConverter();
   private CollectionListenerManager _listeners = (CollectionListenerManager)(new Object());
   private ContextObject _syncContextObject = (ContextObject)(new Object(19));
   private static String SYNC_COLLECTION_NAME = "SMS Messages";
   private static final long SMS_MESSAGES_BACKUP_MERGE_ID;

   SMSSync() {
      this.listenForChangesToFolder(1393133342214151287L);
      this.listenForChangesToFolder(-8580923390364260649L);
      this.listenForChangesToFolder(-4468584479793228955L);
   }

   private final void listenForChangesToFolder(long folderId) {
      Folder folder = Storage.getSMSFolder(folderId);
      CollectionEventSource containedItems = (CollectionEventSource)folder.getContainedItems();
      containedItems.addCollectionListener(this);
   }

   @Override
   public final String getSyncName() {
      return SYNC_COLLECTION_NAME;
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 5;
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return false;
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this._converter;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      Storage.removeAll();
      return true;
   }

   @Override
   public final int getSyncObjectCount() {
      ReadableList syncObjects = (ReadableList)Storage.getSMSFolder(1393133342214151287L).getContainedItems();
      int numSyncObjects = syncObjects.size();
      syncObjects = (ReadableList)Storage.getSMSFolder(-8580923390364260649L).getContainedItems();
      numSyncObjects += syncObjects.size();
      syncObjects = (ReadableList)Storage.getSMSFolder(-4468584479793228955L).getContainedItems();
      return numSyncObjects + syncObjects.size();
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      SyncObject syncObject = null;
      synchronized (FolderHierarchies.getLockObject()) {
         syncObject = this.getSyncObject(uid, Storage.getSMSFolder(1393133342214151287L));
         if (syncObject == null) {
            syncObject = this.getSyncObject(uid, Storage.getSMSFolder(-8580923390364260649L));
            if (syncObject == null) {
               syncObject = this.getSyncObject(uid, Storage.getSMSFolder(-4468584479793228955L));
            }
         }

         return syncObject;
      }
   }

   private final SyncObject getSyncObject(int uid, Folder folder) {
      ReadableList syncObjects = (ReadableList)folder.getContainedItems();

      for (int i = syncObjects.size() - 1; i >= 0; i--) {
         SyncObject syncObject = (SyncObject)syncObjects.getAt(i);
         if (syncObject.getUID() == uid) {
            return syncObject;
         }
      }

      return null;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      Folder inbox = Storage.getSMSFolder(1393133342214151287L);
      Folder outbox = Storage.getSMSFolder(-8580923390364260649L);
      Folder orphaned = Storage.getSMSFolder(-4468584479793228955L);
      FolderMerge.registerMergedFolder(642795440399165430L, inbox);
      FolderMerge.registerMergedFolder(642795440399165430L, outbox);
      FolderMerge.registerMergedFolder(642795440399165430L, orphaned);
      ReadableList messages = (ReadableList)FolderMerge.getMergeCollection(642795440399165430L);
      int numMessages = messages.size();
      SyncObject[] syncObjects = new Object[numMessages];
      LowMemoryManager.poll();
      synchronized (FolderHierarchies.getLockObject()) {
         numMessages = messages.size();
         Array.resize(syncObjects, numMessages);
         messages.getAt(0, numMessages, syncObjects, 0);
      }

      FolderMerge.deregisterMergedFolder(642795440399165430L, inbox);
      FolderMerge.deregisterMergedFolder(642795440399165430L, outbox);
      FolderMerge.deregisterMergedFolder(642795440399165430L, orphaned);
      return syncObjects;
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      SMSModel message = (SMSModel)object;
      Storage.fileMessage(message, Storage.getFolderForMessage(message));
      return true;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      ((ActionProvider)object).perform(6780594967363292755L, this._syncContextObject);
      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      this.removeSyncObject(oldObject);
      this.addSyncObject(newObject);
      this.elementUpdated(this, oldObject, newObject);
      return true;
   }

   @Override
   public final void beginTransaction() {
   }

   @Override
   public final void endTransaction() {
   }

   @Override
   public final synchronized int getSyncCollectionSize() {
      return SyncCollectionStatisticsManager.getSyncCollectionSize(this);
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
      this._listeners.fireElementAdded(this, element);
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this._listeners.fireElementUpdated(this, oldElement, newElement);
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this._listeners.fireElementRemoved(this, element);
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
