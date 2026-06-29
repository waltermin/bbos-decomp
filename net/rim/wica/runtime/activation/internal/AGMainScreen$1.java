package net.rim.wica.runtime.activation.internal;

class AGMainScreen$1 implements Runnable {
   private final AGMainScreen this$0;

   AGMainScreen$1(AGMainScreen this$0) {
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      this.this$0.updateUI();
   }
}
