package net.rim.device.api.lbs.gps;

public interface GPSProvider$Listener {
   void locationUpdated();

   void deviceStateChanged(GPSDevice var1, String var2);
}
