package net.rim.device.api.synchronization;

public interface OTASyncListener {
   int TYPE_SLOW_SYNC;
   int TYPE_TRANSACTION;

   void otaSyncOperationStarted(SyncCollection var1, int var2);

   void otaSyncOperationStopped(SyncCollection var1, int var2);
}
