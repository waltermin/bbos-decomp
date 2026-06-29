package net.rim.device.apps.api.ui;

class DialogWithBackgroundThread$1 implements Runnable {
   private final DialogWithBackgroundThread this$0;

   DialogWithBackgroundThread$1(DialogWithBackgroundThread _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Thread thread = (Thread)(new Object(this.this$0._dialogWithBackgroundThreadRunnable));
      thread.start();
   }
}
