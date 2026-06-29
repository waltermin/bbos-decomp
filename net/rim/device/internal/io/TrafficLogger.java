package net.rim.device.internal.io;

public interface TrafficLogger {
   int TYPE_STREAM;
   int TYPE_PACKET;

   void bytesTransmitted(Object var1, int var2, String var3, int var4, byte[] var5);

   void bytesReceived(Object var1, int var2, String var3, int var4, byte[] var5);
}
