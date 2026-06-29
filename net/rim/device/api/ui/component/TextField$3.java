package net.rim.device.api.ui.component;

class TextField$3 implements Runnable {
   private final TextField this$0;

   TextField$3(TextField _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.incrementalFormat();
   }
}
