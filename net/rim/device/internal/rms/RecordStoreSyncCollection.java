package net.rim.device.internal.rms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionStatistics;
import net.rim.device.api.synchronization.SyncCollectionStatisticsManager;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;

public class RecordStoreSyncCollection implements SyncCollection, SyncConverter, SyncCollectionStatistics {
   private static final long RECORD_STORE_ID = 5639922433554527164L;
   private static PersistentObject _persistentObject = RIMPersistentStore.getPersistentObject(5639922433554527164L);
   private static Hashtable _allRecordStores;
   private static Hashtable _activeStores;
   private static RecordStoreSyncCollection _recordStoreSyncCollection = new RecordStoreSyncCollection();

   private RecordStoreSyncCollection() {
   }

   public static RecordStoreSyncCollection getInstance() {
      return _recordStoreSyncCollection;
   }

   private static int getUID(String midletSuiteName) {
      return midletSuiteName.hashCode();
   }

   @Override
   public boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (!(object instanceof RecordStoreSyncCollection$RMSSyncObject)) {
         return false;
      }

      RecordStoreSyncCollection$RMSSyncObject rmso = (RecordStoreSyncCollection$RMSSyncObject)object;
      DataBuffer dataBuffer = new DataBuffer(buffer.isBigEndian());
      String midletSuiteName = rmso.getMidletSuiteName();
      Hashtable rmsdata = rmso.getData();
      int recordStoreCount = rmsdata.size();

      try {
         dataBuffer.writeUTF(midletSuiteName);
      } catch (IOException e) {
         return false;
      }

      dataBuffer.writeInt(recordStoreCount);
      Enumeration keys = rmsdata.keys();

      while (keys.hasMoreElements()) {
         String recordStoreName = (String)keys.nextElement();
         RecordStoreData recordStoreData = (RecordStoreData)rmsdata.get(recordStoreName);
         int recordCount = recordStoreData.getNumRecords();
         int[] recordIds = new int[recordCount];
         recordStoreData.loadRecordIDs(recordIds);

         try {
            dataBuffer.writeUTF(recordStoreName);
         } catch (IOException e) {
            return false;
         }

         dataBuffer.writeInt(recordStoreData.getVersion());
         dataBuffer.writeInt(recordStoreData.getNextRecordID());
         dataBuffer.writeInt(recordStoreData.getSize());
         dataBuffer.writeLong(recordStoreData.getLastModified());
         dataBuffer.writeInt(recordCount);
         dataBuffer.writeInt(recordStoreData.getAuthMode());

         for (int i = 0; i < recordCount; i++) {
            int recordId = recordIds[i];

            byte[] value;
            try {
               value = recordStoreData.getRecordReadOnly(recordId);
            } catch (InvalidRecordIDException e) {
               continue;
            }

            dataBuffer.writeInt(recordId);
            dataBuffer.writeByteArray(value);
         }
      }

      byte[] data = dataBuffer.toArray();
      int written = 0;
      int offset = 0;
      int toWrite = data.length;

      while (true) {
         written = Math.min(toWrite, 65535);
         buffer.writeShort(written);
         toWrite -= written;
         if (toWrite <= 0) {
            buffer.writeByte(0);
            buffer.write(data, offset, written);
            return true;
         }

         buffer.writeByte(1);
         buffer.write(data, offset, written);
         offset += written;
      }
   }

   @Override
   public SyncObject convert(DataBuffer data, int version, int UID) {
      try {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();

         boolean more;
         do {
            int sectionLength = data.readUnsignedShort();
            more = data.readByte() == 1;
            byte[] section = new byte[sectionLength];
            int read = data.read(section);
            if (read < sectionLength) {
               return null;
            }

            buffer.write(section);
         } while (more);

         DataBuffer dataBuffer = new DataBuffer(buffer.toByteArray(), 0, buffer.size(), data.isBigEndian());
         String midletSuiteName = dataBuffer.readUTF();
         int recordStoreCount = dataBuffer.readInt();
         Hashtable hashtable = new Hashtable();

         for (int i = 0; i < recordStoreCount; i++) {
            IntHashtable values = new IntHashtable();
            String recordStoreName = dataBuffer.readUTF();
            int rs_version = dataBuffer.readInt();
            int availableId = dataBuffer.readInt();
            int size = dataBuffer.readInt();
            long lastModified = dataBuffer.readLong();
            int recordCount = dataBuffer.readInt();
            boolean containsMIDP_2_0_Data = false;
            int rs_authmode = 0;
            if ((rs_version & 16777216) != 0) {
               containsMIDP_2_0_Data = true;
               rs_authmode = dataBuffer.readInt();
            }

            for (int j = 0; j < recordCount; j++) {
               int recordId = dataBuffer.readInt();
               byte[] value = dataBuffer.readByteArray();
               values.put(recordId, value);
            }

            RecordStoreData recordStoreData = new RecordStoreData(recordStoreName, rs_version, availableId, size, lastModified, values, recordCount);
            if (containsMIDP_2_0_Data) {
               recordStoreData.setAuthMode(rs_authmode);
            }

            hashtable.put(recordStoreName, recordStoreData);
         }

         return new RecordStoreSyncCollection$RMSSyncObject(midletSuiteName, hashtable);
      } catch (IOException e) {
         return null;
      }
   }

   @Override
   public boolean addSyncObject(SyncObject object) {
      if (!(object instanceof RecordStoreSyncCollection$RMSSyncObject)) {
         return false;
      }

      RecordStoreSyncCollection$RMSSyncObject rmso = (RecordStoreSyncCollection$RMSSyncObject)object;
      Hashtable data = rmso.getData();
      String midletSuiteName = rmso.getMidletSuiteName();
      if (!RecordStoreUtil.isOnDevice(midletSuiteName)) {
         return true;
      }

      synchronized (_allRecordStores) {
         _allRecordStores.put(midletSuiteName, data);
      }

      Enumeration e = data.elements();
      synchronized (_activeStores) {
         while (e.hasMoreElements()) {
            RecordStoreData recordStoreData = (RecordStoreData)e.nextElement();
            RecordStoreManagerProxy rsmp = (RecordStoreManagerProxy)ApplicationRegistry.getApplicationRegistry().getOrWaitFor(6635119920104263588L);
            if (rsmp != null) {
               _activeStores.put(recordStoreData, rsmp.createRecordStore(recordStoreData));
            }
         }

         _persistentObject.commit();
         return true;
      }
   }

   @Override
   public boolean updateSyncObject(SyncObject oldObject, SyncObject newObject) {
      if (newObject instanceof RecordStoreSyncCollection$RMSSyncObject) {
         RecordStoreSyncCollection$RMSSyncObject nrmso = (RecordStoreSyncCollection$RMSSyncObject)newObject;
         if (oldObject instanceof RecordStoreSyncCollection$RMSSyncObject) {
            RecordStoreSyncCollection$RMSSyncObject ormso = (RecordStoreSyncCollection$RMSSyncObject)oldObject;
            String midletSuiteName = nrmso.getMidletSuiteName();
            if (midletSuiteName != null && midletSuiteName.equals(ormso.getMidletSuiteName())) {
               Hashtable data = nrmso.getData();
               synchronized (_allRecordStores) {
                  if (_allRecordStores.get(midletSuiteName) == null) {
                     return false;
                  }

                  _allRecordStores.put(midletSuiteName, data);
               }

               Enumeration e = ormso.getData().elements();
               synchronized (_activeStores) {
                  while (e.hasMoreElements()) {
                     RecordStoreData recordStoreData = (RecordStoreData)e.nextElement();
                     _activeStores.remove(recordStoreData);
                  }

                  e = data.elements();

                  while (e.hasMoreElements()) {
                     RecordStoreData recordStoreData = (RecordStoreData)e.nextElement();
                     RecordStoreManagerProxy rsmp = (RecordStoreManagerProxy)ApplicationRegistry.getApplicationRegistry().getOrWaitFor(6635119920104263588L);
                     if (rsmp != null) {
                        _activeStores.put(recordStoreData, rsmp.createRecordStore(recordStoreData));
                     }
                  }

                  _persistentObject.commit();
                  return true;
               }
            }

            return false;
         }
      }

      return false;
   }

   @Override
   public boolean removeSyncObject(SyncObject object) {
      if (object instanceof RecordStoreSyncCollection$RMSSyncObject) {
         RecordStoreSyncCollection$RMSSyncObject rmso = (RecordStoreSyncCollection$RMSSyncObject)object;
         String midletSuiteName = rmso.getMidletSuiteName();
         if (midletSuiteName != null) {
            synchronized (_allRecordStores) {
               _allRecordStores.remove(midletSuiteName);
            }

            Enumeration e = rmso.getData().elements();
            synchronized (_activeStores) {
               while (e.hasMoreElements()) {
                  _activeStores.remove(e.nextElement());
               }

               _persistentObject.commit();
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public boolean removeAllSyncObjects() {
      synchronized (_allRecordStores) {
         _allRecordStores.clear();
      }

      synchronized (_activeStores) {
         _activeStores.clear();
         _persistentObject.commit();
         return true;
      }
   }

   @Override
   public SyncObject[] getSyncObjects() {
      synchronized (_allRecordStores) {
         int size = _allRecordStores.size();
         SyncObject[] so = new SyncObject[size];
         Enumeration keys = _allRecordStores.keys();

         for (int i = 0; keys.hasMoreElements(); i++) {
            String midletSuiteName = (String)keys.nextElement();
            Hashtable data = (Hashtable)_allRecordStores.get(midletSuiteName);
            so[i] = new RecordStoreSyncCollection$RMSSyncObject(midletSuiteName, data);
         }

         return so;
      }
   }

   @Override
   public SyncObject getSyncObject(int uid) {
      return null;
   }

   @Override
   public boolean isSyncObjectDirty(SyncObject object) {
      return !(object instanceof RecordStoreSyncCollection$RMSSyncObject) ? false : ((RecordStoreSyncCollection$RMSSyncObject)object).isDirty();
   }

   @Override
   public void setSyncObjectDirty(SyncObject object) {
      if (object instanceof RecordStoreSyncCollection$RMSSyncObject) {
         ((RecordStoreSyncCollection$RMSSyncObject)object).setDirty(true);
      }
   }

   @Override
   public void clearSyncObjectDirty(SyncObject object) {
      if (object instanceof RecordStoreSyncCollection$RMSSyncObject) {
         ((RecordStoreSyncCollection$RMSSyncObject)object).setDirty(false);
      }
   }

   @Override
   public int getSyncObjectCount() {
      return _allRecordStores.size();
   }

   @Override
   public int getSyncVersion() {
      return 1;
   }

   @Override
   public String getSyncName() {
      return "RMS Databases";
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
   public synchronized int getSyncCollectionSize() {
      return SyncCollectionStatisticsManager.getSyncCollectionSize(this);
   }

   static {
      synchronized (_persistentObject) {
         if (_persistentObject.getContents() == null) {
            _persistentObject.setContents(new Hashtable(), 51);
            _persistentObject.commit();
         }
      }

      _allRecordStores = (Hashtable)_persistentObject.getContents();
      _activeStores = ApplicationRegistry.getApplicationRegistry().getHashtable(5639922433554527164L);

      try {
         RecordStore.openRecordStore("Fake_RecordStore_Name", false);
      } catch (RecordStoreException var2) {
      }
   }
}
