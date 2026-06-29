package net.rim.device.apps.internal.docview.gui;

class DocViewDisplayScreen$2 implements Runnable {
   private final DocViewDisplayScreen this$0;

   DocViewDisplayScreen$2(DocViewDisplayScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.displayRetrievedPercentage();
   }
}
