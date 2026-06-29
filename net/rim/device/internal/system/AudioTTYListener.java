package net.rim.device.internal.system;

import net.rim.device.api.system.AudioListener;

public interface AudioTTYListener extends AudioListener {
   void responseTTYModeChange(boolean var1, int var2);

   void responseHACModeChange(boolean var1, boolean var2);

   void ttyStatusUpdate(boolean var1);

   void ttyDataAvailable();

   void ttyDataReady();

   void ttyReadBufferFull();
}
