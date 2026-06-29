package net.rim.wica.runtime.activation.internal;

class AGMainScreen$5 implements Runnable {
   private final AGMainScreen this$0;

   AGMainScreen$5(AGMainScreen this$0) {
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      this.this$0.onClose();
   }
}
