package net.rim.device.apps.internal.calendar.sync;

import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncManager;

class CalendarSyncCollection$SyncCollectionAction implements Runnable {
   SyncCollection _syncCollection;
   boolean _enable;

   CalendarSyncCollection$SyncCollectionAction(SyncCollection syncCollection, boolean enable) {
      this._syncCollection = syncCollection;
      this._enable = enable;
   }

   @Override
   public void run() {
      if (this._enable) {
         SyncManager.getInstance().enableSynchronization(this._syncCollection);
      } else {
         SyncManager.getInstance().disableSynchronization(this._syncCollection);
      }
   }
}
