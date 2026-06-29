package net.rim.device.internal.gps;

import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.gps.GPS;
import net.rim.device.api.gps.GPSRegistry;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;

public final class Registration {
   public static final void GPSFirewallMain() {
      if (GPS.isSupported() || BluetoothSerialPort.isSupported() || DeviceInfo.isSimulator()) {
         GPSFirewallImpl firewallImpl = new GPSFirewallImpl();
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         ar.put(-3752949794647167067L, firewallImpl);
      }
   }

   public static final void GPSBluetoothSimulateMain() {
      GPSRegistry.initialize();
   }
}
