package net.rim.device.api.synchronization;

public interface SyncEventListener {
   int SERIAL_SYNC_STARTED;
   int SERIAL_SYNC_STOPPED;
   int OTA_SYNC_TRANSACTION_STARTED;
   int OTA_SYNC_TRANSACTION_STOPPED;

   void syncEventOccurred(int var1, Object var2);
}
