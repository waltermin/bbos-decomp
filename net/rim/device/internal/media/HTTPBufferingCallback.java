package net.rim.device.internal.media;

import javax.microedition.io.InputConnection;

public interface HTTPBufferingCallback {
   void streamingBufferReady();

   void streamingDone(double var1);

   void updateStreamingBufferStatus(long var1, long var3);

   InputConnection requestResource(String var1);
}
