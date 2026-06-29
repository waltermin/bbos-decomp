package net.rim.device.apps.internal.qm.peer;

final class PeerApplication$2 implements Runnable {
   private final PeerApplication this$0;

   PeerApplication$2(PeerApplication _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0.showWelcome()) {
         if (PeerApplication.access$000(this.this$0) != null) {
            PeerApplication.access$000(this.this$0).updateUserState();
         }

         PeerApplication.access$102(this.this$0, true);
      }
   }
}
