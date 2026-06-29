package net.rim.wica.runtime.messaging.internal.outbound;

import net.rim.wica.runtime.comm.OutgoingRequest;
import net.rim.wica.runtime.comm.Response;
import net.rim.wica.runtime.comm.ResponseListener;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;
import net.rim.wica.runtime.messaging.internal.util.SplitQueue$SplitIterator;

final class OutboundProcessor$HeartbeatProcessor extends OutboundProcessor$InternalProcessor implements ResponseListener {
   private final OutboundProcessor this$0;

   private OutboundProcessor$HeartbeatProcessor(OutboundProcessor this$0) {
      super(this$0, null);
      this.this$0 = this$0;
   }

   @Override
   protected final void scheduleTask(Object x) {
      OutboundQueue q = (OutboundQueue)x;
      q.shouldHeartbeat(true);
      if (q.schedule(3)) {
         super.scheduleTask(x);
      }
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
               OutboundQueue q;
               if ((q = (OutboundQueue)super._tasks.removeLastElement()) != null) {
                  q.unschedule(3);
                  if (!q.shouldHeartbeat()) {
                     continue;
                  }

                  SplitQueue$SplitIterator i = q.splitIterator(true);
                  if (!i.hasNext()) {
                     q.shouldHeartbeat(false);
                     this.this$0.sendHeartbeat(q);
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

   @Override
   public final void processResponse(Response response, OutgoingRequest request) {
      if (!response.isSuccessful()) {
         if (request.hasExpired()) {
            OutboundQueue q = (OutboundQueue)this.this$0._urlToQueueTable.get(request.getUrl());
            if (q != null) {
               this.this$0._hbProcessor.scheduleTask(q);
               return;
            }
         } else {
            InternalLogger.logWarning(
               this.this$0,
               "Heartbeat to MDS Services "
                  + request.getUrl()
                  + " failed with response code "
                  + response.getResponseCode()
                  + " after "
                  + request.getAttemptCount()
                  + " attempts",
               null,
               null
            );
         }
      }
   }

   OutboundProcessor$HeartbeatProcessor(OutboundProcessor x0, OutboundProcessor$1 x1) {
      this(x0);
   }
}
