package net.rim.device.apps.internal.browser.javascript;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;

final class ESBlackberryLocation extends RedirectedObject implements LocationListener {
   private boolean _showWarning = true;
   private boolean _allowAccess;
   private final JavaScriptEngine _scriptEngine;
   private final String[] _gpsListeners;
   private boolean _gpsSupported;
   private LocationProvider _locationProvider;
   private boolean _listenerRegistered;

   final void documentClosed() {
      try {
         if (this._locationProvider != null) {
            this._locationProvider.setLocationListener(null, -1, -1, -1);
            this._listenerRegistered = false;
            this._locationProvider.reset();
            this._locationProvider = null;
            return;
         }
      } finally {
         return;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void locationUpdated(LocationProvider provider, Location location) {
      if (location != null && location.isValid()) {
         for (int i = 0; i < this._gpsListeners.length; i++) {
            if (this._gpsListeners[i] != null) {
               boolean var6 = false /* VF: Semaphore variable */;

               try {
                  var6 = true;
                  this._scriptEngine.executeMethod(null, this._gpsListeners[i], null, false);
                  var6 = false;
               } finally {
                  if (var6) {
                     this._gpsListeners[i] = null;
                     continue;
                  }
               }
            }
         }
      }
   }

   @Override
   public final void providerStateChanged(LocationProvider provider, int newState) {
   }

   private final boolean showWarning() {
      if (!this._scriptEngine._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 16, false)) {
         return false;
      } else if (!this._showWarning) {
         return this._allowAccess;
      } else {
         this._showWarning = false;
         String[] choices = CommonResource.getStringArray(10012);
         if (BackgroundDialog.getChoice(BrowserResources.getString(644), choices, 1) == 0) {
            this._allowAccess = true;
            return true;
         } else {
            this._allowAccess = false;
            return false;
         }
      }
   }

   @Override
   public final long requestFieldValue(String name) {
      try {
         if (name == Names.GPSSupported) {
            return Value.makeBooleanValue(this._gpsSupported);
         }

         if (name == Names.longitude) {
            Location location = LocationProvider.getLastKnownLocation();
            if (location != null && this.showWarning()) {
               return Value.makeDoubleValue(location.getQualifiedCoordinates().getLongitude());
            }

            return Value.makeIntegerValue(0);
         }

         if (name == Names.latitude) {
            Location location = LocationProvider.getLastKnownLocation();
            if (location != null && this.showWarning()) {
               return Value.makeDoubleValue(location.getQualifiedCoordinates().getLatitude());
            }

            return Value.makeIntegerValue(0);
         }
      } finally {
         return Value.DEFAULT;
      }

      return Value.DEFAULT;
   }

   private final void setCriteria(Criteria criteria) {
      label31:
      try {
         this._locationProvider = LocationProvider.getInstance(criteria);
      } finally {
         break label31;
      }

      if (this._locationProvider != null) {
         this._gpsSupported = this._locationProvider.getState() != 3;
      } else {
         this._gpsSupported = false;
      }
   }

   public ESBlackberryLocation(JavaScriptEngine engine) {
      this._scriptEngine = engine;
      this.setCriteria((Criteria)(new Object()));
      this._gpsListeners = new Object[0];
      this.addHostFunction(new ESBlackberryLocation$1(this, Names.Location, "refreshLocation", 0));
      this.addHostFunction(new ESBlackberryLocation$2(this, Names.Location, "onLocationUpdate", 0));
      this.addHostFunction(new ESBlackberryLocation$3(this, Names.Location, "setAidMode", 0));
   }
}
