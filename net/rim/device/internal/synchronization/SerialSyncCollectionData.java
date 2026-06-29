package net.rim.device.internal.synchronization;

import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;

public class SerialSyncCollectionData {
   private SyncCollection _sc;
   private SyncObject[] _objects;
   private int _count;
   private int _handleBase;
   private boolean _stale;

   public SerialSyncCollectionData(SyncCollection syncCollection) {
      this._sc = syncCollection;
   }

   public SyncCollection getSyncCollection() {
      return this._sc;
   }

   public SyncObject[] getSyncObjects() {
      return this._objects;
   }

   public int getCount() {
      return this._count;
   }

   public void incrementCount() {
      this._count++;
   }

   public void decrementCount() {
      this._count--;
   }

   public int getHandleBase() {
      return this._handleBase;
   }

   public void setHandleBase(int handleBase) {
      this._handleBase = handleBase;
   }

   public boolean isStale() {
      return this._stale;
   }

   public void setStale(boolean stale) {
      this._stale = stale;
   }

   public SyncObject getSyncObject(int handle) {
      try {
         return this._objects[handle - this._handleBase];
      } finally {
         ;
      }
   }

   public void loadSyncObjectsFromCollection() {
      if (this._sc != null) {
         this._objects = this._sc.getSyncObjects();
         this._count = this._objects.length;
      }
   }

   public int getNumberOfRecords() {
      int result = 0;
      if (this._sc != null) {
         result = this._sc.getSyncObjectCount();
      }

      return result;
   }

   public boolean convertFromSyncObject(SyncObject object, DataBuffer buffer, int version) {
      boolean result = false;
      if (this._sc != null) {
         SyncConverter c = this._sc.getSyncConverter();
         if (c != null) {
            result = c.convert(object, buffer, version);
         }
      }

      return result;
   }

   public int convertToSyncObjectAndAdd(DataBuffer buffer, int version, int uniqueId, int dirty, SerialSyncDaemon$UidWrapper uidWrapper) {
      int result = 2;
      if (this._sc != null) {
         SyncConverter c = this._sc.getSyncConverter();
         if (c != null) {
            SyncObject so = c.convert(buffer, version, uniqueId);
            if (so != null && this._sc.addSyncObject(so)) {
               result = 0;
               uidWrapper.setUid(so.getUID());
               if (dirty == 0) {
                  this._sc.clearSyncObjectDirty(so);
               }
            }
         }
      }

      return result;
   }

   public boolean isWritable() {
      return this._sc == null ? true : SerialSyncDaemon.isWritable(this._sc);
   }

   public void endTransaction() {
      if (this._sc != null) {
         this._sc.endTransaction();
      }
   }

   public void beginTransaction() {
      if (this._sc != null) {
         this._sc.beginTransaction();
      }
   }

   public void collectionUpdatedSerially() {
      if (this._sc != null) {
         ((SyncManagerImpl)SyncManager.getInstance()).collectionUpdatedSerially(this._sc);
      }
   }

   public void cleanCollection() {
      if (this._sc != null) {
         this._sc.removeAllSyncObjects();
         this._objects = null;
         this._count = 0;
      }
   }

   public void dispose() {
   }
}
