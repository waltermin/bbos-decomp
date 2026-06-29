package net.rim.device.api.gps;

public interface GPSListener {
   void gpsModeChangeComplete(boolean var1, int var2);

   void gpsLocationUpdated(int var1, int var2, int var3);

   void gpsResponseGetLPS(int var1);

   void gpsResponseSetLPS(int var1);

   void gpsResponseEnablePIN(int var1);

   void gpsResponseChangePIN(int var1);

   void gpsPDEChangeComplete(boolean var1, int var2, int var3);

   void gpsEphemerisDataRequired(int var1);

   void gpsCredentialChangeComplete(boolean var1, int var2);

   void gpsLocationAidingRequest();
}
