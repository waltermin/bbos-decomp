package net.rim.wica.runtime.core;

class RuntimeFramework$1 implements Runnable {
   private final RuntimeFramework this$0;

   RuntimeFramework$1(RuntimeFramework this$0) {
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      this.this$0._scheduledForStart = false;
      this.this$0.start();
   }
}
