package net.rim.device.api.lowmemory;

public interface LowMemoryListener {
   int LOW_PRIORITY = 0;
   int MEDIUM_PRIORITY = 1;
   int HIGH_PRIORITY = 2;

   boolean freeStaleObject(int var1);
}
