package net.rim.device.internal.io;

public interface BoundNativeSocketListener {
   void socketDataReady();

   void socketWriteReady();

   void socketDisconnected();

   int getSocketId();
}
