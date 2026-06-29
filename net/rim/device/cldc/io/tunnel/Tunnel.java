package net.rim.device.cldc.io.tunnel;

public interface Tunnel {
   TunnelConfig getConfig();

   int getStatus();

   int getIdentifier();

   void close();

   void kick();

   void reset();

   Object setup(int var1, Object var2);
}
