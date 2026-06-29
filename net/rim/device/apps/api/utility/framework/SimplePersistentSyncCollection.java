package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionWithVersion;
import net.rim.device.api.collection.ReadableLongMap;
import net.rim.device.api.collection.util.BigSortedReadableList;
import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.collection.util.CollectionListenerAction;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncListener;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.vm.Array;
import net.rim.vm.DirtyBits;
import net.rim.vm.Memory;

public class SimplePersistentSyncCollection
   extends BigSortedReadableList
   implements CollectionWithVersion,
   SyncCollection,
   Runnable,
   ReadableLongMap,
   OTASyncListener,
   SyncCollectionStatistics {
   protected PersistentObject _persistentObject;
   protected boolean _inSyncTransaction;
   protected boolean _syncRemoveAllDone;
   protected int _maximumUpdatesAllowed;
   protected int _version;
   protected BigVector _updateCache;
   protected boolean _suppressEvent = false;

   protected void clearPersistentData() {
      throw null;
   }

   protected void commonCtorEpilogue() {
      this.verifySorted();
   }

   protected synchronized void initList(BigVector list, int version) {
      super._elements = list;
      this._version = version;
   }

   protected void syncTransactionStopped() {
      if (this._inSyncTransaction) {
         this._inSyncTransaction = false;
         this.commit();
         DirtyBits.commit();
         if (this._syncRemoveAllDone || this._updateCache != null) {
            this._syncRemoveAllDone = false;
            this.run();
         }
      }
   }

   public synchronized void verifySorted() {
      int n = this.size();
      if (n >= 2) {
         Comparator comparator = this.getComparator();
         Object higher = this.getAt(n - 1);

         for (int i = n - 2; i >= 0; i--) {
            Object lower = this.getAt(i);
            if (comparator.compare(lower, higher) > 0) {
               this.sort();
               this.commit();
               return;
            }

            higher = lower;
         }
      }
   }

   protected void commit() {
      if (!this._inSyncTransaction) {
         this._persistentObject.commit();
      }
   }

   protected void forceCommit() {
      this._persistentObject.forceCommit();
   }

   public int generateUniqueID() {
      int uid;
      do {
         uid = UIDGenerator.getUID();
      } while (this.get(uid) != null);

      return uid;
   }

   public void add(Object o) {
      synchronized (this) {
         if (this._syncRemoveAllDone) {
            super._elements.addElement(o);
         } else {
            this.doAdd(o);
         }

         DirtyBits.setDirty(o);
         this._version++;
         this.commit();
         if (!this._syncRemoveAllDone) {
            this.fireElementAdded(this, o);
         }
      }
   }

   public void update(Object oldObject, Object newObject) {
      synchronized (this) {
         boolean fire = this.doUpdate(oldObject, newObject);
         if (fire) {
            DirtyBits.setDirty(newObject);
            this._version++;
            this.commit();
         }

         if (!this._syncRemoveAllDone && fire) {
            this.fireElementUpdated(this, oldObject, newObject);
         }
      }
   }

   public void remove(Object o) {
      synchronized (this) {
         boolean fire = this.doRemove(o);
         if (fire) {
            this._version++;
            this.commit();
         }

         if (!this._syncRemoveAllDone && fire) {
            this.fireElementRemoved(this, o);
         }
      }
   }

   public void removeAll() {
      synchronized (this) {
         this.clearPersistentData();
         this._version++;
         this.forceCommit();
         this.fireReset(this);
      }
   }

   public boolean contains(Object object) {
      return this.getIndex(object) != -1;
   }

   public void endSyncCleanup() {
      synchronized (this) {
         this.verifySorted();
      }
   }

   protected void syncTransactionStarted(boolean limitUpdateCacheSize) {
      this._inSyncTransaction = true;
      if (!this._syncRemoveAllDone) {
         this._maximumUpdatesAllowed = limitUpdateCacheSize ? this.size() : -1;
         this._updateCache = (BigVector)(new Object());
      }
   }

   public void endBBBuiltInTransaction() {
      this._suppressEvent = false;
   }

   public void beginBBBuiltInTransaction() {
      this._suppressEvent = true;
   }

   @Override
   public boolean removeAllSyncObjects() {
      this._syncRemoveAllDone = true;
      this._updateCache = null;
      this.removeAll();
      return true;
   }

   @Override
   public SyncObject[] getSyncObjects() {
      SyncObject[] objects = new Object[0];
      synchronized (this) {
         int count = this.size();
         int dest = 0;
         Array.resize(objects, count);

         for (int i = 0; i < count; i++) {
            Object o = this.getAt(i);
            if (o instanceof Object) {
               SyncObject syncObj = (SyncObject)o;
               objects[dest++] = syncObj;
            }
         }

         Array.resize(objects, dest);
         return objects;
      }
   }

   @Override
   public SyncObject getSyncObject(int uid) {
      return (SyncObject)this.get(uid);
   }

   @Override
   public boolean isSyncObjectDirty(SyncObject object) {
      return DirtyBits.isDirty(object);
   }

   @Override
   public void setSyncObjectDirty(SyncObject object) {
      DirtyBits.setDirty(object);
   }

   @Override
   public void clearSyncObjectDirty(SyncObject object) {
      DirtyBits.setClean(object);
   }

   @Override
   public synchronized int getSyncObjectCount() {
      return this.size();
   }

   @Override
   public void beginTransaction() {
      this.syncTransactionStarted(true);
   }

   @Override
   public boolean removeSyncObject(SyncObject object) {
      this.remove(object);
      return true;
   }

   @Override
   public void endTransaction() {
      this.syncTransactionStopped();
   }

   @Override
   public boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      this.update(oldObject, newObject);
      return true;
   }

   @Override
   public synchronized int getSyncCollectionSize() {
      return SyncCollectionStatisticsManager.getSyncCollectionSize(this);
   }

   @Override
   public Object get(long key) {
      synchronized (this) {
         int count = this.size();

         for (int i = 0; i < count; i++) {
            Object o = this.getAt(i);
            if (o instanceof Object) {
               int uid = ((SyncObject)o).getUID();
               if (key == uid) {
                  return o;
               }
            }
         }

         return null;
      }
   }

   @Override
   public long getKey(Object o) {
      return ((SyncObject)o).getUID();
   }

   @Override
   public boolean contains(long key) {
      return this.get(key) != null;
   }

   @Override
   public void run() {
      boolean needEndSync = false;
      if (this._updateCache != null) {
         synchronized (this) {
            synchronized (this._updateCache) {
               int size = this._updateCache.size() / 2;
               Object[] fromArray = new Object[size];
               Object[] toArray = new Object[size];
               Memory.moveToFlash(this._updateCache);
               this.orderUpdates(this._updateCache, fromArray, toArray);
               this._updateCache = null;
               Memory.moveToFlash(fromArray);
               Memory.moveToFlash(toArray);
               CollectionListenerAction action = new ApplyUpdatesAction(fromArray, toArray);

               try {
                  super._listenerManager.forEachListener(this, action);
               } finally {
                  ;
               }
            }
         }
      } else {
         needEndSync = true;
      }

      if (needEndSync) {
         this.endSyncCleanup();
      }
   }

   @Override
   public boolean addSyncObject(SyncObject object) {
      this.add(object);
      return true;
   }

   @Override
   public int getVersion() {
      return this._version;
   }

   @Override
   public void otaSyncOperationStarted(SyncCollection syncCollection, int type) {
      if (type == 1) {
         this.syncTransactionStarted(false);
      }
   }

   @Override
   public void otaSyncOperationStopped(SyncCollection syncCollection, int type) {
      if (type == 1) {
         this.syncTransactionStopped();
      }
   }

   @Override
   public SyncConverter getSyncConverter() {
      throw null;
   }

   @Override
   public String getSyncName(Locale _1) {
      throw null;
   }

   @Override
   public String getSyncName() {
      throw null;
   }

   @Override
   public int getSyncVersion() {
      throw null;
   }

   protected SimplePersistentSyncCollection(Comparator cmp, long persistentId) {
      super(cmp);
      this._persistentObject = RIMPersistentStore.getPersistentObject(persistentId);
   }

   private void orderUpdates(BigVector updates, Object[] fromArray, Object[] toArray) {
      int count = updates.size();
      int i = 0;

      for (int index = 0; i < count; index++) {
         fromArray[index] = updates.elementAt(i);
         toArray[index] = updates.elementAt(i + 1);
         i += 2;
      }

      Comparator comparator = this.getComparator();
      Arrays.sort(toArray, 0, count / 2, fromArray, new SimplePersistentSyncCollection$1(this, comparator));
   }

   private boolean checkUpdateThreshold() {
      if (this._maximumUpdatesAllowed > 0 && this._updateCache.size() > this._maximumUpdatesAllowed) {
         this._updateCache = null;
         this._syncRemoveAllDone = true;
         this.fireReset(null);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void fireElementAdded(Collection collection, Object element) {
      if (!this._suppressEvent) {
         if (this._updateCache != null) {
            synchronized (this._updateCache) {
               if (!this.checkUpdateThreshold()) {
                  this._updateCache.addElement(null);
                  this._updateCache.addElement(element);
               }

               return;
            }
         }

         super.fireElementAdded(collection, element);
      }
   }

   @Override
   protected void fireElementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (this._updateCache != null) {
         synchronized (this._updateCache) {
            if (!this.checkUpdateThreshold()) {
               this._updateCache.addElement(oldElement);
               this._updateCache.addElement(newElement);
            }
         }
      } else {
         super.fireElementUpdated(collection, oldElement, newElement);
      }
   }

   @Override
   protected void fireElementRemoved(Collection collection, Object element) {
      if (!this._suppressEvent) {
         if (this._updateCache != null) {
            synchronized (this._updateCache) {
               if (!this.checkUpdateThreshold()) {
                  this._updateCache.addElement(element);
                  this._updateCache.addElement(null);
               }

               return;
            }
         }

         super.fireElementRemoved(collection, element);
      }
   }

   @Override
   protected synchronized void sort() {
      this._version++;
      super.sort();
   }
}
