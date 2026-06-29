package net.rim.device.apps.api.sync;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;

public class OTASyncDataManager {
   private IntHashtable _data;

   protected OTASyncDataManager(long luid) {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(luid);
      synchronized (persistentObject) {
         this._data = (IntHashtable)persistentObject.getContents();
         if (this._data == null) {
            this._data = new IntHashtable(50);
            persistentObject.setContents(this._data, 51);
            persistentObject.commit();
         }
      }
   }

   public void add(OTASyncIDProvider syncIDProvider, OTASyncData entry) {
      synchronized (this._data) {
         this._data.put(syncIDProvider.getSyncID(), entry);
         this.commit();
      }
   }

   public void addWithoutCommit(OTASyncIDProvider syncIDProvider, OTASyncData entry) {
      synchronized (this._data) {
         this._data.put(syncIDProvider.getSyncID(), entry);
      }
   }

   public void add(int syncID, OTASyncData entry) {
      synchronized (this._data) {
         this._data.put(syncID, entry);
         this.commit();
      }
   }

   public void removeWithoutCommit(OTASyncIDProvider syncIDProvider) {
      synchronized (this._data) {
         this._data.remove(syncIDProvider.getSyncID());
      }
   }

   public void remove(OTASyncIDProvider syncIDProvider) {
      synchronized (this._data) {
         if (this._data.remove(syncIDProvider.getSyncID()) != null) {
            this.commit();
         }
      }
   }

   public void remove(int syncID) {
      synchronized (this._data) {
         if (this._data.remove(syncID) != null) {
            this.commit();
         }
      }
   }

   public void removeWithoutCommit(int syncID) {
      synchronized (this._data) {
         this._data.remove(syncID);
      }
   }

   public OTASyncData get(OTASyncIDProvider syncIDProvider) {
      return (OTASyncData)this._data.get(syncIDProvider.getSyncID());
   }

   public OTASyncData get(int syncID) {
      return (OTASyncData)this._data.get(syncID);
   }

   public void clear() {
      this._data.clear();
      this.commit();
   }

   public IntEnumeration getSyncIDs() {
      return this._data.keys();
   }

   public void commit() {
      PersistentObject.commit(this._data);
   }
}
