package javax.microedition.location;

import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.gps.GPS;
import net.rim.device.api.gps.GPSRegistry;
import net.rim.vm.TraceBack;

public class Orientation {
   private float _azimuth;
   private boolean _isMagnetic;
   private float _pitch = (float)2143289344;
   private float _roll = (float)2143289344;
   private static int ORIENTATION_TIMEOUT = 32;

   public Orientation(float azimuth, boolean isMagnetic, float pitch, float roll) {
      this._azimuth = azimuth;
      this._isMagnetic = isMagnetic;
      this._pitch = pitch;
      this._roll = roll;
   }

   public float getCompassAzimuth() {
      return this._azimuth;
   }

   public boolean isOrientationMagnetic() {
      return this._isMagnetic;
   }

   public float getPitch() {
      return this._pitch;
   }

   public float getRoll() {
      return this._roll;
   }

   public static Orientation getOrientation() {
      LocationProvider.checkSecurity(TraceBack.getCallingModule(0), "lapi_orientation");
      if (!GPS.isSupportedOnCurrentNetwork() && !BluetoothSerialPort.isSupported()) {
         return null;
      }

      if (!LocationProvider._deviceGPS) {
         throw new LocationException("Orientation not supported");
      }

      int mode = 0;
      if (LocationProvider._locationProvider == null) {
         mode = LocationProvider._defaultMode;
      } else {
         mode = LocationProvider._locationProvider._mode;
      }

      long time = System.currentTimeMillis();
      GPSRegistry gpsRegistry = GPSRegistry.getInstance();

      for (int count = 0; count < 3; count++) {
         gpsRegistry.startLocationUpdate(0, mode, null);

         label99:
         try {
            synchronized (gpsRegistry.getLocationLock()) {
               long i = ORIENTATION_TIMEOUT * 1000 - (System.currentTimeMillis() - time);
               if (i <= 0) {
                  return null;
               }

               gpsRegistry.getLocationLock().wait(i);
            }
         } finally {
            break label99;
         }

         Location location = DefaultLocationProvider.createLocation(gpsRegistry.getLocation(mode), mode);
         if (location != null && location.getTimestamp() >= time) {
            float heading = location.getCourse();
            if (!(heading < 0L) && !(heading >= 4645040803167600640L) && !Float.isNaN(heading)) {
               return new Orientation(heading, false, (float)2143289344, (float)2143289344);
            }

            if (System.currentTimeMillis() >= time + ORIENTATION_TIMEOUT * 1000) {
               return null;
            }
         }
      }

      return null;
   }
}
