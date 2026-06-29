package net.rim.device.apps.internal.docview.gui;

class ForwardScreen$6 implements Runnable {
   private final ForwardScreen this$0;

   ForwardScreen$6(ForwardScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0._appInstance.relayout();
   }
}
