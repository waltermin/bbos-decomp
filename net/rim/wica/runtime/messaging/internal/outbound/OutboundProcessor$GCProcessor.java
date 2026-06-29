package net.rim.wica.runtime.messaging.internal.outbound;

final class OutboundProcessor$GCProcessor extends OutboundProcessor$InternalProcessor {
   private final OutboundProcessor this$0;

   private OutboundProcessor$GCProcessor(OutboundProcessor this$0) {
      super(this$0, null);
      this.this$0 = this$0;
   }

   @Override
   protected final void scheduleTask(Object x) {
      super.scheduleTask(x);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      while (true) {
         boolean var5 = false /* VF: Semaphore variable */;

         try {
            var5 = true;
            if (super._scheduler.isRunning()) {
               OutboundQueueConnectionImpl w;
               if ((w = (OutboundQueueConnectionImpl)super._tasks.removeLastElement()) != null) {
                  OutboundQueue q = (OutboundQueue)this.this$0._queueTable.get(w.getAgId());
                  if (q != null && !w.gracefulUninstall()) {
                     this.this$0.filterByWicletId(q, w.getWicletId());
                     this.this$0.filterIntermediaryByWicletId(w.getWicletId());
                  }
                  continue;
               }

               var5 = false;
            } else {
               var5 = false;
            }
         } finally {
            if (var5) {
               this.reschedule();
            }
         }

         this.reschedule();
         return;
      }
   }

   OutboundProcessor$GCProcessor(OutboundProcessor x0, OutboundProcessor$1 x1) {
      this(x0);
   }
}
