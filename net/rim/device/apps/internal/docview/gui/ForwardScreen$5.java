package net.rim.device.apps.internal.docview.gui;

class ForwardScreen$5 implements Runnable {
   private final int val$node;
   private final ForwardScreen this$0;

   ForwardScreen$5(ForwardScreen _1, int _2) {
      this.this$0 = _1;
      this.val$node = _2;
   }

   @Override
   public void run() {
      try {
         this.this$0._attachmentsTree.invalidateNode(this.val$node);
      } finally {
         return;
      }
   }
}
