package net.rim.wica.runtime.messaging.internal.outbound;

final class OutboundProcessor$ResponseProcessor extends OutboundProcessor$InternalProcessor {
   private final OutboundProcessor this$0;

   private OutboundProcessor$ResponseProcessor(OutboundProcessor this$0) {
      super(this$0, null);
      this.this$0 = this$0;
   }

   @Override
   protected final void scheduleTask(Object x) {
      if (((OutboundQueue)x).schedule(1)) {
         super.scheduleTask(x);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      while (true) {
         boolean var4 = false /* VF: Semaphore variable */;

         try {
            var4 = true;
            if (super._scheduler.isRunning()) {
               OutboundQueue q;
               if ((q = (OutboundQueue)super._tasks.removeLastElement()) != null) {
                  q.unschedule(1);
                  this.this$0.processResponses(q);
                  if (q.shouldHeartbeat() && q.isEmpty()) {
                     this.this$0._hbProcessor.scheduleTask(q);
                  }
                  continue;
               }

               var4 = false;
            } else {
               var4 = false;
            }
         } finally {
            if (var4) {
               this.reschedule();
            }
         }

         this.reschedule();
         return;
      }
   }

   OutboundProcessor$ResponseProcessor(OutboundProcessor x0, OutboundProcessor$1 x1) {
      this(x0);
   }
}
