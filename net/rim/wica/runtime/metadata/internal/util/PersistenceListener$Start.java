package net.rim.wica.runtime.metadata.internal.util;

import net.rim.device.api.system.PersistentContent;

final class PersistenceListener$Start implements Runnable {
   private Object _ticket;
   private final PersistenceListener this$0;

   private PersistenceListener$Start(PersistenceListener this$0) {
      this.this$0 = this$0;
      this._ticket = PersistentContent.getTicket();
      this$0._globalTicket = this._ticket;
   }

   @Override
   public final void run() {
      this._ticket = null;
      if (!this.this$0.shouldStart()) {
         this.this$0._globalTicket = null;
      } else if (!this.this$0.waitForDeactivate()) {
         this.this$0._globalTicket = null;
         this.this$0._persistenceReadable = false;
         this.this$0.start();
      }
   }

   PersistenceListener$Start(PersistenceListener x0, PersistenceListener$1 x1) {
      this(x0);
   }
}
