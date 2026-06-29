package javax.microedition.location;

import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.vm.Process;
import net.rim.vm.TraceBack;

class BluetoothSerialPortGPSProvider extends LocationProvider implements GlobalEventListener {
   BluetoothGPSRegistry _gpsRegistry;
   boolean _locationRequested;

   BluetoothSerialPortGPSProvider() {
      if (BluetoothSerialPort.isSupported()) {
         this._gpsRegistry = BluetoothGPSRegistry.getInstance();
         Application.getApplication().addGlobalEventListener(this);
      }
   }

   @Override
   public int getState() {
      return !this._locationRequested ? 1 : this._gpsRegistry.getState();
   }

   @Override
   public Location getLocation(int timeout) {
      LocationProvider.checkSecurity(TraceBack.getCallingModule(0), "lapi_location");
      if (timeout != 0 && timeout >= -1) {
         if (timeout == -1) {
            timeout = 900;
         }

         long time = System.currentTimeMillis();
         this._gpsRegistry.registerGPSConsumer();
         synchronized (this._gpsRegistry.getBTLocationLock()) {
            this._gpsRegistry.getBTLocationLock().wait(timeout * 1000);
         }

         if (this._locationRequested && this.getState() == 3) {
            throw new LocationException();
         } else if (super._reset) {
            super._reset = false;
            throw new Object();
         } else {
            Location location = this._gpsRegistry.getLocation();
            this._gpsRegistry.removeGPSConsumer(Process.currentProcess().getProcessId());
            this._locationRequested = true;
            if (location != null && location.getTimestamp() > time) {
               LocationProvider._lastLocation = location;
               return location;
            } else {
               throw new LocationException("Timed out while waiting for GPS Location");
            }
         }
      } else {
         throw new Object("Invalid value of timeout parameter");
      }
   }

   @Override
   public void setLocationListener(LocationListener listener, int interval, int timeout, int maxAge) {
      LocationProvider.checkSecurity(TraceBack.getCallingModule(0), "lapi_location");
      if (listener == null) {
         if (LocationProvider._listenerThread != null && LocationProvider._listenerThread.isAlive()) {
            LocationProvider._listenerThread.stopThread();
         }

         LocationProvider._listenerThread = null;
         LocationProvider._locationListenerRunning = false;
         this._gpsRegistry.removeGPSConsumer(Process.currentProcess().getProcessId());
      } else {
         this._gpsRegistry.registerGPSConsumer();
         this._locationRequested = true;
         if (listener == null
            || interval >= -1
               && (interval == -1 || timeout <= interval && maxAge <= interval && (timeout >= 1 || timeout == -1) && (maxAge >= 1 || maxAge == -1))) {
            super._locationListener = listener;
            LocationProvider._locationListenerRunning = true;
            switch (interval) {
               case -1:
               default:
                  interval = 30;
               case -2:
                  timeout = timeout == -1 ? 15 : timeout;
                  maxAge = maxAge == -1 ? 8 : maxAge;
                  if (LocationProvider._listenerThread != null) {
                     LocationProvider._listenerThread.stopThread();
                  }

                  LocationProvider._listenerThread = new LocationProvider$ListenerThread(this, interval, timeout, maxAge);
                  LocationProvider._listenerThread.start();
                  return;
               case 0:
            }
         } else {
            throw new Object("Illegal value of interval, timeout or maxAge passed in");
         }
      }
   }

   @Override
   public void reset() {
      super._reset = true;
      synchronized (this._gpsRegistry.getBTLocationLock()) {
         this._gpsRegistry.getBTLocationLock().notifyAll();
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 3338410696610913563L && super._locationListener != null) {
         super._locationListener.providerStateChanged(this, data0);
      }
   }
}
