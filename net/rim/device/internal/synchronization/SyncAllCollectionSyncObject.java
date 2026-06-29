package net.rim.device.internal.synchronization;

import net.rim.device.api.synchronization.SyncObject;

public class SyncAllCollectionSyncObject implements SyncObject {
   private int _uid = 0;
   private long _sid = -1;
   private SyncObject _syncObject = null;

   public SyncAllCollectionSyncObject(SyncObject so, long sid, int uid) {
      this._syncObject = so;
      this._sid = sid;
      this._uid = uid;
   }

   public long getSid() {
      return this._sid;
   }

   public SyncObject getContainedSyncObject() {
      return this._syncObject;
   }

   @Override
   public int getUID() {
      return this._uid;
   }
}
