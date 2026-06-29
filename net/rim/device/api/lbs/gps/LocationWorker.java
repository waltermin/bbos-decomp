package net.rim.device.api.lbs.gps;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;
import net.rim.device.api.gps.GPS;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.internal.bluetooth.BluetoothME;

public class LocationWorker implements LocationListener, LocationCallback {
   private LocationDevice _callback;
   private LocationProvider _lProvider;
   protected GPSLocationData _locationDataInternal;
   NmeaStream _nmeaStream = new NmeaStream();
   private long _startTime = 0;
   private boolean _firstTime = false;
   private static final long GUID = 7528405864290685501L;
   private static final float MS_TO_KNOTS = 1.9438444F;
   private static final int GPS_LOCK_TIMEOUT = 900000;
   private static final String NMEA_MIME = "application/X-jsr179-location-nmea";
   private static ResourceBundle _lbsBundle;
   private static LocationWorker INSTANCE = null;

   public static final LocationWorker getInstance() {
      if (INSTANCE == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         INSTANCE = (LocationWorker)ar.get(7528405864290685501L);
         if (INSTANCE == null) {
            INSTANCE = new LocationWorker();
            ar.put(7528405864290685501L, INSTANCE);
         }
      }

      return INSTANCE;
   }

   private LocationWorker() {
      _lbsBundle = ResourceBundle.getBundle(5578399137938411462L, "net.rim.device.api.lbs.LBSapi");
   }

   protected LocationDevice getCallback() {
      return this._callback;
   }

   public void checkLocationProvider() {
   }

   public void setLocationDevice(LocationDevice device) {
      this._callback = device;
      if (this._callback != null) {
         this._callback._status = 0;
         if (this._callback.isInternalGPS()) {
            GPS.setLAPIDataSource(GPS.GPS_SOURCE_DEVICE);
            return;
         }

         GPS.setLAPIDataSource(this._callback._name);
      }
   }

   private final void getLocationProvider() {
      if (this._lProvider == null) {
         this._lProvider = this.createLocationProvider(false, false);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final LocationProvider createLocationProvider(boolean forceAutonomous, boolean highPower) {
      try {
         Criteria criteria = new Criteria();
         criteria.setAddressInfoRequired(false);
         criteria.setAltitudeRequired(false);
         criteria.setPreferredResponseTime(forceAutonomous ? 60 : 16);
         criteria.setSpeedAndCourseRequired(true);
         if (highPower) {
            criteria.setPreferredPowerConsumption(3);
         } else {
            criteria.setPreferredPowerConsumption(2);
         }

         criteria.setHorizontalAccuracy(32);
         criteria.setVerticalAccuracy(32);
         int capability = 4;
         if (this._callback.isInternalGPS()) {
            capability = GPS.getAidCapability();
         }

         boolean assistModeSupported = (capability & 2) != 0;
         boolean cellModeSupported = (capability & 1) != 0;
         boolean costing = !forceAutonomous ? assistModeSupported || cellModeSupported : false;
         if ((RadioInfo.getActiveWAFs() & 2) == 2 && Branding.getVendorId() == 106) {
            costing = false;
         }

         criteria.setCostAllowed(costing);
         return LocationProvider.getInstance(criteria);
      } catch (Throwable var10) {
         EventLogger.logEvent(4560142210062134028L, e.getMessage().getBytes(), 2);
         return null;
      }
   }

   private void handleBluetoothPowerOn() {
      if (this._callback != null && !this._callback.isInternalGPS() && !BluetoothME.isPowerOn()) {
         int status = BluetoothME.requestPowerOn();
         if (status == 2) {
            synchronized (this) {
               try {
                  this.wait(3000);
               } finally {
                  return;
               }

               return;
            }
         }
      }
   }

   protected GPSLocationData singleFix() {
      this.handleBluetoothPowerOn();
      GPSLocationData data = new GPSLocationData();
      data.setValid(false);
      if (this._callback != null && this._callback._status == 0) {
         LocationProvider provider = this.createLocationProvider(false, true);
         SingleFixListener listener = new SingleFixListener(this, 2);
         provider.setLocationListener(listener, 1, 1, 1);
         synchronized (this) {
            label48:
            try {
               this.wait();
            } finally {
               break label48;
            }
         }

         Location location = listener.getCurrentLocation();
         if (location != null && location.isValid()) {
            QualifiedCoordinates coords = location.getQualifiedCoordinates();
            if (coords != null) {
               int latitude = (int)(coords.getLatitude() * 4681608360884174848L);
               int longitude = (int)(coords.getLongitude() * 4681608360884174848L);
               float bearing = location.getCourse();
               float speed = location.getSpeed() * 1073270757;
               data.setValid(true);
               this._callback.setGPSDataValues(data, latitude, longitude, bearing, speed, true);
            }
         }

         provider.setLocationListener(null, 0, 0, 0);
         provider.reset();
      }

      return data;
   }

   public final void startDelayedFix() {
   }

   public final void stopDelayedFix() {
   }

   protected final void resetLocationProvider() {
      this._lProvider = null;
   }

   protected final void setInterval(int interval) {
      if (this._lProvider != null && this._callback != null) {
         this._lProvider.setLocationListener(this, interval, 1, 1);
      }
   }

   protected final String getString(int resourceID) {
      if (_lbsBundle == null) {
         _lbsBundle = ResourceBundle.getBundle(5578399137938411462L, "net.rim.device.api.lbs.LBSapi");
      }

      return _lbsBundle.getString(resourceID);
   }

   protected final boolean startReporting() {
      if (this._callback == null) {
         return false;
      }

      this._firstTime = true;
      this.handleBluetoothPowerOn();
      this._locationDataInternal.reset();
      this._callback.setGPSDataValues(Integer.MAX_VALUE, Integer.MAX_VALUE, (float)false, (float)false, false);
      this.getLocationProvider();
      if (this._lProvider != null) {
         this._callback._status = 2;
         this._callback.fireDeviceEvent(this._callback, this.getString(16));
         this._startTime = System.currentTimeMillis();

         try {
            this._lProvider.setLocationListener(this, 1, 1, 1);
            return true;
         } finally {
            this.stopReporting();
            return false;
         }
      } else {
         return false;
      }
   }

   protected final boolean stopReporting() {
      if (this._lProvider != null) {
         this._lProvider.setLocationListener(null, 0, 0, 0);
         this._locationDataInternal.reset();
      }

      if (this._callback != null) {
         this._callback.setGPSinvalid();
         this._callback._status = 0;
      }

      return true;
   }

   @Override
   public void locationUpdated(LocationProvider provider, Location location) {
      boolean sendUpdate = false;
      int previousSatelliteCount = this._locationDataInternal.getSatelliteCount();
      if (!location.isValid()) {
         if (System.currentTimeMillis() - this._startTime >= 900000) {
            this._callback._status = 10;
            this._callback.fireDeviceEvent(this._callback, this.getString(10));
            return;
         }

         this._locationDataInternal.setValid(false);
      } else {
         if (this._firstTime) {
            this._firstTime = false;
            this._callback._status = 2;
            this._callback.fireDeviceEvent(this._callback, this.getString(16));
            return;
         }

         this._locationDataInternal.setValid(true);
         this._startTime = System.currentTimeMillis();
         QualifiedCoordinates coords = location.getQualifiedCoordinates();
         if (coords != null) {
            String satCountStr = location.getExtraInfo("satellites");
            if (satCountStr != null) {
               this._locationDataInternal.setSatelliteCount(Integer.parseInt(satCountStr));
               if (Integer.parseInt(satCountStr) == 0) {
                  this._locationDataInternal.setValid(false);
               }
            } else {
               satCountStr = location.getExtraInfo("application/X-jsr179-location-nmea");
               if (satCountStr != null) {
                  this._nmeaStream.parseSentence(this._locationDataInternal, satCountStr);
               }
            }

            int latitude = (int)(coords.getLatitude() * 4681608360884174848L);
            int longitude = (int)(coords.getLongitude() * 4681608360884174848L);
            float bearing = location.getCourse();
            float speed = location.getSpeed() * 1073270757;
            sendUpdate = previousSatelliteCount != this._locationDataInternal.getSatelliteCount()
               || this._locationDataInternal.getLatitude() != latitude
               || this._locationDataInternal.getLongitude() != longitude
               || this._locationDataInternal.getBearing() != bearing
               || this._locationDataInternal.getSpeed() != speed;
            this._callback.setGPSDataValues(latitude, longitude, bearing, speed, this._locationDataInternal._isValid);
            this._callback.setAccuracy(coords.getVerticalAccuracy(), coords.getHorizontalAccuracy());
         }
      }

      if (this._callback._status != 1) {
         if (!location.isValid()) {
            this._callback._status = 2;
            this._callback.fireDeviceEvent(this._callback, this.getString(16));
         } else {
            this._callback._status = 1;
            this._callback.fireDeviceEvent(this._callback, "");
         }
      } else if (this._callback._status == 1 && !this._locationDataInternal.isValid()) {
         this._callback._status = 2;
         this._callback.fireDeviceEvent(this._callback, this.getString(16));
      }

      if (sendUpdate) {
         this._callback.updateData(true);
      }
   }

   @Override
   public void providerStateChanged(LocationProvider provider, int newState) {
      if (this._callback == null) {
         EventLogger.logEvent(4560142210062134028L, ("providerStateChanged: state = " + newState + ", callback = null").getBytes(), 0);
      } else if (newState == 2) {
         this._callback._status = 10;
         this._callback.fireDeviceEvent(this._callback, this.getString(10));
         this._startTime = System.currentTimeMillis();
      } else {
         if (newState == 3) {
            this._callback._status = 10;
            this._callback.fireDeviceEvent(this._callback, this.getString(10));
            this._startTime = System.currentTimeMillis();
         }
      }
   }

   @Override
   public void locationError() {
      synchronized (this) {
         this.notify();
      }
   }

   @Override
   public void newLocation(Location location) {
      synchronized (this) {
         this.notify();
      }
   }
}
