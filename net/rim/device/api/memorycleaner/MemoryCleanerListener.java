package net.rim.device.api.memorycleaner;

public interface MemoryCleanerListener {
   int EVENT_IN_HOLSTER = 0;
   int EVENT_IDLE_TIMEOUT = 1;
   int EVENT_SYNC_START = 2;
   int EVENT_SYNC_STOPPED = 3;
   int EVENT_MEMORY_CLEANER = 4;
   int EVENT_TIME_CHANGED = 5;
   int EVENT_DEVICE_LOCK = 6;
   int EVENT_PROGRAMMATIC_CLEAN = 7;
   int EVENT_POWER_DOWN = 8;
   int EVENT_OTA_SYNC_TRANSACTION_STOPPED = 9;
   int EVENT_PERSISTENT_CONTENT_CLEAN = 10;
   int EVENT_IT_POLICY_CHANGED = 11;

   boolean cleanNow(int var1);

   String getDescription();
}
