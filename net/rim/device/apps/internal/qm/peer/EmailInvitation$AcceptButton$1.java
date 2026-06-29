package net.rim.device.apps.internal.qm.peer;

final class EmailInvitation$AcceptButton$1 extends Thread {
   private final EmailInvitation$AcceptButton this$1;

   EmailInvitation$AcceptButton$1(EmailInvitation$AcceptButton _1) {
      this.this$1 = _1;
   }

   @Override
   public final void run() {
      EmailInvitation$AcceptButton.access$000(this.this$1).acceptInvitation();
   }
}
