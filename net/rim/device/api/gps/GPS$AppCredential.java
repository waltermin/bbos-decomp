package net.rim.device.api.gps;

public class GPS$AppCredential {
   int _gpsAppId;
   String _appPassword;

   public GPS$AppCredential(int appId, String pass) {
      this._gpsAppId = appId;
      this._appPassword = pass;
   }

   public int getAppId() {
      return this._gpsAppId;
   }
}
