package net.rim.device.api.streaming;

public interface SourceSession {
   int writeBuffer(byte[] var1, int var2, int var3);

   int lostData(int var1);

   int close(boolean var1, boolean var2);
}
