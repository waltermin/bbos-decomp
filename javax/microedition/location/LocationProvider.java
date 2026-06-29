package javax.microedition.location;

import java.util.Vector;
import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.bluetooth.BluetoothSerialPortInfo;
import net.rim.device.api.gps.GPS;
import net.rim.device.api.gps.GPS$AppCriteria;
import net.rim.device.api.gps.GPSRegistry;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.MultiMap;
import net.rim.device.internal.gps.GPSFirewall;
import net.rim.vm.TraceBack;

public class LocationProvider {
   LocationListener _locationListener;
   boolean _isBTSimulation;
   GPS$AppCriteria _criteria;
   int _firstMode;
   int _mode;
   boolean _reset;
   public static final int AVAILABLE = 1;
   public static final int TEMPORARILY_UNAVAILABLE = 2;
   public static final int OUT_OF_SERVICE = 3;
   static final int DEFAULT_FREQUENCY = 30;
   static final int DEFAULT_TIMEOUT = 15;
   static final int DEFAULT_MAXAGE = 8;
   static final int DEFAULT_GETLOCATION_TIMEOUT = 900;
   static int PROXIMITY_LISTENER_INTERVAL = 20;
   static final Location INVALID_LOCATION = new Location();
   private static MultiMap _proximityMap = new MultiMap();
   static boolean _proximityListenerRunning;
   static LocationProvider$ProximityThread _proximityThread;
   static LocationProvider _locationProvider;
   static Location _lastLocation = new Location();
   static QualifiedCoordinates _lastCoordinates = new QualifiedCoordinates((double)0L, (double)0L, (float)2143289344, (float)2143289344, (float)2143289344);
   static LocationProvider$ListenerThread _listenerThread;
   static boolean _locationListenerRunning;
   static int _defaultMode;
   static boolean _autoModeSupported;
   static boolean _assistModeSupported;
   static boolean _cellModeSupported;
   static boolean _deviceGPS;
   private static PersistentObject _gpsDataSourceStore;
   private static ResourceBundle _rb = ResourceBundle.getBundle(3100685609005034344L, "net.rim.device.internal.resource.Firewall");

   static void checkSecurity(int handle, String permission) {
      if (Branding.getVendorId() == 102) {
      }

      if (GPS.getMode() != 2) {
         throw new SecurityException("Permission denied");
      }

      if (!ControlledAccess.verifyCodeModuleSignature(handle, 51) && !GPSFirewall.getInstance().allowAccess(permission)) {
         throw new SecurityException("Permission denied");
      }

      if (ITPolicy.getBoolean(24, 52, false)) {
         throw new ControlledAccessException(_rb.getString(11));
      }
   }

   private static boolean isAuthorizedRIMEntryPoint(int handle) {
      if (ControlledAccess.verifyRRISignatures(true)) {
         String moduleName = CodeModuleManager.getModuleName(handle).toLowerCase();
         if (moduleName == null) {
            return false;
         }

         if (moduleName.startsWith("net_rim_bb_browser_rendering_lib")
            || moduleName.startsWith("net_rim_bb_elt")
            || moduleName.startsWith("net_rim_bb_options_app")
            || moduleName.startsWith("net_rim_locationapi")) {
            return true;
         }
      }

      return false;
   }

   static String getDataSource() {
      _deviceGPS = false;
      _gpsDataSourceStore = PersistentStore.getPersistentObject(GPS.GPS_DATA_SOURCE_KEY);
      String gpsSource;
      synchronized (_gpsDataSourceStore) {
         gpsSource = (String)_gpsDataSourceStore.getContents();
      }

      if (gpsSource != null && !gpsSource.equals(GPS.GPS_SOURCE_DEVICE) || !GPS.isSupportedOnCurrentNetwork() && !DeviceInfo.isSimulator()) {
         Vector bluetoothDevices = new Vector();
         if (BluetoothSerialPort.isSupported()) {
            BluetoothSerialPortInfo[] portInfo = BluetoothSerialPort.getSerialPortInfo();
            if (portInfo != null) {
               for (int port = 0; port < portInfo.length; port++) {
                  String deviceName = portInfo[port].getDeviceName();
                  if (deviceName != null && !deviceName.startsWith("Smart Card Reader") && deviceName != null && !bluetoothDevices.contains(deviceName)) {
                     bluetoothDevices.addElement(deviceName);
                  }
               }
            }

            if (!bluetoothDevices.contains(gpsSource)) {
               if (GPS.isSupportedOnCurrentNetwork()) {
                  _deviceGPS = true;
                  return null;
               }

               if (bluetoothDevices.size() > 0) {
                  gpsSource = (String)bluetoothDevices.elementAt(0);
               }
            }
         }

         return gpsSource;
      } else {
         _deviceGPS = true;
         return null;
      }
   }

   protected LocationProvider() {
   }

   public int getState() {
      throw null;
   }

   public static LocationProvider getInstance(Criteria criteria) {
      int firstRequestMode = _defaultMode;
      int subsequentRequestMode = _defaultMode;
      getDataSource();
      if (DeviceInfo.isSimulator() && GPSRegistry.getInstance().getSimulateGPSPuck()) {
         _locationProvider = new DefaultLocationProvider(firstRequestMode, subsequentRequestMode);
         _locationProvider._isBTSimulation = true;
         return _locationProvider;
      }

      boolean bluetoothSupport = BluetoothSerialPort.isSupported();
      if (!GPS.isSupportedOnCurrentNetwork() && !bluetoothSupport) {
         return null;
      }

      if (_locationProvider != null) {
         _locationProvider.setLocationListener(null, 0, 0, 0);
         _locationProvider.reset();
      }

      if (!_deviceGPS) {
         if (bluetoothSupport) {
            _locationProvider = new BluetoothSerialPortGPSProvider();
            return _locationProvider;
         } else {
            return null;
         }
      } else {
         if (GPS.getMode() != 2) {
            return null;
         }

         if (criteria != null) {
            int horizontalAccuracy = criteria.getHorizontalAccuracy();
            int verticalAccuracy = criteria.getVerticalAccuracy();
            boolean allowedToCost = criteria.isAllowedToCost();
            int preferredPower = criteria.getPreferredPowerConsumption();
            if (criteria.isAddressInfoRequired()) {
               return null;
            }

            if (GPSRegistry.isVerizon() && (horizontalAccuracy != 0 || verticalAccuracy != 0) && allowedToCost && preferredPower != 0) {
               firstRequestMode = 1;
               subsequentRequestMode = 1;
            } else if ((horizontalAccuracy != 0 || verticalAccuracy != 0) && !allowedToCost) {
               if (!_autoModeSupported) {
                  return null;
               }

               firstRequestMode = 2;
               subsequentRequestMode = 2;
            } else if ((horizontalAccuracy != 0 || verticalAccuracy != 0) && allowedToCost && preferredPower != 3) {
               if (!_autoModeSupported) {
                  return null;
               }

               firstRequestMode = 2;
               subsequentRequestMode = 2;
            } else if ((horizontalAccuracy != 0 || verticalAccuracy != 0) && allowedToCost && preferredPower == 3) {
               if (!_autoModeSupported) {
                  return null;
               }

               firstRequestMode = 1;
               subsequentRequestMode = 2;
            } else if (horizontalAccuracy == 0 && verticalAccuracy == 0 && !allowedToCost && preferredPower != 1) {
               if (!_autoModeSupported) {
                  return null;
               }

               firstRequestMode = 2;
               subsequentRequestMode = 2;
            } else if (horizontalAccuracy != 0 || verticalAccuracy != 0 || !allowedToCost || preferredPower != 2 && preferredPower != 0) {
               if (horizontalAccuracy == 0 && verticalAccuracy == 0 && allowedToCost && preferredPower == 3) {
                  if (!_autoModeSupported) {
                     return null;
                  }

                  firstRequestMode = 1;
                  subsequentRequestMode = 2;
               } else if (horizontalAccuracy == 0 && verticalAccuracy == 0 && allowedToCost && preferredPower == 1) {
                  if (!_cellModeSupported) {
                     return null;
                  }

                  firstRequestMode = 0;
                  subsequentRequestMode = 0;
               } else {
                  firstRequestMode = _defaultMode;
                  subsequentRequestMode = _defaultMode;
               }
            } else {
               if (!_assistModeSupported && criteria.equals(new Criteria())) {
                  firstRequestMode = _defaultMode;
                  subsequentRequestMode = _defaultMode;
               }

               firstRequestMode = 1;
               subsequentRequestMode = 1;
            }
         }

         _locationProvider = new DefaultLocationProvider(firstRequestMode, subsequentRequestMode);
         _locationProvider._criteria = _locationProvider.getGPSCriteria(criteria);
         return _locationProvider;
      }
   }

   private GPS$AppCriteria getGPSCriteria(Criteria crit) {
      if (crit == null) {
         return null;
      }

      int power = 0;
      switch (crit.getPreferredPowerConsumption()) {
         case -1:
            break;
         case 0:
         default:
            power = 0;
            break;
         case 1:
            power = 1;
            break;
         case 2:
            power = 2;
            break;
         case 3:
            power = 3;
      }

      return new GPS$AppCriteria(crit.getPreferredResponseTime(), crit.getVerticalAccuracy(), crit.getHorizontalAccuracy(), crit.isAllowedToCost(), power);
   }

   public Location getLocation(int _1) {
      throw null;
   }

   public void setLocationListener(LocationListener _1, int _2, int _3, int _4) {
      throw null;
   }

   public void reset() {
      throw null;
   }

   public static Location getLastKnownLocation() {
      checkSecurity(TraceBack.getCallingModule(0), "lapi_location");
      return _lastLocation;
   }

   public static void addProximityListener(ProximityListener listener, Coordinates coordinates, float proximityRadius) {
      checkSecurity(TraceBack.getCallingModule(0), "lapi_proximitylistener");
      if (proximityRadius <= 0 || Float.isNaN(proximityRadius)) {
         throw new IllegalArgumentException("Illegal value of proximity radius");
      }

      if (listener == null) {
         throw new NullPointerException("ProximityListener is null");
      }

      if (coordinates == null) {
         throw new NullPointerException("Coordinates is null");
      }

      if (GPS.isSupportedOnCurrentNetwork() || BluetoothSerialPort.isSupported()) {
         synchronized (_proximityMap) {
            _proximityMap.add(listener, new LocationProvider$ProximityInfo(coordinates, proximityRadius, listener));
         }

         if (_deviceGPS) {
            int mode = 0;
            if (_locationProvider != null) {
               mode = _locationProvider._mode;
            } else {
               mode = _defaultMode;
            }

            if (mode != 0) {
               GPSRegistry.getInstance().startLocationUpdate(PROXIMITY_LISTENER_INTERVAL, mode, null);
            }
         } else {
            BluetoothGPSRegistry.getInstance().registerGPSConsumer();
         }

         if (_proximityThread == null) {
            _proximityThread = new LocationProvider$ProximityThread();
         }

         if (!_proximityListenerRunning) {
            _proximityThread.start();
         }

         _proximityListenerRunning = true;
      }
   }

   public static void removeProximityListener(ProximityListener listener) {
      _proximityMap.removeKey(listener);
      if (_proximityMap.isEmpty()) {
         _proximityListenerRunning = false;
         if (_proximityThread != null) {
            _proximityThread.stopThread();
            _proximityThread = null;
         }
      }
   }

   static boolean isCMDAAssist() {
      if (RadioInfo.getNetworkType() != 4) {
         return false;
      } else {
         return _locationProvider != null ? _locationProvider._mode == 1 : _defaultMode == 1;
      }
   }

   static boolean isSimulateBTPuck() {
      return _locationProvider != null ? _locationProvider._isBTSimulation : DeviceInfo.isSimulator() && GPSRegistry.getInstance().getSimulateGPSPuck();
   }

   static {
      INVALID_LOCATION.setValid(false);
      _lastLocation.setCoordinates(_lastCoordinates);
      getDataSource();
      if (GPS.isSupported()) {
         int aidCapability = GPS.getAidCapability();
         _autoModeSupported = (aidCapability & 4) != 0;
         _assistModeSupported = (aidCapability & 2) != 0;
         _cellModeSupported = (aidCapability & 1) != 0;
         if (_autoModeSupported) {
            _defaultMode = 2;
            return;
         }

         if (_assistModeSupported) {
            _defaultMode = 1;
            return;
         }

         if (_cellModeSupported) {
            _defaultMode = 0;
         }
      }
   }
}
