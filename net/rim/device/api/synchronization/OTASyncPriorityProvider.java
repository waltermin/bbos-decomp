package net.rim.device.api.synchronization;

public interface OTASyncPriorityProvider {
   int MAX_PRIORITY;
   int MIN_PRIORITY;
   int DEFAULT_PRIORITY;

   int getSyncPriority();
}
