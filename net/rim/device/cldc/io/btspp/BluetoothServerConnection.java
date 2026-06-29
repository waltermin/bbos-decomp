package net.rim.device.cldc.io.btspp;

import net.rim.device.apps.internal.bluetooth.LocalServiceRecord;

public interface BluetoothServerConnection {
   LocalServiceRecord getServiceRecord();

   int getServiceRecordHandle();

   int getPSM();

   int getRFCOMMChannel();

   boolean isConnected();
}
