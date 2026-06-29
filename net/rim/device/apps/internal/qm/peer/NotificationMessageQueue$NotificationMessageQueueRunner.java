package net.rim.device.apps.internal.qm.peer;

import net.rim.device.apps.internal.qm.peer.common.NotificationMessage;
import net.rim.device.apps.internal.qm.peer.common.SystemNotificationMessage;

final class NotificationMessageQueue$NotificationMessageQueueRunner extends Thread {
   private final NotificationMessageQueue this$0;

   NotificationMessageQueue$NotificationMessageQueueRunner(NotificationMessageQueue _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      while (true) {
         label24:
         try {
            NotificationMessage msg = NotificationMessageQueue.access$000(this.this$0, true);
            if (msg != null) {
               this.fireQueueChanged(msg);
               if (msg instanceof SystemNotificationMessage) {
                  msg.setValid(false);
               }
            }
         } finally {
            break label24;
         }

         Object var5 = null;
      }
   }

   private final void fireQueueChanged(NotificationMessage msg) {
      PeerApplication app = PeerApplication.getInstance();
      if (app != null) {
         app.notificationQueueChanged(msg);
      }
   }
}
