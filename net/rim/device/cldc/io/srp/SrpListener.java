package net.rim.device.cldc.io.srp;

public interface SrpListener {
   int CONNECTION_TYPE_ROUTER;
   int CONNECTION_TYPE_RELAY;
   int LINK_TYPE_WIFI;
   int LINK_TYPE_RF;

   int getLinkType();

   int getConnectionType();

   void srpServiceStateChanged(String var1, int var2, boolean var3);

   void srpRouteStateChanged(int var1, int var2, boolean var3);
}
