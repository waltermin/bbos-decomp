package net.rim.device.apps.internal.docview.gui;

class ForwardScreen$MoreUpdateThread$1 implements Runnable {
   private final ForwardScreen$MoreUpdateThread this$1;

   ForwardScreen$MoreUpdateThread$1(ForwardScreen$MoreUpdateThread _1) {
      this.this$1 = _1;
   }

   @Override
   public void run() {
      this.this$1.this$0._attachmentsTree.redrawTree();
   }
}
