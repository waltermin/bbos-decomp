package net.rim.device.api.enterpriseconfig;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.synchronization.SyncCollection;

public interface EnterpriseConfig extends SyncCollection, CollectionEventSource {
   long ENTERPRISE_CONFIG_ID = 5083252457608156518L;

   EnterpriseConfigRecord createEnterpriseConfigRecord(int var1, byte[] var2);

   EnterpriseConfigRecord[] getRecordsByTableId(byte var1);
}
