package net.rim.device.internal.deviceoptions.synchronization;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.synchronization.AlwaysSyncCollection;
import net.rim.device.internal.synchronization.NoProtectedContentInCollection;
import net.rim.vm.Array;

public class PolicySyncCollection implements SyncCollection, SyncConverter, AlwaysSyncCollection, NoProtectedContentInCollection {
   private PersistentObject _persist = RIMPersistentStore.getPersistentObject(6362811050397941699L);
   private Object[] _records = (Object[])this._persist.getContents();
   private static final long KEY = 6362811050397941699L;
   private static final String SYNC_NAME = "Policy";
   private static final int SYNC_VERSION = 0;

   PolicySyncCollection() {
      if (this._records == null) {
         this._records = new Object[0];
         this._persist.setContents(this._records, 51);
         this._persist.commit();
      }
   }

   @Override
   public boolean addSyncObject(SyncObject object) {
      int index = this._records.length;
      Array.resize(this._records, index + 1);
      this._records[index] = object;
      PersistentObject.commit(this._records);
      return true;
   }

   @Override
   public boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      int len = this._records.length;

      for (int i = 0; i < len; i++) {
         if (this._records[i] == oldObject) {
            this._records[i] = newObject;
            PersistentObject.commit(this._records);
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean removeSyncObject(SyncObject object) {
      int len = this._records.length;

      for (int i = 0; i < len; i++) {
         if (this._records[i] == object) {
            if (i < len - 1) {
               System.arraycopy(this._records, i, this._records, i + 1, len - i - 1);
            }

            Array.resize(this._records, len - 1);
            PersistentObject.commit(this._records);
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean removeAllSyncObjects() {
      Array.resize(this._records, 0);
      this._persist.commit();
      return true;
   }

   @Override
   public SyncObject[] getSyncObjects() {
      int len = this._records.length;
      SyncObject[] result = new Object[len];
      System.arraycopy(this._records, 0, result, 0, len);
      return result;
   }

   @Override
   public SyncObject getSyncObject(int uid) {
      int numRecords = this._records.length;

      for (int i = 0; i < numRecords; i++) {
         SyncObject record = (SyncObject)this._records[i];
         if (record.getUID() == uid) {
            return record;
         }
      }

      return null;
   }

   @Override
   public boolean isSyncObjectDirty(SyncObject object) {
      return false;
   }

   @Override
   public void setSyncObjectDirty(SyncObject object) {
   }

   @Override
   public void clearSyncObjectDirty(SyncObject object) {
   }

   @Override
   public int getSyncObjectCount() {
      return this._records.length;
   }

   @Override
   public int getSyncVersion() {
      return 0;
   }

   @Override
   public String getSyncName() {
      return "Policy";
   }

   @Override
   public String getSyncName(Locale locale) {
      return null;
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

   @Override
   public boolean convert(SyncObject object, DataBuffer buffer, int version) {
      buffer.write(((PolicySyncCollection$PolicySyncObject)object)._data);
      return true;
   }

   @Override
   public SyncObject convert(DataBuffer data, int version, int UID) {
      PolicySyncCollection$PolicySyncObject result = new PolicySyncCollection$PolicySyncObject();
      int len = data.available();
      result._data = new byte[len];
      data.read(result._data);
      return result;
   }
}
