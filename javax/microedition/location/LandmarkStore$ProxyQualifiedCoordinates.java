package javax.microedition.location;

import net.rim.device.api.util.Persistable;

class LandmarkStore$ProxyQualifiedCoordinates implements Persistable {
   double _latitude;
   double _longitude;
   float _altitude;
   float _horizontalAccuracy;
   float _verticalAccuracy;

   LandmarkStore$ProxyQualifiedCoordinates(QualifiedCoordinates qcoord) {
      if (qcoord != null) {
         this._latitude = qcoord.getLatitude();
         this._longitude = qcoord.getLongitude();
         this._altitude = qcoord.getAltitude();
         this._horizontalAccuracy = qcoord.getHorizontalAccuracy();
         this._verticalAccuracy = qcoord.getVerticalAccuracy();
      }
   }

   QualifiedCoordinates getQualifiedCoordinates() {
      return new QualifiedCoordinates(this._latitude, this._longitude, this._altitude, this._horizontalAccuracy, this._verticalAccuracy);
   }
}
