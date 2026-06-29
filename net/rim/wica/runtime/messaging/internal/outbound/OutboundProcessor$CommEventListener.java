package net.rim.wica.runtime.messaging.internal.outbound;

import java.util.Enumeration;
import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;

final class OutboundProcessor$CommEventListener implements EventListener {
   private final OutboundProcessor this$0;

   private OutboundProcessor$CommEventListener(OutboundProcessor this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void handleEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 299:
            InternalLogger.logUnkownEvent(this, event);
            break;
         case 300:
            synchronized (this.this$0._queueTable) {
               Enumeration queues = this.this$0._queueTable.elements();

               while (queues.hasMoreElements()) {
                  this.this$0.changeStateOnDeviceDisabled((OutboundQueue)queues.nextElement());
               }

               return;
            }
         case 301:
            if (!(data instanceof Object)) {
               InternalLogger.logBadEvent(this, event);
               return;
            }

            OutboundQueue q = (OutboundQueue)this.this$0._urlToQueueTable.get(data.toString());
            if (q != null) {
               this.this$0.changeStateOnAgDisabled(q);
               return;
            }
            break;
         case 302:
         default:
            synchronized (this.this$0._queueTable) {
               Enumeration queues = this.this$0._queueTable.elements();

               while (queues.hasMoreElements()) {
                  OutboundQueue q = (OutboundQueue)queues.nextElement();
                  this.this$0._hbProcessor.scheduleTask(q);
                  this.this$0.changeStateOnDeviceEnabled(q);
               }

               return;
            }
         case 303:
            if (!(data instanceof Object)) {
               InternalLogger.logBadEvent(this, event);
               return;
            }

            OutboundQueue q = (OutboundQueue)this.this$0._urlToQueueTable.get(data.toString());
            if (q != null) {
               this.this$0.changeStateOnAgEnabled(q);
               return;
            }
      }
   }

   @Override
   public final String toString() {
      return ((StringBuffer)(new Object())).append(this.this$0.toString()).append("#CommunicationEventListener").toString();
   }

   OutboundProcessor$CommEventListener(OutboundProcessor x0, OutboundProcessor$1 x1) {
      this(x0);
   }
}
