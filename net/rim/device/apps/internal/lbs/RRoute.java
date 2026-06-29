package net.rim.device.apps.internal.lbs;

import javax.microedition.location.Coordinates;
import net.rim.device.api.lbs.gps.GPSLocationData;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.internal.lbs.content.IntArray;
import net.rim.device.apps.internal.lbs.content.LocationDocumentConverter;
import net.rim.device.apps.internal.lbs.maplet.MapPoint;

final class RRoute {
   Coordinates _coord;
   RouteCallback _callback = new RouteCallback(this);
   private DataBuffer _db = (DataBuffer)(new Object());
   int _rootElementMarker;
   IntArray _path = new IntArray();
   Coordinates[] _coordc = new Object[0];
   boolean _includeAltitude = true;
   IntArray _altitudes;
   boolean _includeTime = true;
   int _time;
   IntArray _times;
   int _index = 0;
   int _marker;
   static Bitmap _crumb = Bitmap.getBitmapResource("crumb.png");
   static int _crumbWidth = _crumb.getWidth();
   static int _crumbHeight = _crumb.getHeight();
   static int _ofx = _crumbWidth / 2;
   static int _ofy = _crumbHeight / 2;

   RRoute(GPSLocationData gpsLocationData) {
      this.start(gpsLocationData);
   }

   final void paint(Graphics graphics, Transform transform) {
      MapPoint point = new MapPoint();
      int count = this._coordc.length;

      for (int i = 0; i < count; i++) {
         point._x = (int)this._coordc[i].getLongitude();
         point._y = (int)this._coordc[i].getLatitude();
         transform.convertWorldToScreen(point);
         graphics.drawBitmap(point._x - _ofx, point._y - _ofy, _crumbWidth, _crumbHeight, _crumb, 0, 0);
      }
   }

   final void start(GPSLocationData gpsLocationData) {
      this._coord.setLatitude(gpsLocationData.getLatitude());
      this._coord.setLongitude(gpsLocationData.getLongitude());
      this._marker = LocationDocumentConverter.startRoute(this._db, 0, 0, null, null);
      LocationDocumentConverter.writeInstruction(
         this._db, (int)this._coord.getLongitude(), (int)this._coord.getLatitude(), 0, 0, "start", 10, "address", "description", "exit", "connector", "towards"
      );
      this._path.start();
      this._path.add((int)this._coord.getLongitude());
      this._path.add((int)this._coord.getLatitude());
      if (this._includeAltitude) {
         this._coord.setAltitude(gpsLocationData.getAltitude());
         this._altitudes = new IntArray();
         this._altitudes.start();
         this._altitudes.add((int)this._coord.getAltitude());
      }

      if (this._includeTime) {
         this._time = (int)(System.currentTimeMillis() / 1000);
         this._times = new IntArray();
         this._times.start();
         this._times.add(this._time);
      }
   }

   final void addObservation(GPSLocationData gpsLocationData) {
      int lat = gpsLocationData.getLatitudeInt();
      int lon = gpsLocationData.getLongitudeInt();
      int dy = lat - (int)this._coord.getLatitude();
      int dx = lon - (int)this._coord.getLongitude();
      Coordinates tmpCoord = (Coordinates)(new Object(lat, lon, (float)false));
      if (Math.abs(dx) + Math.abs(dy) >= 10) {
         this._path.add(dx);
         this._path.add(dy);
         this._coord.setLatitude(lat);
         this._coord.setLongitude(lon);
         Arrays.add(this._coordc, tmpCoord);
         if (this._includeAltitude) {
            int altitude = (int)gpsLocationData.getAltitude();
            this._altitudes.add(altitude - (int)this._coord.getAltitude());
            this._coord.setAltitude(gpsLocationData.getAltitude());
         }

         if (this._includeTime) {
            int time = (int)(System.currentTimeMillis() / 1000);
            this._times.add(time - this._time);
            this._time = time;
         }
      }
   }

   final void save() {
      this._path.end();
      LocationDocumentConverter.writePath(this._db, this._path, 1, "", "", "", 1, (double)4607182418800017408L);
      if (this._includeTime) {
         this._times.end();
         LocationDocumentConverter.writeProfile(this._db, this._times, 1);
      }

      if (this._includeAltitude) {
         this._altitudes.end();
         LocationDocumentConverter.writeProfile(this._db, this._altitudes, 2);
      }

      LocationDocumentConverter.writeInstruction(
         this._db, (int)this._coord.getLongitude(), (int)this._coord.getLatitude(), 0, 0, "end", 10, "address", "description", "exit", "connector", "towards"
      );
      LocationDocumentConverter.endElement(this._db, this._marker);
      String name = LocationDocumentCollection.getInstance().generateUniqueLabel();
      LocationDocumentCollection.getInstance().add(this._db, name, null);
   }
}
