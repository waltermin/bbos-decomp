package net.rim.device.api.system;

public class WLANInfo$WLANAPInfo {
   private String _profileName;
   private String _ssid;
   private String _bssid;
   private int _radioBand;
   private int _dataRate;
   private int _signalLevel;

   WLANInfo$WLANAPInfo(String profileName, String ssid, String bssid, int radioBand, int dataRate, int signalLevel) {
      this._profileName = profileName;
      this._ssid = ssid;
      this._bssid = bssid;
      this._radioBand = radioBand;
      this._dataRate = dataRate;
      this._signalLevel = signalLevel;
   }

   public String getProfileName() {
      return this._profileName;
   }

   public String getSSID() {
      return this._ssid;
   }

   public String getBSSID() {
      return this._bssid;
   }

   public int getRadioBand() {
      return this._radioBand;
   }

   public int getDataRate() {
      return this._dataRate;
   }

   public int getSignalLevel() {
      return this._signalLevel;
   }
}
