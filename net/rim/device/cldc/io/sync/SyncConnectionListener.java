package net.rim.device.cldc.io.sync;

public interface SyncConnectionListener {
   byte DATAGRAM_RECEIVED;
   byte DATAGRAM_SENT;
   byte DATAGRAM_DROPPED;
   byte CONNECTION_CLOSED;
   byte SERIAL_CONNECTION;
   byte OTA_CONNECTION;

   void onSyncConnectionEvent(int var1, Object var2, int var3);
}
