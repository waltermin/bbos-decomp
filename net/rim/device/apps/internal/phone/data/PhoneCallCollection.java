package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncDefaultProvider;
import net.rim.device.api.synchronization.OTASyncEventOptimizationProvider;
import net.rim.device.api.synchronization.OTASyncListener;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.RIMModelSyncConverter;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollection;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.vm.Array;
import net.rim.vm.DirtyBits;

final class PhoneCallCollection
   implements OTASyncCapable,
   OTASyncListener,
   CollectionEventSource,
   SyncCollection,
   SyncCollectionStatistics,
   SyncEventListener,
   PersistentContentListener,
   OTASyncDefaultProvider,
   OTASyncEventOptimizationProvider {
   private Object[] _deferredCallLogAdditions;
   private boolean _inSyncTransaction;
   private PersistedSortedCollection _defaultFolderData;
   private PersistedSortedCollection _missedCallFolderData;
   private PersistedSortedCollection _dcCallFolderData;
   private PersistedSortedCollection _dcAlertFolderData;
   private RIMModelSyncConverter _syncConverter = new RIMModelSyncConverter(20, -5829986326706945081L);
   private SyncCollectionSchema _schema;
   static final int SYNC_TYPE_PHONE = 0;
   static final int SYNC_TYPE_GENERAL = 1;
   private static final int MIN_CALL_LOG_COUNT_FOR_STATUS = 10;
   private static long PHONE_CALL_COLLECTION_ENABLE_SYNC = 8364368765202218487L;
   private static PhoneCallCollection _instance;
   private static final int[] KEY_FIELD_IDS = new int[]{4, -804782076, 9832200, 9831900};
   private static final int DEFAULT_RECORD_TYPE = 1;

   final synchronized void deferCallLogAddition(Object callLog) {
      if (this._deferredCallLogAdditions == null) {
         this._deferredCallLogAdditions = new Object[1];
      } else {
         Array.resize(this._deferredCallLogAdditions, this._deferredCallLogAdditions.length + 1);
      }

      this._deferredCallLogAdditions[this._deferredCallLogAdditions.length - 1] = callLog;
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      SimpleFolder[] folders = PhoneFolders.getPhoneFolders();

      for (int idx = 0; idx < folders.length; idx++) {
         PersistedSortedCollection items = (PersistedSortedCollection)folders[idx].getContainedItems();
         items.removeCollectionListener(listener);
      }
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName() {
      return "Phone Call Logs";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      return this._syncConverter;
   }

   @Override
   public final boolean addSyncObject(SyncObject object) {
      PhoneFolders.restoreItem(object);
      return true;
   }

   @Override
   public final boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      PhoneFolders.replaceItem(oldObject, newObject);
      return true;
   }

   @Override
   public final boolean removeSyncObject(SyncObject object) {
      PhoneFolders.removeItem(object);
      return true;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      this._defaultFolderData.removeAll();
      this._missedCallFolderData.removeAll();
      if (this._dcCallFolderData != null) {
         this._dcCallFolderData.removeAll();
      }

      if (this._dcAlertFolderData != null) {
         this._dcAlertFolderData.removeAll();
      }

      VoiceUnopenedCount.getInstance().databaseDeleted();
      CallLogCollection.getInstance().callLogsDatabaseDeleted();
      return true;
   }

   @Override
   public final SyncObject[] getSyncObjects() {
      SyncObject[] objects = new SyncObject[0];
      synchronized (this) {
         int missedCallCount = this._missedCallFolderData.size();
         int defaultCallCount = this._defaultFolderData.size();
         int dcCallCount = this._dcCallFolderData != null ? this._dcCallFolderData.size() : 0;
         int dcAlertCount = this._dcAlertFolderData != null ? this._dcAlertFolderData.size() : 0;
         int count = missedCallCount + defaultCallCount + dcCallCount + dcAlertCount;
         int dest = 0;
         Array.resize(objects, count);

         for (int i = 0; i < missedCallCount; i++) {
            Object o = this._missedCallFolderData.getAt(i);
            if (o instanceof SyncObject) {
               objects[dest++] = (SyncObject)o;
            }
         }

         for (int var13 = 0; var13 < defaultCallCount; var13++) {
            Object o = this._defaultFolderData.getAt(var13);
            if (o instanceof SyncObject) {
               objects[dest++] = (SyncObject)o;
            }
         }

         for (int var14 = 0; var14 < dcCallCount; var14++) {
            Object o = this._dcCallFolderData.getAt(var14);
            if (o instanceof SyncObject) {
               objects[dest++] = (SyncObject)o;
            }
         }

         for (int var15 = 0; var15 < dcAlertCount; var15++) {
            Object o = this._dcAlertFolderData.getAt(var15);
            if (o instanceof SyncObject) {
               objects[dest++] = (SyncObject)o;
            }
         }

         Array.resize(objects, dest);
         return objects;
      }
   }

   @Override
   public final SyncObject getSyncObject(int uid) {
      synchronized (this) {
         SyncObject obj = this.getSyncObject(this._defaultFolderData, uid);
         if (obj == null) {
            obj = this.getSyncObject(this._missedCallFolderData, uid);
         }

         if (obj == null && this._dcCallFolderData != null) {
            obj = this.getSyncObject(this._dcCallFolderData, uid);
         }

         if (obj == null && this._dcAlertFolderData != null) {
            obj = this.getSyncObject(this._dcAlertFolderData, uid);
         }

         return obj;
      }
   }

   @Override
   public final boolean isSyncObjectDirty(SyncObject object) {
      return DirtyBits.isDirty(object);
   }

   @Override
   public final void setSyncObjectDirty(SyncObject object) {
      DirtyBits.setDirty(object);
   }

   @Override
   public final void clearSyncObjectDirty(SyncObject object) {
      DirtyBits.setClean(object);
   }

   @Override
   public final synchronized int getSyncObjectCount() {
      int missedCallCount = this._missedCallFolderData.size();
      int defaultCallCount = this._defaultFolderData.size();
      int dcCallCount = this._dcCallFolderData != null ? this._dcCallFolderData.size() : 0;
      int dcAlertCount = this._dcAlertFolderData != null ? this._dcAlertFolderData.size() : 0;
      return missedCallCount + defaultCallCount + dcCallCount + dcAlertCount;
   }

   @Override
   public final void syncEventOccurred(int eventId, Object object) {
      CallLogCollection.getInstance().syncEventOccurred(eventId);
   }

   @Override
   public final void beginTransaction() {
      this.syncTransactionStarted();
   }

   @Override
   public final void endTransaction() {
      this.syncTransactionStopped();
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
   public final synchronized int getSyncCollectionSize() {
      return SyncCollectionStatisticsManager.getSyncCollectionSize(this);
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return this._schema;
   }

   @Override
   public final void addCollectionListener(Object listener) {
      SimpleFolder[] folders = PhoneFolders.getPhoneFolders();

      for (int idx = 0; idx < folders.length; idx++) {
         PersistedSortedCollection items = (PersistedSortedCollection)folders[idx].getContainedItems();
         items.addCollectionListener(listener);
      }
   }

   @Override
   public final void persistentContentStateChanged(int state) {
      if (state == 1) {
         Hotlist.getInstance().removeDuplicates(true);
         this.updateCallLogCollection();
      }
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      PhoneFolders.crypt(generation);
      Hotlist.crypt(generation);
   }

   @Override
   public final boolean isDisabledByDefault() {
      return false;
   }

   @Override
   public final boolean getEventOptimizationDisabled() {
      return true;
   }

   private PhoneCallCollection() {
      AddressBook ab = AddressBookServices.getAddressBook(true);
      if (ab != null) {
         synchronized (ab) {
            this._missedCallFolderData = PhoneFolders.getContainedItems(7042951934619290849L);
            this._defaultFolderData = PhoneFolders.getContainedItems(5390902206192375236L);
            this._dcCallFolderData = PhoneFolders.getContainedItems(-1859209320265783789L);
            this._dcAlertFolderData = PhoneFolders.getContainedItems(-2025972805868361049L);
         }
      }

      this._schema = new SyncCollectionSchema();
      this._schema.setDefaultRecordType(1);
      this._schema.setKeyFieldIds(1, KEY_FIELD_IDS);
   }

   private final void startCallLogUpdate(Runnable runnable, boolean newThread) {
      if (newThread) {
         Runnable updateRunnable = runnable;
         Dialog status = new PhoneCallCollection$1OrganizingCallsDialog(
            this, PhoneResources.getString(6288), Bitmap.getPredefinedBitmap(3), 33554432, updateRunnable
         );
         status.setEscapeEnabled(false);
         synchronized (Application.getEventLock()) {
            status.show(10);
         }
      } else {
         runnable.run();
      }
   }

   private final synchronized void updateCallLogCollection() {
      int count = 0;
      CallLogCollection clc = CallLogCollection.getInstance();
      if (clc.initializationBlockedByContentProtection()) {
         Runnable runnable = new PhoneCallCollection$1(this, clc);
         count = PhoneFolders.getCallLogCount();
         count += this._deferredCallLogAdditions != null ? this._deferredCallLogAdditions.length : 0;
         this.startCallLogUpdate(runnable, count > 10);
      } else {
         if (this._deferredCallLogAdditions != null && this._deferredCallLogAdditions.length > 0) {
            Object[] deferredCallLogs = this._deferredCallLogAdditions;
            this._deferredCallLogAdditions = null;
            Runnable runnable = new PhoneCallCollection$2(this, deferredCallLogs);
            this.startCallLogUpdate(runnable, deferredCallLogs.length > 10);
         }
      }
   }

   static final PhoneCallCollection getInstance() {
      if (_instance == null) {
         boolean needToCreate = false;
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         synchronized (FolderHierarchies.getLockObject()) {
            _instance = (PhoneCallCollection)applicationRegistry.getOrWaitFor(PHONE_CALL_COLLECTION_ENABLE_SYNC);
            if (_instance == null) {
               needToCreate = true;
            }
         }

         if (needToCreate) {
            _instance = new PhoneCallCollection();
            applicationRegistry.put(PHONE_CALL_COLLECTION_ENABLE_SYNC, _instance);
            SyncManager syncManager = SyncManager.getInstance();
            if (syncManager != null) {
               syncManager.enableSynchronization(_instance, true, 9);
               syncManager.addSyncEventListener(_instance);
            }

            PersistentContent.addListener(_instance);
         }
      }

      return _instance;
   }

   private final SyncObject getSyncObject(PersistedSortedCollection coll, int uid) {
      for (int i = coll.size() - 1; i >= 0; i--) {
         Object elem = coll.getAt(i);
         if (elem instanceof SyncObject) {
            SyncObject so = (SyncObject)elem;
            if (so.getUID() == uid) {
               return so;
            }
         }
      }

      return null;
   }

   private final void syncTransactionStopped() {
      if (this._inSyncTransaction) {
         this._inSyncTransaction = false;
         this._missedCallFolderData.commitFolder();
         this._defaultFolderData.commitFolder();
         if (this._dcCallFolderData != null) {
            this._dcCallFolderData.commitFolder();
         }

         if (this._dcAlertFolderData != null) {
            this._dcAlertFolderData.commitFolder();
         }

         DirtyBits.commit();
      }

      CallLogCollection.getInstance().syncTransactionStopped(0);
   }

   private final void syncTransactionStarted() {
      this._inSyncTransaction = true;
      CallLogCollection.getInstance().syncTransactionStarted(0);
   }
}
