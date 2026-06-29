package net.rim.device.internal.bluetooth;

import net.rim.device.api.bluetooth.BluetoothSerialPortInfo;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.InternalServices;

public class BluetoothDeviceManager extends Application {
   protected static final long GUID;

   public static BluetoothDeviceManager getInstance() {
      return (BluetoothDeviceManager)ApplicationRegistry.getApplicationRegistry().waitForStartup(-4148425341967320934L);
   }

   public void enableTCKTestMode(boolean _1) {
      throw null;
   }

   public BluetoothSerialPortInfo[] getSerialPortInfo() {
      throw null;
   }

   public static boolean isSPPEnabled() {
      return !ITPolicy.getBoolean(34, 5, false);
   }

   public static boolean isDesktopConnectivityEnabled() {
      if (!InternalServices.isDeviceSecure()) {
         return true;
      } else if (!isSPPEnabled()) {
         return false;
      } else {
         return !ITPolicyInternal.isITPolicyEnabled() ? true : !ITPolicy.getBoolean(34, 9, true);
      }
   }

   public static boolean isDUNEnabled() {
      return !ITPolicy.getBoolean(34, 16, false);
   }

   public boolean isRadioOnPromptIfOff(boolean _1) {
      throw null;
   }

   public boolean setPowerOn(boolean _1) {
      throw null;
   }

   public static boolean isWirelessBypassEnabled() {
      if (!InternalServices.isDeviceSecure()) {
         return true;
      } else if (!isSPPEnabled()) {
         return false;
      } else {
         return !ITPolicyInternal.isITPolicyEnabled() ? true : !ITPolicy.getBoolean(34, 10, true);
      }
   }
}
