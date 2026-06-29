package javax.microedition.rms;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.internal.rms.RecordStoreData;
import net.rim.device.internal.rms.RecordStoreUtil;

class RecordStoreManager$RecordStoreManagerListener implements GlobalEventListener {
   private Hashtable _toDelete = new Hashtable();
   private static final long TIMEOUT;

   public void deleteRecordStoresWithKey(String midletSuiteHashKey, Hashtable recordStores) {
      Hashtable midletSuiteStores = (Hashtable)recordStores.get(midletSuiteHashKey);
      if (midletSuiteStores != null) {
         recordStores.remove(midletSuiteHashKey);
         Enumeration midletSuiteStoreKeys = midletSuiteStores.keys();
         synchronized (RecordStoreManager._activeStores) {
            while (midletSuiteStoreKeys.hasMoreElements()) {
               String currentRecordStoreName = (String)midletSuiteStoreKeys.nextElement();
               RecordStoreData recordStoreData = (RecordStoreData)midletSuiteStores.get(currentRecordStoreName);
               RecordStoreManager._activeStores.remove(recordStoreData);
            }
         }
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -4232371946002803201L) {
         this.deleteUnusedRecords(false);
      } else {
         if (guid == 256826950193107649L) {
            this.cleanUpRecords();
         }
      }
   }

   private void cleanUpRecords() {
      synchronized (RecordStoreManager._persistentObject) {
         Hashtable allRecordStores = (Hashtable)RecordStoreManager._persistentObject.getContents();
         Enumeration toDeleteKeys = this._toDelete.keys();

         while (toDeleteKeys.hasMoreElements()) {
            String midletSuiteHashKey = (String)toDeleteKeys.nextElement();
            Long timeMarked = (Long)this._toDelete.get(midletSuiteHashKey);
            long diff = System.currentTimeMillis() - timeMarked;
            if (diff > 10000) {
               this.deleteRecordStoresWithKey(midletSuiteHashKey, allRecordStores);
               this._toDelete.remove(midletSuiteHashKey);
            } else if (RecordStoreUtil.isOnDevice(midletSuiteHashKey)) {
               this._toDelete.remove(midletSuiteHashKey);
            }
         }

         RecordStoreManager._persistentObject.commit();
      }
   }

   private void deleteUnusedRecords(boolean deleteNow) {
      synchronized (RecordStoreManager._persistentObject) {
         Hashtable allRecordStores = (Hashtable)RecordStoreManager._persistentObject.getContents();
         Enumeration allRecordStoreKeys = allRecordStores.keys();

         while (allRecordStoreKeys.hasMoreElements()) {
            String midletSuiteHashKey = (String)allRecordStoreKeys.nextElement();
            if (!RecordStoreUtil.isOnDevice(midletSuiteHashKey)) {
               if (deleteNow) {
                  this.deleteRecordStoresWithKey(midletSuiteHashKey, allRecordStores);
               } else {
                  this._toDelete.put(midletSuiteHashKey, new Long(System.currentTimeMillis()));
               }
            }
         }

         RecordStoreManager._persistentObject.commit();
      }
   }

   public RecordStoreManager$RecordStoreManagerListener() {
      this.deleteUnusedRecords(true);
   }
}
