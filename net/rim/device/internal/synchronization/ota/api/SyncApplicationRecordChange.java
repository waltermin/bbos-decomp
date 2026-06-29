package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.api.util.Persistable;

public class SyncApplicationRecordChange extends SyncApplicationChange implements Persistable {
   private int _recordUid;
   private byte _tableId;

   public SyncApplicationRecordChange() {
   }

   public SyncApplicationRecordChange(SyncApplicationRecordChange aSyncApplicationRecordChange) {
      super(aSyncApplicationRecordChange);
      this._recordUid = aSyncApplicationRecordChange._recordUid;
      this._tableId = aSyncApplicationRecordChange._tableId;
   }

   public void setRecordUid(int aRecordUid) {
      this._recordUid = aRecordUid;
   }

   public int getRecordUid() {
      return this._recordUid;
   }

   public void setTableId(int aTableId) {
      this._tableId = (byte)aTableId;
   }

   public int getTableId() {
      return this._tableId;
   }

   @Override
   public int getGroup() {
      return this._tableId;
   }

   public void setRecordFields(byte[] recordFields) {
      this.setParameters(recordFields);
   }

   public byte[] getRecordFields(boolean copy) {
      return this.getParameters(copy);
   }

   @Override
   public int hashCode() {
      return this.getRecordUid();
   }
}
