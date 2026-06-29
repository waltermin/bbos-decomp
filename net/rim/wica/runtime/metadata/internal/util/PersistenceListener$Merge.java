package net.rim.wica.runtime.metadata.internal.util;

import net.rim.device.api.system.PersistentContent;

final class PersistenceListener$Merge implements Runnable {
   private Object _ticket;
   private final PersistenceListener this$0;

   private PersistenceListener$Merge(PersistenceListener this$0) {
      this.this$0 = this$0;
      this._ticket = PersistentContent.getTicket();
      this._ticket = this._ticket;
   }

   @Override
   public final void run() {
      if (!this.this$0.shouldMerge()) {
         this._ticket = null;
      } else {
         this.this$0._persistenceReadable = true;
         this.this$0.merge();
         this._ticket = null;
      }
   }

   PersistenceListener$Merge(PersistenceListener x0, PersistenceListener$1 x1) {
      this(x0);
   }
}
