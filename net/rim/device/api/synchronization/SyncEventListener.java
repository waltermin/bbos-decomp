package net.rim.device.api.synchronization;

public interface SyncEventListener {
   int SERIAL_SYNC_STARTED = 1;
   int SERIAL_SYNC_STOPPED = 2;
   int OTA_SYNC_TRANSACTION_STARTED = 3;
   int OTA_SYNC_TRANSACTION_STOPPED = 4;

   void syncEventOccurred(int var1, Object var2);
}
