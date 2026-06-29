package net.rim.device.apps.internal.qm.peer;

final class PeerApplication$1 implements Runnable {
   private final PeerApplication this$0;

   PeerApplication$1(PeerApplication _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (PeerApplication.access$000(this.this$0) != null) {
         PeerApplication.access$000(this.this$0).updateUserState();
      }
   }
}
