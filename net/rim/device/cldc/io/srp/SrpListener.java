package net.rim.device.cldc.io.srp;

public interface SrpListener {
   int CONNECTION_TYPE_ROUTER = 0;
   int CONNECTION_TYPE_RELAY = 1;
   int LINK_TYPE_WIFI = 0;
   int LINK_TYPE_RF = 1;

   int getLinkType();

   int getConnectionType();

   void srpServiceStateChanged(String var1, int var2, boolean var3);

   void srpRouteStateChanged(int var1, int var2, boolean var3);
}
