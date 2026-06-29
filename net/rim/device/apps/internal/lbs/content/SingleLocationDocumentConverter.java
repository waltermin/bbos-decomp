package net.rim.device.apps.internal.lbs.content;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.internal.lbs.Decision;
import net.rim.device.apps.internal.lbs.Location;
import net.rim.device.apps.internal.lbs.LocationSyncable;
import net.rim.device.apps.internal.lbs.Route;

public final class SingleLocationDocumentConverter extends LocationDocumentConverter {
   private Location _location;
   private DataBuffer _db = new DataBuffer();
   private Location[] _cachedLocations = new Location[0];
   private String _label;
   private static SingleLocationDocumentConverter INSTANCE;

   public static final SingleLocationDocumentConverter getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new SingleLocationDocumentConverter();
      }

      return INSTANCE;
   }

   private SingleLocationDocumentConverter() {
   }

   public final Location getLocation(LocationSyncable location) {
      synchronized (this._cachedLocations) {
         for (int i = 0; i < this._cachedLocations.length; i++) {
            if (this._cachedLocations[i]._uid == location.getUID()) {
               return this._cachedLocations[i];
            }
         }
      }

      byte[] var6 = location.getData();
      this._location = null;
      this._label = location.getLabel();
      this._db.setData(var6, 0, var6.length);
      this.parseTLE(this._db);
      if (this._location != null && !Arrays.contains(this._cachedLocations, this._location)) {
         this._location._uid = location.getUID();
         Arrays.add(this._cachedLocations, this._location);
      }

      return this._location;
   }

   public final void removeLocation(LocationSyncable location) {
      synchronized (this._cachedLocations) {
         for (int i = 0; i < this._cachedLocations.length; i++) {
            if (this._cachedLocations[i]._uid == location.getUID()) {
               Arrays.remove(this._cachedLocations, this._cachedLocations[i]);
            }
         }
      }
   }

   @Override
   public final void onStartGetRoute() {
   }

   @Override
   public final void onEndGetRoute() {
   }

   @Override
   public final void onStartRoute(int distance, int time, String routeName, String folderHeirarchy) {
      if (this._label != null && !this._label.equals("")) {
         routeName = this._label;
      }

      this._location = new Route(null, distance, time, routeName);
      this._location._folderHierarchy = folderHeirarchy;
   }

   @Override
   public final void onEndRoute() {
   }

   @Override
   public final void onStartPoi(String id, String source) {
   }

   @Override
   public final void onEndPoi() {
   }

   @Override
   public final void onLegalNotice(String label) {
   }

   @Override
   public final void onLocation(
      int x,
      int y,
      int zoom,
      String label,
      String description,
      String address,
      String city,
      String region,
      String country,
      String postalCode,
      String phone,
      String fax,
      String url,
      String email,
      String categories,
      double rating,
      String source,
      String sponsored,
      String folderHeirarchy
   ) {
      this._location = new Location(
         y,
         x,
         zoom,
         label,
         description,
         address,
         city,
         region,
         country,
         postalCode,
         phone,
         fax,
         url,
         email,
         categories,
         rating,
         source,
         sponsored,
         null,
         folderHeirarchy
      );
   }

   @Override
   public final void onInstruction(
      int x, int y, int action, int actionInfo, String name, int distance, String address, String description, String exit, String connector, String towards
   ) {
      ((Route)this._location)
         .addDecision(new Decision((Route)this._location, x, y, action, actionInfo, name, distance, address, description, exit, connector, towards));
   }

   @Override
   public final void onPath(IntArray d, int type, String id, String layer, String stroke, int strokeWidth, double opacity) {
      ((Route)this._location).addPath(d, type);
   }

   @Override
   public final void onProfile(IntArray d, int type) {
   }
}
