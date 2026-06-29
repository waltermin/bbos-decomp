package net.rim.device.cldc.io.tunnel;

public interface TunnelManager {
   TunnelConfig getConfig();

   int getStatus();

   int getIdentifier();

   boolean isClosed();

   void close(Tunnel var1);

   void kick();

   void reset();

   Object setup(int var1, Object var2);
}
