package net.rim.wica.runtime.messaging.internal.outbound;

import java.util.Enumeration;
import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.lifecycle.LifecycleService;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;

final class OutboundProcessor$ManagementEventListener implements EventListener {
   private final OutboundProcessor this$0;

   private OutboundProcessor$ManagementEventListener(OutboundProcessor this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void handleEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 100:
            if (eventParam == 2 || eventParam == 11) {
               this.this$0.unregisterDefaultAg();
               return;
            }
            break;
         case 104:
            if (data instanceof Object) {
               this.this$0._deviceId = data;
               return;
            }

            InternalLogger.logBadEvent(this, event);
            return;
         case 105:
            this.this$0.registerDefaultAg();
            return;
         case 107:
            LifecycleService lc = (LifecycleService)this.this$0
               ._provider
               .getService(
                  OutboundProcessor.class$net$rim$wica$runtime$lifecycle$LifecycleService == null
                     ? (
                        OutboundProcessor.class$net$rim$wica$runtime$lifecycle$LifecycleService = OutboundProcessor.class$(
                           "net.rim.wica.runtime.lifecycle.LifecycleService"
                        )
                     )
                     : OutboundProcessor.class$net$rim$wica$runtime$lifecycle$LifecycleService
               );
            synchronized (this.this$0._queueConnTable) {
               Enumeration e = this.this$0._queueConnTable.elements();

               while (e.hasMoreElements()) {
                  OutboundQueueConnectionImpl w = (OutboundQueueConnectionImpl)e.nextElement();
                  w.setQueueLimit(lc.getWiclet(w.getWicletId()).getOutboundQueueSizeLimit());
               }

               return;
            }
         default:
            InternalLogger.logUnkownEvent(this, event);
      }
   }

   @Override
   public final String toString() {
      return ((StringBuffer)(new Object())).append(this.this$0.toString()).append("#ManagementEventListener").toString();
   }

   OutboundProcessor$ManagementEventListener(OutboundProcessor x0, OutboundProcessor$1 x1) {
      this(x0);
   }
}
