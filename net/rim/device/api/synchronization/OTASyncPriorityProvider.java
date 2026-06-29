package net.rim.device.api.synchronization;

public interface OTASyncPriorityProvider {
   int MAX_PRIORITY = 1;
   int MIN_PRIORITY = 10;
   int DEFAULT_PRIORITY = 5;

   int getSyncPriority();
}
