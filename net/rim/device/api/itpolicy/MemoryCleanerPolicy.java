package net.rim.device.api.itpolicy;

public interface MemoryCleanerPolicy {
   int MEM_CLEANER_MAX_IDLE_TIME = 1;
   int FORCE_MEM_CLEAN_WHEN_IDLE = 2;
   int FORCE_MEM_CLEAN_WHEN_HOLSTERED = 3;
   int MEM_CLEANER_MAX_IDLE_TIME_DEFAULT = 60;
   boolean FORCE_MEM_CLEAN_WHEN_IDLE_DEFAULT = false;
   boolean FORCE_MEM_CLEAN_WHEN_HOLSTERED_DEFAULT = false;
}
