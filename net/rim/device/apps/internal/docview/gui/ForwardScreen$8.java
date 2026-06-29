package net.rim.device.apps.internal.docview.gui;

class ForwardScreen$8 implements Runnable {
   private final ForwardScreen this$0;

   ForwardScreen$8(ForwardScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0._attachmentsTree.redrawTree();
   }
}
