package net.rim.wica.runtime.persistence.internal;

import net.rim.device.api.system.PersistentContent;

final class PersistentContentHelperImpl$WaitForTicket implements Runnable {
   private final PersistentContentHelperImpl this$0;

   private PersistentContentHelperImpl$WaitForTicket(PersistentContentHelperImpl this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      PersistentContent.waitForTicket();
      synchronized (this.this$0) {
         this.this$0._waitingForTicket = false;
         this.this$0.persistentContentStateChanged(PersistentContent.getState());
      }
   }

   PersistentContentHelperImpl$WaitForTicket(PersistentContentHelperImpl x0, PersistentContentHelperImpl$1 x1) {
      this(x0);
   }
}
