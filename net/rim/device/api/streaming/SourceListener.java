package net.rim.device.api.streaming;

public interface SourceListener {
   void streamingWriteWatermarkCrossed(SourceSession var1);

   void streamingSinkError(SourceSession var1);

   void streamingSessionClosed(SourceSession var1);
}
