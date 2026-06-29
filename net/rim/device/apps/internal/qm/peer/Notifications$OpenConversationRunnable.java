package net.rim.device.apps.internal.qm.peer;

final class Notifications$OpenConversationRunnable implements Runnable {
   private PeerConversation _conversation;
   private boolean _scheduled;

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
         PeerApplication.getInstance().openConversationTriggeredByUnHolster(this._conversation, null);
         var3 = false;
      } finally {
         if (var3) {
            this._scheduled = false;
         }
      }

      this._scheduled = false;
   }

   static final void access$600(Notifications$OpenConversationRunnable x0, PeerConversation x1) {
      x0.invokeLater(x1);
   }
}
