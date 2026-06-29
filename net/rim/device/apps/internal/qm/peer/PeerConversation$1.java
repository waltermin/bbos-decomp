package net.rim.device.apps.internal.qm.peer;

final class PeerConversation$1 implements Runnable {
   private final PeerConversation this$0;

   PeerConversation$1(PeerConversation _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      PeerApplication.getInstance();
      PeerApplication.endConversation(this.this$0);
   }
}
