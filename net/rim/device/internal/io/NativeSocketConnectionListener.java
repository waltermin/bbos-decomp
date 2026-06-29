package net.rim.device.internal.io;

public interface NativeSocketConnectionListener extends NativeSocketListener {
   int ERROR_NONE = 0;

   void socketConnected(int var1);

   void socketDisconnected(int var1);

   void socketError(int var1, int var2);

   void socketWriteReady(int var1);
}
