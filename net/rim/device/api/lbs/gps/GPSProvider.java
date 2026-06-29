package net.rim.device.api.lbs.gps;

import java.util.Vector;
import javax.bluetooth.UUID;
import net.rim.device.api.gps.GPS;
import net.rim.device.api.gps.GPSSettings;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.apps.internal.bluetooth.BluetoothDevice;
import net.rim.device.apps.internal.bluetooth.BluetoothDeviceManagerImpl;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.vm.Array;

public final class GPSProvider {
   private GPSLocationData _locationDataInternal;
   private GPSDevice _locationDevice;
   private Vector _listeners;
   private Vector _listenerLocationDatas;
   GPSDevice _internalGPS;
   private boolean _checkForInternalGPS;
   private boolean _useLAPI = false;
   private static final long GUID;
   private static GPSProvider _INSTANCE;
   private static ResourceBundle _lbsBundle;
   private static boolean CAN_USE_GPS = true;

   private GPSProvider() {
      this._locationDataInternal = new GPSLocationData();
      this._listeners = (Vector)(new Object());
      this._listenerLocationDatas = (Vector)(new Object());
      EventLogger.register(4560142210062134028L, "LBS-GPSProvider", 2);
      _lbsBundle = ResourceBundle.getBundle(5578399137938411462L, "net.rim.device.api.lbs.LBSapi");
      this.checkLAPI();
   }

   public static final void init() {
      if (Branding.getVendorId() == 105) {
         if ((RadioInfo.getActiveWAFs() & 2) == 2) {
            String user = "1";
            String pass = "1";
            String ip = "1.1.1.1";
            int port = 0;
            String str = ((StringBuffer)(new Object())).append(ip).append(";").append(user).append(";").append(pass).toString();
            GPSSettings.setPDEInfo(str, port);
            if (user.equals("1") && pass.equals("1") && ip.equals("1.1.1.1") && port == 0) {
               CAN_USE_GPS = false;
               return;
            }
         } else {
            CAN_USE_GPS = false;
         }
      }
   }

   public static final GPSProvider getInstance() {
      if (_INSTANCE == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         if (ar != null) {
            _INSTANCE = (GPSProvider)ar.get(-5807538809205914903L);
            if (_INSTANCE == null) {
               _INSTANCE = new GPSProvider();
               ar.put(-5807538809205914903L, _INSTANCE);
            }
         } else {
            _INSTANCE = new GPSProvider();
         }
      }

      return _INSTANCE;
   }

   public static final boolean isGPSSupportedOnNetwork() {
      return ITPolicy.getBoolean(24, 52, false) ? false : GPS.isSupportedOnCurrentNetwork() && CAN_USE_GPS;
   }

   public static final boolean isGPSSupported() {
      if (GPS.getMode() != 2) {
         return false;
      }

      GPSDevice device = getInstance().getDeviceInUse();
      return device != null && (CAN_USE_GPS && isGPSSupportedOnNetwork() || !device.isInternalGPS());
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean getInternalGPS() {
      if (this._internalGPS == null && this._checkForInternalGPS) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         this._internalGPS = (GPSDevice)ar.get(-5162649070632360034L);
         if (this._internalGPS == null) {
            int moduleHandle = CodeModuleManager.getModuleHandle("net_rim_bb_lbs_internal_gps");
            EventLogger.logEvent(4560142210062134028L, ((StringBuffer)(new Object("Internal GPS code module: "))).append(moduleHandle).toString().getBytes(), 0);
            if (moduleHandle > 0) {
               ApplicationDescriptor[] descriptors = CodeModuleManager.getApplicationDescriptors(moduleHandle);
               ApplicationDescriptor descriptor = null;

               for (int i = descriptors.length - 1; i > -1; i--) {
                  descriptor = descriptors[i];
                  String[] arguments = descriptor.getArgs();
                  if (arguments == null || arguments.length == 0) {
                     EventLogger.logEvent(4560142210062134028L, "Accquring Internal GPS module handle.".getBytes(), 0);
                     ApplicationManager applicationManager = ApplicationManager.getApplicationManager();
                     boolean var10 = false /* VF: Semaphore variable */;

                     try {
                        var10 = true;
                        applicationManager.runApplication(descriptor, true);
                        var10 = false;
                        break;
                     } finally {
                        if (var10) {
                           EventLogger.logEvent(4560142210062134028L, "Could not run Internal GPS for registration.".getBytes(), 2);
                           break;
                        }
                     }
                  }
               }
            }

            this._internalGPS = (GPSDevice)ar.get(-5162649070632360034L);
         }

         EventLogger.logEvent(4560142210062134028L, ((StringBuffer)(new Object("Registering GPS device: "))).append(this._internalGPS).toString().getBytes(), 0);
      }

      this._checkForInternalGPS = false;
      return this._internalGPS != null;
   }

   public final GPSDevice[] getLocationDevices(boolean addNoDevice) {
      int ix = 0;
      GPSDevice[] devices = new GPSDevice[0];
      this._locationDataInternal.reset();
      if (addNoDevice) {
         Array.resize(devices, 1);
         devices[ix++] = GPSDevice.NO_DEVICE;
      }

      if (this._useLAPI) {
         EventLogger.logEvent(-5807538809205914903L, "using LAPI".getBytes(), 0);
         LocationWorker.getInstance()._locationDataInternal = this._locationDataInternal;
         if (GPS.isSupported() && isGPSSupportedOnNetwork()) {
            Array.resize(devices, devices.length + 1);
            devices[ix] = new LocationDevice(true, null);
            devices[ix++]._locationDataInternal = this._locationDataInternal;
         }

         if (BluetoothME.isSupported()) {
            int reduceListCount = 0;
            int notAddedCount = 0;
            int listIx = devices.length;
            Object[] btDevices = null;
            BluetoothDeviceManagerImpl mgr = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
            Vector deviceList = mgr.getPairedDevices();
            btDevices = new Object[deviceList.size()];
            deviceList.copyInto(btDevices);
            if (btDevices != null && btDevices.length > 0) {
               Array.resize(devices, listIx + btDevices.length);

               for (int i = 0; i < btDevices.length; i++) {
                  if (((BluetoothDevice)btDevices[i]).getServiceRecord((UUID)(new Object(4353))) == null) {
                     notAddedCount++;
                  } else {
                     String deviceName = ((BluetoothDevice)btDevices[i]).getName();
                     if (deviceName != null && !deviceName.startsWith("Smart Card Reader")) {
                        devices[listIx] = new LocationDevice(false, btDevices[i]);
                        devices[listIx]._locationDataInternal = this._locationDataInternal;

                        for (int t = 0; t < listIx; t++) {
                           if (devices[t].equals(devices[listIx])) {
                              devices[listIx] = null;
                              listIx--;
                              reduceListCount++;
                              break;
                           }
                        }

                        listIx++;
                     }
                  }
               }

               if (reduceListCount > 0 || notAddedCount > 0) {
                  Array.resize(devices, devices.length - reduceListCount - notAddedCount);
               }
            }
         }
      }

      return devices;
   }

   public final void setInterval(int interval) {
      if (this._useLAPI) {
         LocationWorker.getInstance().setInterval(interval);
      }
   }

   public final GPSDevice getDeviceInUse() {
      return this._locationDevice;
   }

   public final void setDeviceToUse(GPSDevice device) {
      if (this._locationDevice != null && this._locationDevice.getDeviceState() != 0) {
         this.stopReportingInternal(this._locationDevice, true);
      }

      this.stopReportingInternal(device, false);
      if (this._useLAPI && device != null && device != this._locationDevice) {
         EventLogger.logEvent(4560142210062134028L, ((StringBuffer)(new Object("reset provider: "))).append(device).toString().getBytes(), 5);
         LocationWorker.getInstance().resetLocationProvider();
      }

      if (device != null) {
         device.setDeviceToUse();
      }

      this._locationDevice = device;
   }

   public final void setDeviceToUse(String name) {
      if (name != null) {
         GPSDevice[] devices = this.getLocationDevices(!GPS.isSupported());

         for (int i = 0; i < devices.length; i++) {
            GPSDevice device = devices[i];
            if (device.equals(name)) {
               this.setDeviceToUse(device);
            }
         }
      } else {
         this.setDeviceToUse(GPSDevice.NO_DEVICE);
      }
   }

   public final void checkLAPI() {
      this._useLAPI = GPS.isLAPISupported();
   }

   public final void clearDeviceUsed() {
      this.stopReportingInternal(null, false);
      this._locationDevice = null;
   }

   public final GPSLocationData getSingleFix(GPSDevice device) {
      return !device.equals(GPSDevice.NO_DEVICE) ? device.singleFix() : null;
   }

   public final boolean startReporting(GPSDevice device) {
      boolean reporting = false;
      if (!device.equals(GPSDevice.NO_DEVICE)) {
         if (this._locationDevice != null) {
            synchronized (this._locationDevice) {
               this._locationDevice.stopReporting();
               this._locationDevice = null;
            }
         }

         this._locationDataInternal.reset();
         this.updateLocationData(false);
         this._locationDevice = device;
         reporting = this._locationDevice.startReporting();
      }

      return reporting;
   }

   public final void stopReporting(GPSDevice device) {
      this.stopReportingInternal(device, true);
   }

   final void stopReportingInternal(GPSDevice device, boolean fireDeviceEvent) {
      if (device == null) {
         device = this._locationDevice;
      }

      if (device != null && !device.equals(GPSDevice.NO_DEVICE)) {
         device.stopReporting();
         device._status = 0;
      }
   }

   public final void addLocationListener(GPSProvider$Listener listener, GPSLocationData data) {
      synchronized (this._listeners) {
         if (!this._listeners.contains(listener)) {
            Class clazz = listener.getClass();

            for (int i = this._listeners.size() - 1; i >= 0; i--) {
               if (clazz.getName().equals(this._listeners.elementAt(i).getClass().getName())) {
                  EventLogger.logEvent(
                     4560142210062134028L,
                     ((StringBuffer)(new Object("Multiple instances of the same class are in the listener: "))).append(clazz.getName()).toString().getBytes(),
                     3
                  );
                  this._listeners.removeElementAt(i);
                  this._listenerLocationDatas.removeElementAt(i);
               }
            }

            EventLogger.logEvent(4560142210062134028L, ((StringBuffer)(new Object("adding listener: "))).append(listener).toString().getBytes(), 5);
            this._listeners.addElement(listener);
            this._listenerLocationDatas.addElement(data);
         }
      }
   }

   public final void removeLocationListener(GPSProvider$Listener listener) {
      synchronized (this._listeners) {
         EventLogger.logEvent(4560142210062134028L, ((StringBuffer)(new Object("removing listener: "))).append(listener).toString().getBytes(), 5);
         int ix = this._listeners.indexOf(listener);
         if (ix >= 0) {
            this._listeners.removeElementAt(ix);
            this._listenerLocationDatas.removeElementAt(ix);
         }
      }
   }

   final void fireLocationDeviceEvent(GPSDevice device, String message) {
      synchronized (this._listeners) {
         for (int i = 0; i < this._listeners.size(); i++) {
            ((GPSProvider$Listener)this._listeners.elementAt(i)).deviceStateChanged(device, message);
         }
      }
   }

   final void updateLocationData(boolean fireUpdateEvent) {
      synchronized (this._locationDataInternal) {
         for (int i = 0; i < this._listeners.size(); i++) {
            this._locationDataInternal.copyInto((GPSLocationData)this._listenerLocationDatas.elementAt(i));
            if (fireUpdateEvent) {
               ((GPSProvider$Listener)this._listeners.elementAt(i)).locationUpdated();
            }
         }
      }
   }

   static final String getString(int id) {
      if (_lbsBundle == null) {
         _lbsBundle = ResourceBundle.getBundle(5578399137938411462L, "net.rim.device.api.lbs.LBSapi");
      }

      return _lbsBundle.getString(id);
   }
}
