package net.rim.device.api.synchronization;

import net.rim.device.api.util.DataBuffer;

public interface SyncConverter {
   boolean convert(SyncObject var1, DataBuffer var2, int var3);

   SyncObject convert(DataBuffer var1, int var2, int var3);
}
