package net.rim.device.internal.rms;

import javax.microedition.rms.RecordStore;

public interface RecordStoreManagerProxy {
   long ID_OF_IMPL = 6635119920104263588L;

   void deleteRecordStores(String var1, String var2);

   boolean recordStoresExistForSuite(String var1, String var2);

   RecordStore createRecordStore(RecordStoreData var1);

   void commit(RecordStoreData var1);
}
