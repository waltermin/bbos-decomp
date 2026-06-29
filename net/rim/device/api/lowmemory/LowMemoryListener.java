package net.rim.device.api.lowmemory;

public interface LowMemoryListener {
   int LOW_PRIORITY;
   int MEDIUM_PRIORITY;
   int HIGH_PRIORITY;

   boolean freeStaleObject(int var1);
}
