package net.rim.device.internal.system;

class ForcedResetManager$ResetRunnable$DelayedResetRunnable$1 extends Thread {
   private final ForcedResetManager$ResetRunnable$DelayedResetRunnable this$1;

   ForcedResetManager$ResetRunnable$DelayedResetRunnable$1(ForcedResetManager$ResetRunnable$DelayedResetRunnable _1) {
      this.this$1 = _1;
   }

   @Override
   public void run() {
      System.out.println("Sleeping... " + this.this$1._message + '(' + this.this$1._variant + ')');

      try {
         Thread.sleep(2000);
      } catch (InterruptedException var2) {
      }

      System.out.println("Resetting!");
      InternalServices.initiateReset("FRM SCHb");
   }
}
