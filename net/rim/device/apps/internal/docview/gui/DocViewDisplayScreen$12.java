package net.rim.device.apps.internal.docview.gui;

class DocViewDisplayScreen$12 implements Runnable {
   private final DocViewDisplayScreen this$0;

   DocViewDisplayScreen$12(DocViewDisplayScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.displayRetrievedPercentage();
   }
}
