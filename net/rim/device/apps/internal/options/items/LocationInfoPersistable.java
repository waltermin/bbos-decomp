package net.rim.device.apps.internal.options.items;

import net.rim.device.api.util.Persistable;
import net.rim.device.apps.internal.options.resources.OptionsResources;

public final class LocationInfoPersistable implements Persistable {
   private String _longString = "W 0° 0.00'";
   private String _latString = "N 0° 0.00'";
   private String _numSat = "0";
   private String _accuracy = "0.0 m";
   private String _lastFix = OptionsResources.getString(1984);

   public final void setValues(String lon, String lat, String num, String ac, String fix) {
      this._longString = lon;
      this._latString = lat;
      this._numSat = num;
      this._accuracy = ac;
      this._lastFix = fix;
   }

   public final String getLatitude() {
      return this._latString;
   }

   public final String getLongitude() {
      return this._longString;
   }

   public final String getSattelites() {
      return this._numSat;
   }

   public final String getAccuracy() {
      return this._accuracy;
   }

   public final String getLastFixTime() {
      return this._lastFix;
   }
}
