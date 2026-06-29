package javax.bluetooth;

import javax.microedition.io.Connection;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.internal.bluetooth.BluetoothDeviceManagerImpl;
import net.rim.device.apps.internal.bluetooth.LocalServiceRecord;
import net.rim.device.cldc.io.btspp.BluetoothServerConnection;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;
import net.rim.device.internal.bluetooth.BluetoothME;

public class LocalDevice {
   private LocalDevice$BluetoothProperties _properties = new LocalDevice$BluetoothProperties();
   private BluetoothDeviceManagerImpl _btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
   private DeviceClass _deviceClass = new DeviceClass(5243404);
   private DiscoveryAgent _discoveryAgent = new DiscoveryAgent(this._btManager);
   private static final long GUID = -8665271906926764260L;

   private LocalDevice() {
   }

   public static LocalDevice getLocalDevice() throws BluetoothStateException {
      assertPermission();
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      LocalDevice localDevice = (LocalDevice)ar.getOrWaitFor(-8665271906926764260L);
      if (localDevice == null) {
         localDevice = new LocalDevice();
         ar.put(-8665271906926764260L, localDevice);
      }

      if (!localDevice._btManager.isRadioOnPromptIfOff(false)) {
         throw new BluetoothStateException("Unable to enable the Bluetooth radio");
      } else {
         return localDevice;
      }
   }

   public DiscoveryAgent getDiscoveryAgent() {
      return this._discoveryAgent;
   }

   public String getFriendlyName() {
      return this._btManager.getLocalName();
   }

   public DeviceClass getDeviceClass() {
      return this._deviceClass;
   }

   public boolean setDiscoverable(int mode) {
      switch (mode) {
         case 0:
         case 10390272:
         case 10390323:
            return false;
         default:
            if (mode >= 10390272 && mode <= 10390335) {
               return false;
            } else {
               throw new IllegalArgumentException();
            }
      }
   }

   public static String getProperty(String property) {
      try {
         return getLocalDevice()._properties.getProperty(property);
      } catch (BluetoothStateException ex) {
         return null;
      }
   }

   public int getDiscoverable() {
      return this._btManager.isDiscoverable() ? 10390323 : 0;
   }

   public String getBluetoothAddress() {
      byte[] addr = BluetoothME.getLocalDeviceAddress();
      return addr != null ? BluetoothME.deviceAddressToString(addr) : null;
   }

   public ServiceRecord getRecord(Connection notifier) {
      if (notifier == null) {
         throw new NullPointerException();
      }

      if (!(notifier instanceof BluetoothServerConnection)) {
         throw new IllegalArgumentException();
      }

      BluetoothServerConnection conn = (BluetoothServerConnection)notifier;
      return conn.getServiceRecord();
   }

   public void updateRecord(ServiceRecord srvRecord) {
      if (srvRecord == null) {
         throw new NullPointerException();
      }

      if (!(srvRecord instanceof LocalServiceRecord)) {
         throw new IllegalArgumentException();
      }

      ((LocalServiceRecord)srvRecord).updateRecord();
   }

   public static boolean isPowerOn() {
      assertPermission();
      return BluetoothME.isPowerOn();
   }

   private static void assertPermission() {
      ApplicationControl.assertBluetoothSerialProfileAllowed(true);
   }
}
