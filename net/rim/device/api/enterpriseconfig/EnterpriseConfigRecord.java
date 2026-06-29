package net.rim.device.api.enterpriseconfig;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;

public interface EnterpriseConfigRecord extends Persistable, SyncObject {
   DataBuffer getData();

   byte getTableId();
}
