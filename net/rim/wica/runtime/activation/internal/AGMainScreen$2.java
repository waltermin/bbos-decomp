package net.rim.wica.runtime.activation.internal;

class AGMainScreen$2 implements Runnable {
   private final String val$statusText;
   private final int val$progress;
   private final AGMainScreen this$0;

   AGMainScreen$2(AGMainScreen this$0, String val$statusText, int val$progress) {
      this.this$0 = this$0;
      this.val$statusText = val$statusText;
      this.val$progress = val$progress;
   }

   @Override
   public void run() {
      this.this$0.updateProgress(this.val$statusText, this.val$progress);
   }
}
