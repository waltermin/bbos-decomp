package net.rim.device.internal.deviceoptions;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;

public interface OptionsProvider extends SyncObject {
   void getOptionsData(DataBuffer var1);

   void setOptionsData(DataBuffer var1);
}
