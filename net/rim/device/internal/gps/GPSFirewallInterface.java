package net.rim.device.internal.gps;

public interface GPSFirewallInterface {
   long GPS_FIREWALL_GUID;
   int PROMPT;
   int ALLOW_APP;
   int DENY_APP;
   int DENY_FOREVER;
   int ALLOW_FOREVER;

   void setCurrentPrivacy(int var1);

   boolean allowAccess(String var1);

   boolean isEnabled();

   void reset();
}
