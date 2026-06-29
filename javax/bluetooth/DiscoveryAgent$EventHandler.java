package javax.bluetooth;

import net.rim.device.apps.internal.bluetooth.BluetoothDeviceManagerListener;

class DiscoveryAgent$EventHandler implements BluetoothDeviceManagerListener {
   DiscoveryListener _listener;
   private final DiscoveryAgent this$0;

   DiscoveryAgent$EventHandler(DiscoveryAgent _1, DiscoveryListener listener) {
      this.this$0 = _1;
      this._listener = listener;
      _1._btManager.addListener(this);
   }

   void destroy() {
      this.this$0._btManager.removeListener(this);
   }

   @Override
   public void inquiryComplete() {
   }

   @Override
   public void inquiryCancelled() {
   }

   @Override
   public void deviceAdded() {
   }

   @Override
   public void deviceRemoved() {
   }

   @Override
   public void deviceListUpdated() {
   }

   @Override
   public void deviceInfoUpdated() {
   }

   @Override
   public void stateChanged() {
   }

   @Override
   public void inquiryResult(RemoteDevice remoteDevice, DeviceClass deviceClass) {
   }

   @Override
   public void servicesDiscovered(int transactionID, ServiceRecord[] services) {
   }

   @Override
   public void serviceSearchCompleted(int transactionID, int result) {
   }

   @Override
   public void pairingInProgress() {
   }
}
