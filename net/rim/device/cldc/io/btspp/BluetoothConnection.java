package net.rim.device.cldc.io.btspp;

public interface BluetoothConnection {
   byte[] getRemoteAddress();

   boolean isConnected();
}
