package net.rim.device.api.streaming;

public interface SinkSession {
   int readBuffer(byte[] var1, int var2, int var3);

   int close(boolean var1, boolean var2);
}
