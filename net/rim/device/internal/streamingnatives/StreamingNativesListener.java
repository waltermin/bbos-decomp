package net.rim.device.internal.streamingnatives;

public interface StreamingNativesListener {
   void streamErrorFromSink(int var1, int var2);

   void streamErrorFromSource(int var1, int var2);

   void streamHitWatermark(int var1, int var2);

   void streamLostData(int var1, int var2);

   void streamNewData(int var1, int var2);

   void streamSessionClosed(int var1);

   void streamSinkDone(int var1);

   void streamSourceDone(int var1);
}
