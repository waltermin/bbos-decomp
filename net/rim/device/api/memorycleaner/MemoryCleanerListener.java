package net.rim.device.api.memorycleaner;

public interface MemoryCleanerListener {
   int EVENT_IN_HOLSTER;
   int EVENT_IDLE_TIMEOUT;
   int EVENT_SYNC_START;
   int EVENT_SYNC_STOPPED;
   int EVENT_MEMORY_CLEANER;
   int EVENT_TIME_CHANGED;
   int EVENT_DEVICE_LOCK;
   int EVENT_PROGRAMMATIC_CLEAN;
   int EVENT_POWER_DOWN;
   int EVENT_OTA_SYNC_TRANSACTION_STOPPED;
   int EVENT_PERSISTENT_CONTENT_CLEAN;
   int EVENT_IT_POLICY_CHANGED;

   boolean cleanNow(int var1);

   String getDescription();
}
