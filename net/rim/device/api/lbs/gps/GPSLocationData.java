package net.rim.device.api.lbs.gps;

public final class GPSLocationData {
   int _latitude;
   int _longitude;
   int _satelliteCount;
   boolean _sourceLocked;
   float _speed;
   float _bearing;
   float _altitude;
   boolean _isValid;
   float _vAccuracy;
   float _hAccuracy;
   public static final double PRECISION_DIVISOR = 100000.0;

   public GPSLocationData() {
      this.reset();
   }

   public final float getVerticalAccuracy() {
      return this._vAccuracy;
   }

   public final float getHorizontalAccuracy() {
      return this._hAccuracy;
   }

   public final double getLatitude() {
      return this._latitude / 4681608360884174848L;
   }

   public final double getLongitude() {
      return this._longitude / 4681608360884174848L;
   }

   public final int getLatitudeInt() {
      return this._latitude;
   }

   public final int getLongitudeInt() {
      return this._longitude;
   }

   public final int getSatelliteCount() {
      return this._satelliteCount;
   }

   public final boolean isLocked() {
      return this._sourceLocked;
   }

   public final float getSpeed() {
      return this._speed;
   }

   public final float getBearing() {
      return this._bearing;
   }

   public final float getAltitude() {
      return this._altitude;
   }

   public final boolean isValid() {
      return this._isValid;
   }

   public final void setValid(boolean valid) {
      this._isValid = valid;
      this._sourceLocked = valid;
   }

   public final void setSatelliteCount(int count) {
      this._satelliteCount = count;
   }

   final void copyInto(GPSLocationData target) {
      target._latitude = this._latitude;
      target._longitude = this._longitude;
      target._satelliteCount = this._satelliteCount;
      target._sourceLocked = this._sourceLocked;
      target._speed = this._speed;
      target._bearing = this._bearing;
      target._altitude = this._altitude;
      target._isValid = this._isValid;
      target._vAccuracy = this._vAccuracy;
      target._hAccuracy = this._hAccuracy;
   }

   public final void reset() {
      this._latitude = 0;
      this._longitude = 0;
      this._satelliteCount = 0;
      this._sourceLocked = false;
      this._speed = (float)false;
      this._bearing = (float)false;
      this._altitude = (float)false;
      this._isValid = false;
      this._vAccuracy = (float)2143289344;
      this._hAccuracy = (float)2143289344;
   }

   @Override
   public final String toString() {
      return "GPSLocationData {latitude: "
         + this.getLatitude()
         + ", longitude: "
         + this.getLongitude()
         + ", speed: "
         + this._speed
         + ", bearing: "
         + this._bearing
         + ", satellites: "
         + this._satelliteCount
         + " is valid: "
         + this._isValid
         + "}";
   }
}
