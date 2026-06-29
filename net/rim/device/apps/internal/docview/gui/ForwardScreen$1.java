package net.rim.device.apps.internal.docview.gui;

class ForwardScreen$1 implements Runnable {
   private final ForwardScreen this$0;

   ForwardScreen$1(ForwardScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      DocViewAttachmentPersist.getInstance().checkPendingState();
   }
}
