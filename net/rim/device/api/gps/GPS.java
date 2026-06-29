package net.rim.device.api.gps;

import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.bluetooth.BluetoothSerialPortInfo;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Message;

public final class GPS {
   public static final int GPS_MODE_EMERGENCY_ONLY = 1;
   public static final int GPS_MODE_LOCATION_ON = 2;
   public static final int GPS_LOCATION_TYPE_STANDARD = 0;
   public static final int GPS_LOCATION_TYPE_DETAILED = 1;
   public static final int GPS_LOCATION_TYPE_EXTENDED = 3;
   public static final int GPS_AID_MODE_CELLSITE = 0;
   public static final int GPS_AID_MODE_ASSIST = 1;
   public static final int GPS_AID_MODE_AUTONOMOUS = 2;
   public static final int GPS_ERROR_NONE = 0;
   public static final int GPS_ERROR_NO_FIX_IN_ALLOTTED_TIME = 1;
   public static final int GPS_ERROR_DEGRADED_FIX_IN_ALLOTTED_TIME = 2;
   public static final int GPS_ERROR_TIMEOUT_NO_FIX_NO_ASSIST_DATA = 3;
   public static final int GPS_ERROR_TIMEOUT_DEGRADED_FIX_NO_ASSIST_DATA = 4;
   public static final int GPS_ERROR_LOW_BATTERY = 5;
   public static final int GPS_ERROR_CHIPSET_DEAD = 6;
   public static final int GPS_ERROR_INVALID_REQUEST = 7;
   public static final int GPS_ERROR_PRIVACY_ACCESS_DENIED = 8;
   public static final int GPS_ERROR_ALMANAC_OUTDATED = 9;
   public static final int LPS_RESULT_COMPLETE = 0;
   public static final int LPS_RESULT_FAILED = 1;
   public static final int LPS_STATUS_INVALID_PARAMETER = 0;
   public static final int LPS_STATUS_PENDING = 1;
   public static final int LPS_STATUS_OS_BUSY = 2;
   public static final int LPS_STATUS_SUCCESS = 3;
   public static final int LPS_ID_LOCATION_BY_PERMISSION = 0;
   public static final int LPS_ID_LOCATION_RESTRICTED = 1;
   public static final int LPS_ID_LOCATION_UNRESTRICTED = 2;
   public static final int GPS_AID_MODE_CELLSITE_FLAG = 1;
   public static final int GPS_AID_MODE_ASSIST_FLAG = 2;
   public static final int GPS_AID_MODE_AUTONOMOUS_FLAG = 4;
   public static final int GPS_EPHEMERIS_FORMAT_SIRF_UNCOMPRESSED = 0;
   public static final int GPS_EPHEMERIS_STATUS_VALID = 0;
   public static final int GPS_EPHEMERIS_STATUS_UNAVAILABLE = 1;
   public static final int GPS_EPHEMERIS_STATUS_UNSUPPORTED_TYPE = 2;
   public static final int GPS_POWER_USAGE_NOT_REQ = 0;
   public static final int GPS_POWER_USAGE_LOW = 1;
   public static final int GPS_POWER_USAGE_MED = 2;
   public static final int GPS_POWER_USAGE_HIGH = 3;
   public static final int GPS_LOCATION_AIDING_STATUS_UNKNOWN = 0;
   public static final int GPS_LOCATION_AIDING_STATUS_VALID = 1;
   public static final int GPS_LOCATION_AIDING_STATUS_MOVED = 2;
   public static long GPS_DATA_SOURCE_KEY = 1025333612688110108L;
   public static String GPS_SOURCE_DEVICE = "Device GPS";
   public static long GPS_MODE_KEY = -1939038497867287894L;
   private static PersistentObject _gpsModeStore = PersistentStore.getPersistentObject(GPS_MODE_KEY);

   private GPS() {
   }

   public static final boolean isSupported() {
      return InternalServices.isDeviceCapable(6);
   }

   public static final boolean isSupportedOnCurrentNetwork() {
      return InternalServices.getHardwareID() == 67112452 && (RadioInfo.getActiveWAFs() & 1) == 1 ? false : isSupported();
   }

   public static final void setLAPIDataSource(String name) {
      PersistentObject gpsDataSourceStore = PersistentStore.getPersistentObject(GPS_DATA_SOURCE_KEY);
      synchronized (gpsDataSourceStore) {
         gpsDataSourceStore.setContents(name, 51);
         gpsDataSourceStore.commit();
      }
   }

   public static final String getLAPIDataSource() {
      String source = null;
      PersistentObject gpsDataSourceStore = PersistentStore.getPersistentObject(GPS_DATA_SOURCE_KEY);
      synchronized (gpsDataSourceStore) {
         source = (String)gpsDataSourceStore.getContents();
      }

      if (source == null) {
         return isSupportedOnCurrentNetwork() ? GPS_SOURCE_DEVICE : null;
      }

      if (source.equals(GPS_SOURCE_DEVICE)) {
         return source;
      }

      if (BluetoothSerialPort.isSupported()) {
         BluetoothSerialPortInfo[] portInfo = BluetoothSerialPort.getSerialPortInfo();
         if (portInfo != null) {
            for (int port = 0; port < portInfo.length; port++) {
               String deviceName = portInfo[port].getDeviceName();
               if (deviceName != null && deviceName.equals(source)) {
                  return source;
               }
            }
         }
      }

      return isSupportedOnCurrentNetwork() ? GPS_SOURCE_DEVICE : null;
   }

   public static final boolean isLAPIDataSourceInternalGPS() {
      String name = getLAPIDataSource();
      return name == null ? isSupported() : name.equals(GPS_SOURCE_DEVICE);
   }

   public static final boolean isLAPISupported() {
      if (isSupported()) {
         return true;
      }

      if (BluetoothME.isSupported()) {
         BluetoothSerialPortInfo[] btDevices = BluetoothSerialPort.getSerialPortInfo();
         if (btDevices != null && btDevices.length > 0) {
            return true;
         }
      }

      return false;
   }

   public static final native int getAidCapability();

   public static final native boolean nativeRequestModeChange(int var0);

   public static final boolean requestModeChange(int mode) {
      if (mode != 1 && mode != 2) {
         return false;
      }

      synchronized (_gpsModeStore) {
         _gpsModeStore.setContents(new Integer(mode), 51);
         _gpsModeStore.commit();
      }

      if (isSupported()) {
         nativeRequestModeChange(mode);
      }

      Message message = new Message(23, 5889, 1, mode, 0);
      ApplicationManagerInternal ami = (ApplicationManagerInternal)ApplicationManager.getApplicationManager();
      ami.postMessage(message);
      return true;
   }

   public static final native int nativeGetMode();

   public static final int getMode() {
      synchronized (_gpsModeStore) {
         Integer modeInt = (Integer)_gpsModeStore.getContents();
         if (modeInt != null) {
            return modeInt;
         } else {
            return isSupported() ? nativeGetMode() : 2;
         }
      }
   }

   public static final native int getMaxFixPeriod();

   public static final native boolean startLocationUpdate(int var0, int var1, int var2, GPS$AppCriteria var3);

   public static final boolean startUpdate(int aidMode, int frequency) {
      GPSRegistry gpsRegistry = GPSRegistry.getInstance();
      synchronized (gpsRegistry) {
         return gpsRegistry.startLocationUpdate(frequency, aidMode, null);
      }
   }

   public static final native boolean stopLocationUpdate(int var0);

   public static final boolean stopUpdate(int aidMode) {
      GPSRegistry gpsRegistry = GPSRegistry.getInstance();
      synchronized (gpsRegistry) {
         return gpsRegistry.stopLocationUpdate(aidMode);
      }
   }

   public static final native int getLocation(GPSLocation var0, int var1);

   public static final native boolean getRequestedGPSAssistData(GPSRequestedAssistData var0);

   public static final native int requestGetLPS();

   public static final native int requestSetLPS(int var0, byte[] var1);

   public static final native int requestEnablePIN(boolean var0, byte[] var1);

   public static final native int requestChangePIN(byte[] var0, byte[] var1);

   public static final void addListener(Application app, GPSListener listener) {
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(23) == null) {
            dispatchManager.setDispatcher(23, new GPSEventDispatcher());
         }
      }

      app.addListener(23, listener);
   }

   public static final void removeListener(Application app, GPSListener listener) {
      app.removeListener(23, listener);
   }

   public static final GPS$GPSPDEInfo getPDEInfo() {
      GPS$GPSPDEInfo info = new GPS$GPSPDEInfo();
      getPDEInfo(info);
      return info;
   }

   private static final native void getPDEInfo(GPS$GPSPDEInfo var0);

   public static final native boolean setPDEInfo(GPS$GPSPDEInfo var0);

   public static final native boolean startFledgeGPS();

   public static final native void stopFledgeGPS();

   public static final native void ephemerisDataAvailable(int var0);

   public static final native void provideEphemerisData(int var0, byte[] var1);

   public static final native void provideLocationAiding(GPSLocationAiding var0);
}
