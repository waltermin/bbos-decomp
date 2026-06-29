package net.rim.device.api.bluetooth;

public interface BluetoothSerialPortListener {
   void deviceConnected(boolean var1);

   void deviceDisconnected();

   void dtrStateChange(boolean var1);

   void dataReceived(int var1);

   void dataSent();
}
