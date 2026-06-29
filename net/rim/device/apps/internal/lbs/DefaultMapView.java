package net.rim.device.apps.internal.lbs;

final class DefaultMapView {
   private String _regionName;
   private int _latitude;
   private int _longitude;
   private int _minMCC;
   private int _maxMCC;
   private int _zoomLevel;

   public DefaultMapView() {
      this._regionName = "";
      this._latitude = 4527776;
      this._longitude = -9677355;
      this._minMCC = 0;
      this._maxMCC = 0;
      this._zoomLevel = 15;
   }

   public DefaultMapView(String name, int lat, int lon, int minMcc, int maxMcc, int zoomLev) {
      this._regionName = name;
      this._latitude = lat;
      this._longitude = lon;
      this._minMCC = minMcc;
      this._maxMCC = maxMcc;
      this._zoomLevel = zoomLev;
   }

   public final boolean isValid(int mcc) {
      return mcc >= this._minMCC && mcc <= this._maxMCC;
   }

   public final int getLatitude() {
      return this._latitude;
   }

   public final int getLongitude() {
      return this._longitude;
   }

   public final int getZoomLevel() {
      return this._zoomLevel;
   }

   public final DefaultMapView copy(DefaultMapView original) {
      this._latitude = original._latitude;
      this._longitude = original._longitude;
      this._zoomLevel = original._zoomLevel;
      this._regionName = original._regionName;
      this._minMCC = original._minMCC;
      this._maxMCC = original._maxMCC;
      return this;
   }
}
