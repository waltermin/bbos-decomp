package net.rim.device.internal.gps;

public interface GPSFirewallInterface {
   long GPS_FIREWALL_GUID = -3752949794647167067L;
   int PROMPT = 0;
   int ALLOW_APP = 1;
   int DENY_APP = 2;
   int DENY_FOREVER = 3;
   int ALLOW_FOREVER = 4;

   void setCurrentPrivacy(int var1);

   boolean allowAccess(String var1);

   boolean isEnabled();

   void reset();
}
