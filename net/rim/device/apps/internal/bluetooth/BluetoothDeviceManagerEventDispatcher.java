package net.rim.device.apps.internal.bluetooth;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class BluetoothDeviceManagerEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      int event = message.getEvent();
      BluetoothDeviceManagerListener dmListener = (BluetoothDeviceManagerListener)listener;
      switch (event) {
         case 0:
         default:
            dmListener.inquiryComplete();
            return;
         case 1:
            dmListener.inquiryCancelled();
            return;
         case 2:
            dmListener.deviceAdded();
            return;
         case 3:
            dmListener.deviceRemoved();
            return;
         case 4:
            dmListener.deviceListUpdated();
            return;
         case 5:
            dmListener.deviceInfoUpdated();
            return;
         case 6:
            dmListener.stateChanged();
            return;
         case 7:
            dmListener.inquiryResult((RemoteDevice)message.getObject0(), (DeviceClass)message.getObject1());
            return;
         case 8:
            dmListener.servicesDiscovered(message.getData0(), (ServiceRecord[])message.getObject0());
            return;
         case 9:
            dmListener.serviceSearchCompleted(message.getData0(), message.getData1());
            return;
         case 10:
            dmListener.pairingInProgress();
         case -1:
      }
   }
}
