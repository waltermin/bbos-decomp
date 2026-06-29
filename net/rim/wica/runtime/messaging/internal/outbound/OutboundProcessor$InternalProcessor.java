package net.rim.wica.runtime.messaging.internal.outbound;

import net.rim.wica.runtime.messaging.internal.Processor;
import net.rim.wica.runtime.messaging.internal.util.FastAccessVector;

class OutboundProcessor$InternalProcessor extends Processor {
   protected FastAccessVector _tasks;
   private final OutboundProcessor this$0;

   private OutboundProcessor$InternalProcessor(OutboundProcessor this$0) {
      super(this$0._scheduler);
      this.this$0 = this$0;
      this._tasks = new FastAccessVector();
   }

   @Override
   protected boolean shouldSchedule() {
      return !super._scheduled && super._scheduler.isRunning() && !this._tasks.isEmpty();
   }

   protected void scheduleTask(Object x) {
      this._tasks.addElement(x);
      this.schedule();
   }

   @Override
   public String toString() {
      return this.this$0.toString();
   }

   OutboundProcessor$InternalProcessor(OutboundProcessor x0, OutboundProcessor$1 x1) {
      this(x0);
   }
}
