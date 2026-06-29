package net.rim.device.internal.synchronization.ota.util;

import net.rim.device.api.util.DataBuffer;

public interface TLESerializableObject {
   void readFrom(DataBuffer var1);

   void writeTo(DataBuffer var1);
}
