package net.rim.device.apps.internal.docview.gui;

class ForwardScreen$4 implements Runnable {
   private final int val$node;
   private final ForwardScreen this$0;

   ForwardScreen$4(ForwardScreen _1, int _2) {
      this.this$0 = _1;
      this.val$node = _2;
   }

   @Override
   public void run() {
      if (this.this$0._pendingParts == 0 && this.this$0.isFwdScreenTop()) {
         this.this$0.displayElement(this.val$node, false);
      }
   }
}
