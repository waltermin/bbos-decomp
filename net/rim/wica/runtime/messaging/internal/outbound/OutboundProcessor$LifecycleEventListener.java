package net.rim.wica.runtime.messaging.internal.outbound;

import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.lifecycle.Wiclet;
import net.rim.wica.runtime.lifecycle.WicletUninstalledEvent;
import net.rim.wica.runtime.lifecycle.WicletUpgradeEvent;
import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;

final class OutboundProcessor$LifecycleEventListener implements EventListener {
   private final OutboundProcessor this$0;

   private OutboundProcessor$LifecycleEventListener(OutboundProcessor this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void handleEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 199:
            InternalLogger.logUnkownEvent(this, event);
            return;
         case 200:
         default:
            if (data instanceof Wiclet) {
               this.this$0.installWiclet((Wiclet)data);
               return;
            }

            InternalLogger.logBadEvent(this, event);
            return;
         case 201:
            WicletUpgradeEvent e = (WicletUpgradeEvent)data;
            Wiclet w = e.getWiclet();
            OutboundQueueConnectionImpl wInfo = (OutboundQueueConnectionImpl)this.this$0._queueConnTable.remove(e.getInstalledWicletId());
            if (wInfo == null) {
               Logger.log(this.toString(), "Application to upgrade, ID = " + e.getInstalledWicletId() + ", not found.", 2, 201);
            }

            if (!e.isMsgCompatible() && wInfo != null) {
               wInfo.setUninstallMode(false);
               this.this$0._gcProcessor.scheduleTask(wInfo);
            }

            this.this$0.installWiclet(w);
            return;
         case 202:
            if (!(data instanceof WicletUninstalledEvent)) {
               InternalLogger.logBadEvent(this, event);
            } else {
               WicletUninstalledEvent e = (WicletUninstalledEvent)data;
               OutboundQueueConnectionImpl wInfo = (OutboundQueueConnectionImpl)this.this$0._queueConnTable.remove(e.getWicletId());
               if (wInfo == null) {
                  Logger.log(this.toString(), "Application to delete, ID = " + e.getWicletId() + ", not found.", 2, 202);
               } else {
                  wInfo.setUninstallMode(e.isGraceful());
                  this.this$0._gcProcessor.scheduleTask(wInfo);
               }
            }
      }
   }

   @Override
   public final String toString() {
      return this.this$0.toString() + "#LifecycleEventListener";
   }

   OutboundProcessor$LifecycleEventListener(OutboundProcessor x0, OutboundProcessor$1 x1) {
      this(x0);
   }
}
