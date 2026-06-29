package net.rim.device.apps.internal.elt;

import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

final class SingleFixListener implements LocationListener {
   private ETManager _callback;
   private int _skippedFixes = 0;
   private int _maxSkipCount = 1;
   private Location _currentLocation = null;

   public SingleFixListener(ETManager callback, int maxSkipCount) {
      this._callback = callback;
      this._maxSkipCount = maxSkipCount;
   }

   public final Location getCurrentLocation() {
      return this._currentLocation;
   }

   @Override
   public final void locationUpdated(LocationProvider provider, Location location) {
      if (location.isValid()) {
         String satCountStr = location.getExtraInfo("satellites");
         if (satCountStr != null) {
            int satCount = Integer.parseInt(satCountStr);
            if (satCount == 0) {
               return;
            }
         }

         if (this._skippedFixes < this._maxSkipCount) {
            this._skippedFixes++;
            return;
         }

         this._currentLocation = location;
         this._callback.newLocation(location);
      }
   }

   @Override
   public final void providerStateChanged(LocationProvider provider, int newState) {
      if (newState != 1) {
         this._currentLocation = null;
         this._callback.locationError();
      }
   }
}
