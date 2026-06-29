package net.rim.device.api.memorycleaner;

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.internal.proxy.Proxy;

class MemoryCleanerManager$RemoveListeners implements Runnable {
   private final MemoryCleanerManager this$0;

   private MemoryCleanerManager$RemoveListeners(MemoryCleanerManager _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Proxy proxy = Proxy.getInstance();
      proxy.removeHolsterListener(this.this$0);
      proxy.removeRealtimeClockListener(this.this$0);
      proxy.removeGlobalEventListener(this.this$0);
      proxy.removeSystemListener(this.this$0);
      RIMGlobalMessagePoster.postGlobalEvent(5924166216341050021L);
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.removeSyncEventListener(this.this$0);
      }
   }

   MemoryCleanerManager$RemoveListeners(MemoryCleanerManager x0, MemoryCleanerManager$1 x1) {
      this(x0);
   }
}
