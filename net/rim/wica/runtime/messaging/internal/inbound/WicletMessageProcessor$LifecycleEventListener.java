package net.rim.wica.runtime.messaging.internal.inbound;

import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.lifecycle.Wiclet;
import net.rim.wica.runtime.lifecycle.WicletUninstalledEvent;
import net.rim.wica.runtime.lifecycle.WicletUpgradeEvent;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;

final class WicletMessageProcessor$LifecycleEventListener implements EventListener {
   private final WicletMessageProcessor this$0;

   private WicletMessageProcessor$LifecycleEventListener(WicletMessageProcessor this$0) {
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
            this.this$0.installWiclet((Wiclet)data);
            return;
         case 201:
            WicletUpgradeEvent e = (WicletUpgradeEvent)data;
            if (e.isMsgCompatible()) {
               this.this$0.upgradeWiclet(e.getInstalledWicletId(), e.getWiclet());
               return;
            }

            this.this$0.uninstallWiclet(e.getInstalledWicletId());
            this.this$0.installWiclet(e.getWiclet());
            return;
         case 202:
            this.this$0.uninstallWiclet(((WicletUninstalledEvent)data).getWicletId());
      }
   }

   @Override
   public final String toString() {
      return ((StringBuffer)(new Object())).append(this.this$0.toString()).append("#LifecycleEventListener").toString();
   }

   WicletMessageProcessor$LifecycleEventListener(WicletMessageProcessor x0, WicletMessageProcessor$1 x1) {
      this(x0);
   }
}
