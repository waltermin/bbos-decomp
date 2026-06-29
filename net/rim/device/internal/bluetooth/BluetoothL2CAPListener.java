package net.rim.device.internal.bluetooth;

public interface BluetoothL2CAPListener extends BluetoothListener {
   void l2capIncomingConnection(int var1, int var2, byte[] var3);

   void l2capConnected(int var1);

   void l2capDisconnected(int var1);

   void l2capDataReceived(int var1, byte[] var2);

   void l2capDataSent(int var1);
}
