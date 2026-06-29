package net.rim.device.api.memorycleaner;

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.internal.proxy.Proxy;

class MemoryCleanerManager$AddListeners implements Runnable {
   private final MemoryCleanerManager this$0;

   private MemoryCleanerManager$AddListeners(MemoryCleanerManager _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Proxy proxy = Proxy.getInstance();
      proxy.addHolsterListener(this.this$0);
      proxy.addRealtimeClockListener(this.this$0);
      proxy.addGlobalEventListener(this.this$0);
      proxy.addSystemListener(this.this$0);
      RIMGlobalMessagePoster.postGlobalEvent(5924166216341050021L);
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.addSyncEventListener(this.this$0);
      }
   }

   MemoryCleanerManager$AddListeners(MemoryCleanerManager x0, MemoryCleanerManager$1 x1) {
      this(x0);
   }
}
