package net.rim.device.apps.internal.sms;

class SIMATDisplayTextScreen$PopScreenRunnable implements Runnable {
   private final SIMATDisplayTextScreen this$0;

   private SIMATDisplayTextScreen$PopScreenRunnable(SIMATDisplayTextScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.autoDismiss();
   }

   SIMATDisplayTextScreen$PopScreenRunnable(SIMATDisplayTextScreen x0, SIMATDisplayTextScreen$1 x1) {
      this(x0);
   }
}
