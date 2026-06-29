package net.rim.wica.runtime.messaging.internal.inbound;

import java.util.Enumeration;
import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.lifecycle.Wiclet;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;
import net.rim.wica.runtime.provisioning.ProvisioningEvent;

final class WicletMessageProcessor$MiscellaneousEventListener implements EventListener {
   private final WicletMessageProcessor this$0;

   private WicletMessageProcessor$MiscellaneousEventListener(WicletMessageProcessor this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void handleEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 107:
            synchronized (this.this$0._wicletTable) {
               Enumeration e = this.this$0._wicletTable.elements();

               while (e.hasMoreElements()) {
                  WicletQueueManager wmxx = (WicletQueueManager)e.nextElement();
                  wmxx.handlePolicyUpdate();
               }

               return;
            }
         case 203:
            long wicletIdx = (Long)data;
            WicletQueueManager wmx = (WicletQueueManager)this.this$0._wicletTable.get(wicletIdx);
            if (wmx != null) {
               wmx.handleMetadataStartup();
               return;
            }
            break;
         case 204:
            long wicletId = (Long)data;
            WicletQueueManager wm = (WicletQueueManager)this.this$0._wicletTable.get(wicletId);
            if (wm != null) {
               wm.handleMetadataShutdown();
               return;
            }
            break;
         case 500:
            ProvisioningEvent pe = (ProvisioningEvent)data;
            if (pe.getType() == 4 && pe.getParam() == 3) {
               String uri = pe.getProvisioningTaskInfo().getApplicationUri();
               Wiclet w = this.this$0._lifecycle.getWiclet(uri);
               if (w != null) {
                  WicletQueueManager wmxx = (WicletQueueManager)this.this$0._wicletTable.get(w.getId());
                  wmxx.handleUpgradeStarted();
                  return;
               }
            }
            break;
         default:
            InternalLogger.logUnkownEvent(this, event);
      }
   }

   @Override
   public final String toString() {
      return this.this$0.toString() + "#EventListener";
   }

   WicletMessageProcessor$MiscellaneousEventListener(WicletMessageProcessor x0, WicletMessageProcessor$1 x1) {
      this(x0);
   }
}
