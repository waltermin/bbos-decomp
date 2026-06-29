package net.rim.device.internal.system;

import net.rim.device.api.system.AudioListener;

public interface AudioInternalListener extends AudioListener {
   void recordStreamDone(int var1, int var2);

   void recordStreamFail(int var1);

   void responseAVCModeChange(boolean var1, int var2);

   void micStatusChange(boolean var1);

   void dtmfDataAvailable();

   void dtmfDataBufferFull();
}
