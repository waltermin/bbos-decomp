package net.rim.device.internal.synchronization;

import net.rim.device.api.synchronization.SerialSyncListener;
import net.rim.device.api.synchronization.SerialSyncManager;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncManager;

final class SerialSyncManagerImpl extends SerialSyncManager {
   private SyncManagerImpl _syncManager = (SyncManagerImpl)SyncManager.getInstance();

   @Override
   public final void enableSynchronization(SyncCollection collection) {
      this._syncManager.enableSynchronization(collection);
   }

   @Override
   public final void disableSynchronization(SyncCollection collection) {
      this._syncManager.disableSynchronization(collection);
   }

   @Override
   public final void addSyncListener(SerialSyncListener listener) {
      this._syncManager.addSerialSyncListener(listener);
   }

   @Override
   public final void removeSyncListener(SerialSyncListener listener) {
      this._syncManager.removeSerialSyncListener(listener);
   }

   @Override
   public final boolean isSyncInProgress() {
      return this._syncManager.isSerialSyncInProgress();
   }

   @Override
   public final void setStatusMessage(String message) {
      this._syncManager.setSerialSyncStatusMessage(message);
   }
}
