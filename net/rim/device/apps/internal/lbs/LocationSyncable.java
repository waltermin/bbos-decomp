package net.rim.device.apps.internal.lbs;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.messaging.util.SimpleFolder;

public interface LocationSyncable extends Persistable, SyncObject, KeyProvider {
   int TYPE_LOCATION;
   int TYPE_ROUTE;
   int TYPE_HISTORY;

   void setData(String var1, byte[] var2);

   @Override
   int getUID();

   boolean load(DataBuffer var1, int var2);

   boolean save(DataBuffer var1, int var2);

   String getLabel();

   byte[] getData();

   SimpleFolder getFolderHeirarchies();

   int getType();
}
