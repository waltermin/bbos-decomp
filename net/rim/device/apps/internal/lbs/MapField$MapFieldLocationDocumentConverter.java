package net.rim.device.apps.internal.lbs;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.lbs.content.IntArray;
import net.rim.device.apps.internal.lbs.content.LocationDocumentConverter;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class MapField$MapFieldLocationDocumentConverter extends LocationDocumentConverter {
   MapField _mapField;
   String _currentSource;
   private final MapField this$0;

   public MapField$MapFieldLocationDocumentConverter(MapField this$0, MapField mapField) {
      this.this$0 = this$0;
      this._currentSource = null;
      this._mapField = mapField;
   }

   @Override
   public final void onStartGetRoute() {
      this.this$0._routePlanner = new RoutePlanner(this._mapField);
   }

   @Override
   public final void onEndGetRoute() {
      this.this$0._routePlanner.getRoute();
      this.this$0._routePlanner = null;
   }

   @Override
   public final void onStartRoute(int distance, int time, String routeName, String folderHeirarchy) {
      this.this$0._currentLocations.clearRoutes();
      this.this$0._currentRoute = new Route(this._mapField, distance, time, routeName);
      this.this$0._currentRoute._folderHierarchy = folderHeirarchy;
   }

   @Override
   public final void onEndRoute() {
      this.this$0._currentRoute.createDecisionPointBreadcrumbs();
      this.this$0._currentRoute._startAddress = this.this$0._currentRoute._decisions._decisions[0]._address;
      this.this$0._currentRoute._endAddress = this.this$0._currentRoute._decisions._decisions[this.this$0._currentRoute._decisions._count - 1]._address;
      this.this$0._currentRoute._location = new Location(
         this.this$0._currentRoute._latitude,
         this.this$0._currentRoute._longitude,
         -1,
         "",
         "",
         "",
         "",
         "",
         "",
         "",
         "",
         "",
         "",
         "",
         "",
         (double)-4616189618054758400L,
         "",
         "",
         this.this$0._currentRoute,
         ""
      );
      if (this.this$0._currentRoute._routeName == null || this.this$0._currentRoute._routeName.equals("")) {
         this.this$0._currentRoute._routeName = MessageFormat.format(
            LBSResources.getString(236), new Object[]{this.this$0._currentRoute._startAddress, this.this$0._currentRoute._endAddress}
         );
         this.this$0._currentRoute._label = this.this$0._currentRoute._routeName;
      }

      this.this$0._currentLocations.addRoute(this.this$0._currentRoute._location);
      this.expandBBox(this.this$0._currentRoute._bbox);
      this.this$0._currentRoute._decisions.setFocus(null);
      this.this$0._currentRoute = null;
      if (this._mapField.getRoute() != null) {
         this._mapField._directionsListScreen = new DirectionsListScreen(this._mapField, -1);
         this.this$0._pushDirectionsScreen = true;
      }
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
      if (rating == 4607182418800017408L) {
         rating = (double)-4616189618054758400L;
      }

      if (source == null || source.length() == 0) {
         source = this._currentSource;
      }

      Location location = new Location(
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
      if (location._latitude == 0 && location._longitude == 0) {
         if (StringUtilities.strEqualIgnoreCase(location._sponsored, "yes")) {
            Arrays.add(this.this$0._currentAds, location);
         }
      } else {
         if (this.this$0._routePlanner != null) {
            this.this$0._routePlanner.add(location);
            return;
         }

         this.this$0._currentLocations.add(location);
         this.expandBBox((XYRect)(new Object(x, y, 1, 1)));
         if (this.this$0._lbsDocSource != null && this.this$0._lbsDocSource.equals("POI_SERVER")) {
            Arrays.add(this.this$0._currentPOIs, location);
            this.this$0._currentLocations.setFocus(this.this$0._currentPOIs[0]);
            return;
         }
      }
   }

   @Override
   public final void onLegalNotice(String label) {
      Arrays.add(this.this$0._currentLegalNotices, label);
   }

   @Override
   public final void onStartPoi(String id, String source) {
      this._currentSource = source;
   }

   @Override
   public final void onEndPoi() {
      this._currentSource = null;
   }

   @Override
   public final void onInstruction(
      int x, int y, int action, int actionInfo, String name, int distance, String address, String description, String exit, String connector, String towards
   ) {
      if (this.this$0._currentRoute != null) {
         this.this$0
            ._currentRoute
            ._decisions
            .add(new Decision(this.this$0._currentRoute, x, y, action, actionInfo, name, distance, address, description, exit, connector, towards));
      }
   }

   @Override
   public final void onPath(IntArray d, int type, String id, String layerID, String stroke, int strokeWidth, double opacity) {
      if (this.this$0._currentRoute != null) {
         this.this$0._currentRoute.addPath(d, type);
      }
   }

   @Override
   public final void onProfile(IntArray d, int type) {
   }

   private final void expandBBox(XYRect rect) {
      if (rect != null) {
         if (this._mapField._bbox == null) {
            this._mapField._bbox = (XYRect)(new Object(rect));
            return;
         }

         this._mapField._bbox.union(rect);
      }
   }
}
