package net.rim.device.apps.internal.phone;

final class RIMPhone$3 implements Runnable {
   private final int val$callId;
   private final RIMPhone this$0;

   RIMPhone$3(RIMPhone _1, int _2) {
      this.this$0 = _1;
      this.val$callId = _2;
   }

   @Override
   public final void run() {
      this.this$0.handleMissedCall(this.val$callId, false);
   }
}
