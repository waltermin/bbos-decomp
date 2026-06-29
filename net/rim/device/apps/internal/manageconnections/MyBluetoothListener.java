package net.rim.device.apps.internal.manageconnections;

import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.bluetooth.BluetoothMEListener2;

final class MyBluetoothListener implements BluetoothMEListener2 {
   private boolean _bluetoothOn;
   ConnectionsPopupScreen _screen;

   MyBluetoothListener(ConnectionsPopupScreen screen) {
      this._screen = screen;
      this._bluetoothOn = BluetoothME.isPowerOn();
   }

   @Override
   public final void powerOnComplete(boolean success) {
      if (success && !this._bluetoothOn) {
         this._bluetoothOn = true;
         this._screen.updateLater();
      }
   }

   @Override
   public final void powerOffComplete() {
      if (this._bluetoothOn) {
         this._bluetoothOn = false;
         this._screen.updateLater();
      }
   }

   @Override
   public final void deviceConnected(byte[] address, int deviceClass, int result) {
      this._screen.updateLater();
   }

   @Override
   public final void deviceDisconnected(byte[] address, int reason) {
      this._screen.updateLater();
   }

   @Override
   public final void inquiryComplete() {
   }

   @Override
   public final void inquiryCancelled() {
   }

   @Override
   public final void inquiryResult(byte[] address, int rssi, int deviceClass, int pageScanInfo) {
   }

   @Override
   public final void pairingComplete(byte[] address, int result) {
   }

   @Override
   public final void deviceNameRetrieved(byte[] address, byte[] name) {
   }

   @Override
   public final void pinCodeRequired(byte[] address, int deviceClass) {
   }

   @Override
   public final void authorizationRequired(byte[] address, int securityRecordID) {
   }

   @Override
   public final void serviceDiscoveryComplete(byte[] address, int result, byte[] data) {
   }

   @Override
   public final void linkModeChanged(byte[] address, int result, int mode) {
   }

   @Override
   public final void connectionAccepted(byte[] address) {
   }

   @Override
   public final void linkKeyChangeComplete(byte[] address, int result) {
   }

   @Override
   public final void authenticationComplete(byte[] address, int result) {
   }

   @Override
   public final void encryptionComplete(byte[] address, int result) {
   }

   @Override
   public final void hciFatalError(byte[] address, int result) {
   }
}
