package net.rim.device.apps.internal.bluetooth;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

public interface BluetoothDeviceManagerListener {
   void inquiryComplete();

   void inquiryCancelled();

   void deviceAdded();

   void deviceRemoved();

   void deviceListUpdated();

   void deviceInfoUpdated();

   void stateChanged();

   void inquiryResult(RemoteDevice var1, DeviceClass var2);

   void servicesDiscovered(int var1, ServiceRecord[] var2);

   void serviceSearchCompleted(int var1, int var2);

   void pairingInProgress();
}
