package net.rim.device.internal.synchronization;

import java.util.Enumeration;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.servicebook.ServiceIdentifier;
import net.rim.device.api.synchronization.MultiServiceSyncCollection;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionStatusProvider;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.LongHashtable;
import net.rim.vm.Memory;

public class SyncAllCollection implements SyncCollection, SyncCollectionStatusProvider {
   private LongHashtable _collections = (LongHashtable)(new Object());
   private IntHashtable _syncObjects = (IntHashtable)(new Object());
   private int _syncObjectCount = 0;
   private String _syncCollectionName = " - All";
   private static final String _syncCollectionNameSuffix = " - All";

   public synchronized void addSyncCollection(MultiServiceSyncCollection mssc, long sid) {
      this._collections.put(sid, mssc);
      SyncObject[] syncObjects = mssc.getSyncObjects();

      for (int i = 0; i < syncObjects.length; i++) {
         this._syncObjectCount++;
         SyncAllCollectionSyncObject so = new SyncAllCollectionSyncObject(syncObjects[i], sid, this._syncObjectCount);
         this._syncObjects.put(this._syncObjectCount, so);
      }
   }

   public MultiServiceSyncCollection getSyncCollectionBySid(long sid) {
      MultiServiceSyncCollection result = (MultiServiceSyncCollection)this._collections.get(sid);
      if (result == null) {
         Enumeration e = this._collections.elements();

         while (e.hasMoreElements()) {
            MultiServiceSyncCollection mssc = (MultiServiceSyncCollection)e.nextElement();
            if (ServiceIdentifier.isSameService(sid, mssc.getSid())) {
               return mssc;
            }
         }
      }

      return result;
   }

   public MultiServiceSyncCollection getDefaultCollection() {
      MultiServiceSyncCollection result = null;
      Enumeration e = this._collections.elements();

      while (e.hasMoreElements()) {
         MultiServiceSyncCollection mssc = (MultiServiceSyncCollection)e.nextElement();
         if (mssc.isDefault()) {
            return mssc;
         }
      }

      return result;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void cleanAllCollections() {
      Enumeration e = this._collections.elements();

      while (e.hasMoreElements()) {
         MultiServiceSyncCollection mssc = (MultiServiceSyncCollection)e.nextElement();
         boolean var5 = false /* VF: Semaphore variable */;

         try {
            var5 = true;
            mssc.beginTransaction();
            mssc.removeAllSyncObjects();
            Memory.persistentGC();
            var5 = false;
         } finally {
            if (var5) {
               mssc.endTransaction();
            }
         }

         mssc.endTransaction();
         ((SyncManagerImpl)SyncManager.getInstance()).collectionUpdatedSerially(mssc);
      }
   }

   public void collectionUpdatedSerially() {
      Enumeration e = this._collections.elements();

      while (e.hasMoreElements()) {
         MultiServiceSyncCollection mssc = (MultiServiceSyncCollection)e.nextElement();
         ((SyncManagerImpl)SyncManager.getInstance()).collectionUpdatedSerially(mssc);
      }
   }

   @Override
   public boolean isReadableForSerialSync() {
      return true;
   }

   @Override
   public boolean isWritableForOTASL() {
      return false;
   }

   @Override
   public int getOTASLControlMask() {
      return 0;
   }

   @Override
   public int getSyncObjectCount() {
      return this._syncObjects.size();
   }

   @Override
   public SyncObject[] getSyncObjects() {
      SyncObject[] syncObjects = new Object[this._syncObjects.size()];
      Enumeration e = this._syncObjects.elements();

      for (int count = 0; e.hasMoreElements(); count++) {
         syncObjects[count] = (SyncObject)e.nextElement();
      }

      return syncObjects;
   }

   @Override
   public SyncObject getSyncObject(int handle) {
      return (SyncObject)this._syncObjects.get(handle);
   }

   @Override
   public int getSyncVersion() {
      int result = 0;
      if (this._collections.size() > 0) {
         Enumeration e = this._collections.elements();
         if (e.hasMoreElements()) {
            result = ((SyncCollection)e.nextElement()).getSyncVersion();
         }
      }

      return result;
   }

   @Override
   public String getSyncName() {
      return this._syncCollectionName;
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
   public boolean addSyncObject(SyncObject object) {
      throw new Object("Invalid Usage: This method not supported in SyncAllCollection.");
   }

   @Override
   public boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      throw new Object("Invalid Usage: This method not supported in SyncAllCollection.");
   }

   @Override
   public boolean removeSyncObject(SyncObject object) {
      throw new Object("Invalid Usage: This method not supported in SyncAllCollection.");
   }

   @Override
   public boolean removeAllSyncObjects() {
      throw new Object("Invalid Usage: This method not supported in SyncAllCollection.");
   }

   @Override
   public String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public SyncConverter getSyncConverter() {
      throw new Object("Invalid Usage: This method not supported in SyncAllCollection.");
   }

   @Override
   public void beginTransaction() {
      Enumeration e = this._collections.elements();

      while (e.hasMoreElements()) {
         MultiServiceSyncCollection mssc = (MultiServiceSyncCollection)e.nextElement();
         mssc.beginTransaction();
      }
   }

   @Override
   public void endTransaction() {
      Enumeration e = this._collections.elements();

      while (e.hasMoreElements()) {
         MultiServiceSyncCollection mssc = (MultiServiceSyncCollection)e.nextElement();
         mssc.endTransaction();
      }
   }

   @Override
   public boolean isWritableForSerialSync() {
      Enumeration e = this._collections.elements();

      while (e.hasMoreElements()) {
         MultiServiceSyncCollection mssc = (MultiServiceSyncCollection)e.nextElement();
         if (!SerialSyncDaemon.isWritable(mssc)) {
            return false;
         }
      }

      return true;
   }

   public SyncAllCollection(String defaultCollectionName) {
      if (defaultCollectionName != null) {
         this._syncCollectionName = ((StringBuffer)(new Object())).append(defaultCollectionName).append(" - All").toString();
      }
   }
}
