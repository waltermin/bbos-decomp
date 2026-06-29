package net.rim.device.api.io;

public interface ConnectionCloseProvider {
   boolean connectionStatusAvailable();

   boolean isConnectionEstablished();

   void setConnectionCloseListener(ConnectionCloseListener var1);
}
