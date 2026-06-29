package net.rim.device.internal.synchronization.ota.api;

public interface SyncAgentListener {
   int OTA_ENABLED = 1;
   int OTA_DISABLED = 2;
   int SERVICE_HAS_BEEN_UPDATED = 3;
   int DATABASE_OTA_ENABLED = 4;
   int DATABASE_OTA_DISABLED = 5;
   int DATABASE_STATISTICS_ADDED = 16;
   int DATABASE_STATISTICS_UPDATED = 17;
   int DATABASE_STATISTICS_REMOVED = 18;
   int START_INITIALIZING_DATABASE = 19;
   int END_INITIALIZING_DATABASE = 20;
   int START_TRANSACTION = 21;
   int END_TRANSACTION = 22;
   int USER_ENABLED = 23;
   int USER_DISABLED = 24;
   int CONFIG_REQUEST_SENT = 25;
   int CONFIG_REQUEST_RECEIVED = 26;
   int INITIALIZATION_ON_SERIAL_CONNECTION = 27;
   int OTA_SERVICE_INITIALIZED = 28;

   void onSyncAgentEvent(int var1, Object var2);
}
