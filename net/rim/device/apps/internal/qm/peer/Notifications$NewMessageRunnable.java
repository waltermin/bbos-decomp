package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.DeviceInfo;

final class Notifications$NewMessageRunnable implements Runnable {
   private PeerConversation _conversation;
   private boolean _scheduled;
   private final Notifications this$0;

   Notifications$NewMessageRunnable(Notifications _1) {
      this.this$0 = _1;
   }

   private final synchronized void invokeLater(PeerConversation conversation) {
      this._conversation = conversation;
      if (!this._scheduled) {
         PeerApplication.getInstance().postInvokeLaterInternal(this);
         this._scheduled = true;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final synchronized void run() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         NotificationsManager.triggerImmediateEvent(4051365837710720090L, this._conversation.hashCode(), this._conversation, null);
         if (DeviceInfo.isInHolster()) {
            NotificationsManager.negotiateDeferredEvent(
               4051365837710720090L,
               this._conversation.hashCode(),
               this._conversation,
               System.currentTimeMillis() + 40000,
               0,
               Notifications.access$500(this.this$0)
            );
            var3 = false;
         } else {
            var3 = false;
         }
      } finally {
         if (var3) {
            this._scheduled = false;
         }
      }

      this._scheduled = false;
   }

   static final void access$100(Notifications$NewMessageRunnable x0, PeerConversation x1) {
      x0.invokeLater(x1);
   }
}
