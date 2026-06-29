package javax.microedition.location;

import net.rim.device.api.gps.GPS;
import net.rim.device.api.gps.GPSLocation;
import net.rim.device.api.gps.GPSLocationExtended;
import net.rim.device.api.gps.GPSLocationStandard;
import net.rim.device.api.gps.GPSRegistry;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.vm.TraceBack;

class DefaultLocationProvider extends LocationProvider implements GlobalEventListener {
   boolean _firstLocationRequest;
   static boolean _gpsListenerRunning;
   static Class class$javax$microedition$location$DefaultLocationProvider;

   DefaultLocationProvider(int firstMode, int mode) {
      super._firstMode = firstMode;
      super._mode = mode;
      this._firstLocationRequest = true;
      Application.getApplication().addGlobalEventListener(this);
   }

   @Override
   public int getState() {
      if (!GPS.isSupported()) {
         return 3;
      } else if (super._mode == 1 && GPSRegistry.isVerizon() && !GPSRegistry.getInstance().getCredentialStatus(Application.getApplication().getProcessId())) {
         return 2;
      } else {
         return GPS.getMode() == 2 ? 1 : 2;
      }
   }

   @Override
   public Location getLocation(int timeout) throws LocationException {
      if (Application.isEventDispatchThread()) {
         throw new LocationException("getLocation() method cannot be called from event thread");
      }

      LocationProvider.checkSecurity(TraceBack.getCallingModule(0), "lapi_location");
      synchronized (this) {
         if (timeout == 0 || timeout < -1) {
            throw new Object("Invalid value of timeout parameter");
         }

         if (timeout == -1) {
            timeout = 900;
         }
      }

      long time = System.currentTimeMillis();
      GPSRegistry gpsRegistry = GPSRegistry.getInstance();
      int count = 0;

      while (count < 3) {
         gpsRegistry.startLocationUpdate(0, super._mode, super._criteria);
         synchronized (gpsRegistry.getLocationLock()) {
            gpsRegistry.getLocationLock().wait(timeout * 1000 - (System.currentTimeMillis() - time));
         }

         if (super._reset) {
            super._reset = false;
            throw new Object();
         }

         createLocation(gpsRegistry.getLocation(super._mode), super._mode);
         if (LocationProvider._lastLocation != null && LocationProvider._lastLocation.getTimestamp() >= time) {
            return LocationProvider._lastLocation;
         }

         if (System.currentTimeMillis() >= time + timeout * 1000) {
            gpsRegistry.stopLocationUpdate(super._mode);
            throw new LocationException("Timed out while waiting for GPS Location");
         }

         count++;
         gpsRegistry.stopLocationUpdate(super._mode);

         try {
            Thread.sleep(2000);
         } finally {
            continue;
         }
      }

      gpsRegistry.stopLocationUpdate(super._mode);
      throw new LocationException("Timed out while waiting for GPS Location");
   }

   @Override
   public void setLocationListener(LocationListener listener, int interval, int timeout, int maxAge) {
      LocationProvider.checkSecurity(TraceBack.getCallingModule(0), "lapi_location");
      if (listener == null
         || interval >= -1 && (interval == -1 || timeout <= interval && maxAge <= interval && (timeout >= 1 || timeout == -1) && (maxAge >= 1 || maxAge == -1))
         )
       {
         if (LocationProvider._listenerThread != null) {
            LocationProvider._listenerThread.stopThread();
            GPSRegistry.getInstance().stopLocationUpdate(super._mode);
         }

         synchronized (class$javax$microedition$location$DefaultLocationProvider == null
            ? (class$javax$microedition$location$DefaultLocationProvider = class$("javax.microedition.location.DefaultLocationProvider"))
            : class$javax$microedition$location$DefaultLocationProvider) {
            if (listener == null) {
               super._locationListener = null;
               LocationProvider._locationListenerRunning = false;
               return;
            }

            LocationProvider._locationListenerRunning = true;
         }

         switch (interval) {
            case -1:
            default:
               interval = 30;
            case -2:
               timeout = timeout == -1 ? 15 : timeout;
               maxAge = maxAge == -1 ? 8 : maxAge;
               GPSRegistry gpsRegistry = GPSRegistry.getInstance();
               gpsRegistry.startLocationUpdate(interval, super._mode, super._criteria);
               super._locationListener = listener;
               LocationProvider._listenerThread = new LocationProvider$ListenerThread(this, interval, timeout, maxAge);
               LocationProvider._listenerThread.start();
               return;
            case 0:
         }
      } else {
         throw new Object("Illegal value of interval, timeout or maxAge passed in");
      }
   }

   @Override
   public void reset() {
      super._reset = true;
      synchronized (GPSRegistry.getInstance().getLocationLock()) {
         GPSRegistry.getInstance().getLocationLock().notifyAll();
      }

      if (LocationProvider._listenerThread != null) {
         LocationProvider._listenerThread.stopThread();
      }

      GPSRegistry.getInstance().stopLocationUpdate(super._mode);
   }

   static Location createLocation(GPSLocation gpsLocation, int mode) {
      try {
         if (!(gpsLocation instanceof Object)) {
            if (gpsLocation instanceof Object) {
               GPSLocationStandard standardLocation = (GPSLocationStandard)gpsLocation;
               LocationProvider._lastCoordinates.setLatitude(standardLocation.getLatitude());
               LocationProvider._lastCoordinates.setLongitude(standardLocation.getLongitude());
               LocationProvider._lastCoordinates.setAltitude((float)2143289344);
               LocationProvider._lastCoordinates.setVerticalAccuracy((float)2143289344);
               LocationProvider._lastCoordinates.setHorizontalAccuracy((float)2143289344);
               LocationProvider._lastLocation.setSpeed((float)2143289344);
               LocationProvider._lastLocation.setCourse((float)2143289344);
               LocationProvider._lastLocation.setTimestamp(standardLocation.getUTCTime());
            }
         } else {
            GPSLocationExtended extendedLocation = (GPSLocationExtended)gpsLocation;
            if (extendedLocation.getLatitude() == 9221120237041090560L
               || extendedLocation.getLongitude() == 9221120237041090560L
               || extendedLocation.getNumberOfSatellitesVisible() <= 0) {
               return null;
            }

            LocationProvider._lastCoordinates.setLatitude(extendedLocation.getLatitude());
            LocationProvider._lastCoordinates.setLongitude(extendedLocation.getLongitude());
            int altitude = extendedLocation.getAltitude();
            if ((altitude & 65535) == 65535) {
               LocationProvider._lastCoordinates.setAltitude((float)2143289344);
            } else {
               LocationProvider._lastCoordinates.setAltitude(extendedLocation.getAltitude());
            }

            double vertUnc = extendedLocation.getVerticalUncertainity();
            if (vertUnc == 9221120237041090560L) {
               LocationProvider._lastCoordinates.setVerticalAccuracy((float)2143289344);
            } else {
               LocationProvider._lastCoordinates.setVerticalAccuracy((float)vertUnc);
            }

            double horzUnc = extendedLocation.getHorizontalUncertainty();
            if (horzUnc == 9221120237041090560L) {
               LocationProvider._lastCoordinates.setHorizontalAccuracy((float)2143289344);
            } else {
               LocationProvider._lastCoordinates.setHorizontalAccuracy((float)horzUnc);
            }

            LocationProvider._lastLocation.setTimestamp(extendedLocation.getUTCTime());
            LocationProvider._lastLocation.setSpeed(extendedLocation.getVelocity());
            LocationProvider._lastLocation.setCourse(extendedLocation.getHeading());
            LocationProvider._lastLocation.setSatellites(extendedLocation.getNumberOfSatellitesVisible());
         }

         switch (mode) {
            case -1:
               break;
            case 0:
            default:
               LocationProvider._lastLocation.setLocationMethod(8);
               break;
            case 1:
               LocationProvider._lastLocation.setLocationMethod(327681);
               break;
            case 2:
               LocationProvider._lastLocation.setLocationMethod(589825);
         }

         LocationProvider._lastLocation.setValid(true);
      } finally {
         return LocationProvider._lastLocation;
      }

      return LocationProvider._lastLocation;
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7999774137434187609L && super._locationListener != null) {
         super._locationListener.providerStateChanged(this, 2);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
