package net.rim.device.internal.io;

public interface TrafficLogger {
   int TYPE_STREAM = 0;
   int TYPE_PACKET = 1;

   void bytesTransmitted(Object var1, int var2, String var3, int var4, byte[] var5);

   void bytesReceived(Object var1, int var2, String var3, int var4, byte[] var5);
}
