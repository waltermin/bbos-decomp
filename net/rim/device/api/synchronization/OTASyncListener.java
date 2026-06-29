package net.rim.device.api.synchronization;

public interface OTASyncListener {
   int TYPE_SLOW_SYNC = 0;
   int TYPE_TRANSACTION = 1;

   void otaSyncOperationStarted(SyncCollection var1, int var2);

   void otaSyncOperationStopped(SyncCollection var1, int var2);
}
