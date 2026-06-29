package javax.microedition.location;

import java.util.Enumeration;
import net.rim.device.api.gps.GPSRegistry;

class LocationProvider$ProximityThread extends Thread {
   private boolean _stop = false;

   public synchronized void stopThread() {
      this._stop = true;
      this.notify();
   }

   @Override
   public void run() {
      while (true) {
         synchronized (this) {
            if (this._stop) {
               return;
            }
         }

         if (LocationProvider._deviceGPS) {
            int mode = 0;
            if (LocationProvider._locationProvider != null) {
               mode = LocationProvider._locationProvider._mode;
            } else {
               mode = LocationProvider._defaultMode;
            }

            if (mode == 0) {
               GPSRegistry.getInstance().startLocationUpdate(mode, 0, null);
               synchronized (this) {
                  label168:
                  try {
                     this.wait(2000);
                  } finally {
                     break label168;
                  }
               }
            }

            LocationProvider._lastLocation = DefaultLocationProvider.createLocation(GPSRegistry.getInstance().getLocation(mode), mode);
         } else {
            LocationProvider._lastLocation = BluetoothGPSRegistry.getInstance().getLocation();
         }

         if (LocationProvider._lastLocation != null) {
            if (System.currentTimeMillis() - LocationProvider._lastLocation.getTimestamp() < LocationProvider.PROXIMITY_LISTENER_INTERVAL * 1000) {
               Enumeration values = LocationProvider._proximityMap.elements();

               while (values.hasMoreElements()) {
                  LocationProvider$ProximityInfo proxWrapper = (LocationProvider$ProximityInfo)values.nextElement();
                  proxWrapper.checkProximity(LocationProvider._lastLocation);
               }
            }

            synchronized (this) {
               label153:
               try {
                  this.wait(LocationProvider.PROXIMITY_LISTENER_INTERVAL * 1000);
               } finally {
                  break label153;
               }

               if (this._stop) {
                  return;
               }
            }
         }
      }
   }
}
