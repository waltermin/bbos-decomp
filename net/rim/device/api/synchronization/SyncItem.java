package net.rim.device.api.synchronization;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.DataBuffer;

public class SyncItem implements SyncObject, SyncCollection, SyncConverter {
   public boolean setSyncData(DataBuffer _1, int _2) {
      throw null;
   }

   public boolean getSyncData(DataBuffer _1, int _2) {
      throw null;
   }

   @Override
   public boolean convert(SyncObject object, DataBuffer buffer, int version) {
      return this.getSyncData(buffer, version);
   }

   @Override
   public int getUID() {
      return 1;
   }

   @Override
   public SyncObject convert(DataBuffer data, int version, int uid) {
      return this.setSyncData(data, version) ? this : null;
   }

   @Override
   public int getSyncVersion() {
      throw null;
   }

   @Override
   public String getSyncName() {
      throw null;
   }

   @Override
   public String getSyncName(Locale _1) {
      throw null;
   }

   @Override
   public boolean addSyncObject(SyncObject object) {
      return true;
   }

   @Override
   public boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      return true;
   }

   @Override
   public boolean removeSyncObject(SyncObject object) {
      return true;
   }

   @Override
   public boolean removeAllSyncObjects() {
      return true;
   }

   @Override
   public SyncObject[] getSyncObjects() {
      return new SyncObject[]{this};
   }

   @Override
   public SyncObject getSyncObject(int uid) {
      return uid == this.getUID() ? this : null;
   }

   @Override
   public boolean isSyncObjectDirty(SyncObject object) {
      return true;
   }

   @Override
   public void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public int getSyncObjectCount() {
      return 1;
   }

   @Override
   public SyncConverter getSyncConverter() {
      return this;
   }

   @Override
   public void beginTransaction() {
   }

   @Override
   public void endTransaction() {
   }

   protected SyncItem() {
   }
}
