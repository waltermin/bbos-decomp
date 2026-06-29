package net.rim.device.apps.internal.lbs;

import net.rim.device.api.lbs.gps.GPSLocationData;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.lbs.finder.FindLocationScreen;
import net.rim.device.apps.internal.lbs.locator.Directions;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

public final class RoutePlanner {
   MapField _mapField;
   Location[] _locations = new Location[0];
   int _count = 0;

   public RoutePlanner(MapField mapField) {
      this._mapField = mapField;
   }

   public final void add(Location location) {
      Arrays.add(this._locations, location);
      this._count++;
   }

   public final void getRoute() {
      Directions directions = null;
      if (this._count > 0) {
         if (this._count != 1) {
            if (this._count > 1) {
               directions = new Directions(
                  this._locations[0]._address,
                  this._locations[0]._latitude,
                  this._locations[0]._longitude,
                  this._locations[1]._address,
                  this._locations[1]._latitude,
                  this._locations[1]._longitude
               );
            }
         } else {
            Location locationStart = null;
            GPSLocationData gpsLocationData = new GPSLocationData();
            if (gpsLocationData != null && gpsLocationData.isValid()) {
               locationStart = new Location(gpsLocationData.getLatitudeInt(), gpsLocationData.getLongitudeInt(), this._mapField.getZoom());
            } else {
               locationStart = FindLocationScreen.getGPSLocation();
            }

            directions = new Directions(
               LBSResources.getString(416),
               locationStart._latitude,
               locationStart._longitude,
               this._locations[0]._address,
               this._locations[0]._latitude,
               this._locations[0]._longitude
            );
         }

         String routeXmlString = directions.getDirections();
         if (routeXmlString != null) {
            this._mapField.openDocument("XML", routeXmlString, 0, true, "DIRECTIONS_SERVER");
         }
      }
   }
}
