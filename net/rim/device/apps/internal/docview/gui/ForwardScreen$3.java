package net.rim.device.apps.internal.docview.gui;

class ForwardScreen$3 implements Runnable {
   private final ForwardScreen this$0;

   ForwardScreen$3(ForwardScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0._titleManager.delete(this.this$0._spacerField);
      this.this$0._titleManager.delete(this.this$0._iconField);
   }
}
