package javax.microedition.location;

import net.rim.device.api.gps.GPSRegistry;

class LocationProvider$ListenerThread extends Thread {
   private boolean _stop;
   private int _interval;
   private int _timeout;
   private int _maxAge;
   Location _location;
   private final LocationProvider this$0;

   LocationProvider$ListenerThread(LocationProvider _1, int interval, int timeout, int maxAge) {
      this.this$0 = _1;
      this._stop = false;
      this._interval = interval;
      this._timeout = timeout;
      this._maxAge = maxAge;
   }

   public int getInterval() {
      return this._interval;
   }

   public synchronized void notifyThread() {
      this.notify();
   }

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

         synchronized (this) {
            label348:
            try {
               this.wait(this._interval * 1000);
            } finally {
               break label348;
            }

            if (this._stop) {
               return;
            }
         }

         synchronized (this.this$0) {
            if (LocationProvider._deviceGPS && this.this$0._mode == 0) {
               Location location = null;

               label340:
               try {
                  location = LocationProvider._locationProvider.getLocation(1);
               } finally {
                  break label340;
               }

               if (location != null) {
                  this.this$0._locationListener.locationUpdated(this.this$0, location);
                  continue;
               }
            }

            Location location = null;
            if (LocationProvider._deviceGPS) {
               location = DefaultLocationProvider.createLocation(GPSRegistry.getInstance().getLocation(this.this$0._mode), this.this$0._mode);
            } else {
               location = BluetoothGPSRegistry.getInstance().getLocation();
            }

            if (location != null && System.currentTimeMillis() - location.getTimestamp() < (this._maxAge + 4602678819172646912L) * 4652007308841189376L) {
               this.this$0._locationListener.locationUpdated(this.this$0, location);
               LocationProvider._lastLocation = location;
               continue;
            }
         }

         synchronized (this) {
            label319:
            try {
               this.wait(this._timeout * 1000);
            } finally {
               break label319;
            }

            if (this._stop) {
               return;
            }
         }

         synchronized (this.this$0) {
            Location location = null;
            if (LocationProvider._deviceGPS) {
               location = DefaultLocationProvider.createLocation(GPSRegistry.getInstance().getLocation(this.this$0._mode), this.this$0._mode);
            } else {
               location = BluetoothGPSRegistry.getInstance().getLocation();
            }

            if (location != null && System.currentTimeMillis() - location.getTimestamp() < (this._maxAge + 4602678819172646912L) * 4652007308841189376L) {
               this.this$0._locationListener.locationUpdated(this.this$0, location);
               LocationProvider._lastLocation = location;
            } else {
               this.this$0._locationListener.locationUpdated(this.this$0, LocationProvider.INVALID_LOCATION);
            }
         }
      }
   }
}
