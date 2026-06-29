package net.rim.device.cldc.io.sync;

public interface SyncConnectionListener {
   byte DATAGRAM_RECEIVED = 1;
   byte DATAGRAM_SENT = 2;
   byte DATAGRAM_DROPPED = 3;
   byte CONNECTION_CLOSED = 16;
   byte SERIAL_CONNECTION = 17;
   byte OTA_CONNECTION = 18;

   void onSyncConnectionEvent(int var1, Object var2, int var3);
}
