package net.rim.device.api.streaming;

public interface SinkListener {
   void streamingReadWatermarkCrossed(SinkSession var1);

   void streamingSourceLostData(SinkSession var1, int var2);

   void streamingSourceError(SinkSession var1);

   void streamingSessionClosed(SinkSession var1);
}
