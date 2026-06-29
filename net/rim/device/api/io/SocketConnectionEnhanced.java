package net.rim.device.api.io;

public interface SocketConnectionEnhanced {
   short READ_TIMEOUT = 256;

   void setSocketOptionEx(short var1, long var2);

   long getSocketOptionEx(short var1);
}
