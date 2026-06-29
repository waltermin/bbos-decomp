package net.rim.wica.runtime.messaging.internal.outbound;

import net.rim.wica.runtime.messaging.internal.MessageImpl;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;

final class OutboundProcessor$MessageProcessor extends OutboundProcessor$InternalProcessor {
   private final OutboundProcessor this$0;

   private OutboundProcessor$MessageProcessor(OutboundProcessor this$0) {
      super(this$0, null);
      this.this$0 = this$0;
   }

   @Override
   protected final boolean shouldSchedule() {
      return !super._scheduled && super._scheduler.isRunning() && (!this.this$0._intermediaryQueue.isEmpty() || !super._tasks.isEmpty());
   }

   @Override
   protected final void scheduleTask(Object x) {
      if (((OutboundQueue)x).schedule(0)) {
         super.scheduleTask(x);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      OutboundQueue q = null;
      long agIdCache = 0;

      label72:
      while (true) {
         boolean var8 = false /* VF: Semaphore variable */;

         try {
            var8 = true;
            MessageImpl m;
            if (!super._scheduler.isRunning() || (m = (MessageImpl)this.this$0._intermediaryQueue.take()) == null) {
               while (true) {
                  if (super._scheduler.isRunning()) {
                     if ((q = (OutboundQueue)super._tasks.removeLastElement()) != null) {
                        OutboundQueue$State state = q.getState();
                        if (state.isEnabled()) {
                           this.this$0.processEnabledQueue(q);
                        } else if (state.isDisabled()) {
                           this.this$0.processDisabledQueue(q);
                        }

                        q.unschedule(0);
                        continue;
                     }

                     var8 = false;
                     break label72;
                  }

                  var8 = false;
                  break label72;
               }
            }

            if (m.getAGID() != agIdCache) {
               q = (OutboundQueue)this.this$0._queueTable.get(m.getAGID());
               if (q == null) {
                  InternalLogger.logWarning(
                     this.this$0, ((StringBuffer)(new Object("Queue for MDS Services "))).append(m.getAGID()).append(" not found.").toString(), null, m
                  );
                  continue;
               }

               agIdCache = m.getAGID();
               this.scheduleTask(q);
            }

            q.put(m);
         } finally {
            if (var8) {
               this.reschedule();
            }
         }
      }

      this.reschedule();
   }

   OutboundProcessor$MessageProcessor(OutboundProcessor x0, OutboundProcessor$1 x1) {
      this(x0);
   }
}
