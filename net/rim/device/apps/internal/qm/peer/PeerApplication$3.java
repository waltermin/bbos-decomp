package net.rim.device.apps.internal.qm.peer;

final class PeerApplication$3 implements Runnable {
   private final PeerApplication this$0;

   PeerApplication$3(PeerApplication _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (PeerApplication.access$000(this.this$0) != null && !PeerApplication.access$000(this.this$0).isDisplayed()) {
         this.this$0.pushScreen(PeerApplication.access$000(this.this$0));
      }
   }
}
