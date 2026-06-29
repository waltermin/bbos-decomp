package net.rim.device.apps.internal.lbs;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.lbs.maplet.MapRect;
import net.rim.vm.Array;

final class CurrentLocations implements CollectionListener {
   XYRect _bbox = (XYRect)(new Object());
   MapField _mapField = null;
   Location _routeLocation = null;
   Location[] _locations = new Location[0];
   int _focus;
   int _count;
   boolean _semiFocus = false;
   public static final int NO_PAN = 0;
   public static final int PAN_IF_OFFSCREEN = 1;
   public static final int PAN_TO_CENTRE = 2;

   public CurrentLocations(MapField mapField) {
      this._mapField = mapField;
      LocationDocumentCollection.getInstance().addCollectionListener(new Object(this));
   }

   final Location getFocus() {
      return this._focus >= 0 && this._focus < this._locations.length && !this._semiFocus ? this._locations[this._focus] : null;
   }

   final Location getSemiFocus() {
      return this._focus >= 0 && this._focus < this._locations.length ? this._locations[this._focus] : null;
   }

   public final void setSemiFocused(boolean semiFocus) {
      this._semiFocus = semiFocus;
      if (semiFocus) {
         this._mapField._showCaption = false;
      } else {
         this._mapField._showCaption = true;
      }
   }

   public final void setFocus(Location location) {
      for (int i = 0; i < this._count; i++) {
         if (this._locations[i] == location) {
            this._focus = i;
            break;
         }
      }

      this.setSemiFocused(false);
   }

   public final void setFocusAuto() {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   final Location getCenterLocation() {
      Location closestLocation = null;
      if (this._count > 0) {
         MapRect window = new MapRect();
         int width = 38 << this._mapField._zoom;
         int height = 42 << this._mapField._zoom;
         int padding = 5 << this._mapField._zoom;
         window._bottom = this._mapField._latitude - height;
         window._top = this._mapField._latitude + padding;
         window._left = this._mapField._longitude - width / 2;
         window._right = this._mapField._longitude + width / 2;
         int minDistanceSq = Integer.MAX_VALUE;
         Location location = null;

         for (int i = 0; i < this._count; i++) {
            location = this._locations[i];
            if (window.hitTest(location._longitude, location._latitude) == 0) {
               int dx = location._longitude - this._mapField._longitude;
               int dy = location._latitude - this._mapField._latitude;
               int distanceSq = dx * dx + dy * dy;
               if (distanceSq < minDistanceSq) {
                  minDistanceSq = distanceSq;
                  closestLocation = location;
               }
            }
         }
      }

      return closestLocation;
   }

   final boolean hasLocations() {
      return this._locations.length > 0;
   }

   final void clear() {
      for (int i = 0; i < this._count; i++) {
         this._locations[i] = null;
      }

      this._locations = new Location[0];
      this._mapField._currentPOIs = new Location[0];
      this._mapField._currentAds = new Location[0];
      this._mapField._currentLegalNotices = new Object[0];
      this._focus = 0;
      this._count = 0;
      this._bbox.x = 0;
      this._bbox.y = 0;
      this._bbox.width = 0;
      this._bbox.height = 0;
      this.clearAds();
      this._mapField._lastUsedScreen = null;
      this._mapField._locationsListScreen = null;
   }

   final void clear(Location location) {
      int pos = this.clear(location, this._locations);
      if (pos == this._focus) {
         this._focus = 0;
      }

      this._count--;
      Arrays.remove(this._mapField._currentPOIs, location);
      this._bbox.x = 0;
      this._bbox.y = 0;
      this._bbox.width = 0;
      this._bbox.height = 0;
      if (this._routeLocation != null) {
         this._bbox.union(this._routeLocation._longitude, this._routeLocation._latitude, 1, 1);
      }

      for (int i = 0; i < this._count; i++) {
         this._bbox.union(this._locations[i]._longitude, this._locations[i]._latitude, 1, 1);
      }

      if (this._count == 0) {
         this._mapField._locationsListScreen = null;
         if (this._mapField._lastUsedScreen instanceof LocationsListScreen) {
            this._mapField._lastUsedScreen = null;
         }
      }
   }

   private final int clear(Location loc, Location[] locations) {
      int locIx = 0;

      for (int i = 0; i < this._count; i++) {
         if (locations[i] == loc) {
            locIx = i;
            break;
         }
      }

      for (int i = locIx; i < this._count - 1; i++) {
         locations[i] = locations[i + 1];
      }

      Array.resize(locations, this._count == 0 ? this._count : this._count - 1);
      return locIx;
   }

   final void clearRoutes() {
      if (this._routeLocation != null) {
         this._routeLocation._route = null;
         this._routeLocation = null;
      }

      this._bbox.x = 0;
      this._bbox.y = 0;
      this._bbox.width = 0;
      this._bbox.height = 0;

      for (int i = 0; i < this._count; i++) {
         this._bbox.union(this._locations[i]._longitude, this._locations[i]._latitude, 1, 1);
      }

      this._mapField._directionsListScreen = null;
      if (this._mapField._lastUsedScreen instanceof DirectionsListScreen) {
         this._mapField._lastUsedScreen = null;
      }
   }

   final void clearAds() {
      for (int i = this._mapField._currentAds.length - 1; i >= 0; i--) {
         Arrays.removeAt(this._mapField._currentAds, i);
      }
   }

   private final boolean add(Location[] locations, Location location) {
      boolean newLocation = true;

      for (int i = 0; i < this._count; i++) {
         if (location._latitude == this._locations[i]._latitude
            && location._longitude == this._locations[i]._longitude
            && location._address != null
            && this._locations[i]._address != null
            && location._address.equals(this._locations[i]._address)
            && location._label != null
            && this._locations[i]._label != null
            && location._label.equals(locations[i]._label)) {
            newLocation = false;
         }
      }

      if (newLocation) {
         Arrays.add(locations, location);
      }

      return newLocation;
   }

   final void add(Location location) {
      boolean newLocation = this.add(this._locations, location);
      if (newLocation) {
         this._count++;
      }

      this._bbox.union(location._longitude, location._latitude, 1, 1);
      this.setFocus(location);
   }

   final void addRoute(Location location) {
      if (location != null) {
         this._routeLocation = location;
         this._bbox.union(location._route._bbox);
      }
   }

   final boolean moveFocus(int inc, int rule) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   final void pan(int rule) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   final void paint(Graphics graphics, Transform transform, boolean showCaption) {
      if (this._routeLocation != null) {
         if (this._mapField._lastUsedScreen instanceof DirectionsListScreen) {
            this._routeLocation._route.paint(graphics, transform, showCaption);
         } else {
            Decision focus = this._routeLocation._route._decisions.getFocus();
            this._routeLocation._route._decisions.setFocus(null);
            this._routeLocation._route.paint(graphics, transform, showCaption);
            this._routeLocation._route._decisions.setFocus(focus);
         }
      }

      if (!(this._mapField._lastUsedScreen instanceof DirectionsListScreen)) {
         for (int i = 0; i < this._count; i++) {
            if (i != this._focus) {
               this._locations[i].paint(graphics, transform, false, false, i + 1);
            }
         }

         Location location = this.getSemiFocus();
         if (location != null) {
            boolean focus = true;
            if (this._count > 1) {
               location.paint(graphics, transform, focus, showCaption, this._focus + 1);
               return;
            }

            location.paint(graphics, transform, focus, showCaption, -1);
         }
      }
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      LocationSyncable collectionLoc = (LocationSyncable)newElement;
      Location loc = LocationDocumentCollection.getInstance().getLocation(collectionLoc);

      for (int i = 0; i < this._count; i++) {
         if (collectionLoc.getUID() == this._locations[i]._uid) {
            this._locations[i].copyFrom(this._locations[i], loc);
            return;
         }
      }
   }

   @Override
   public final void reset(Collection collection) {
   }
}
