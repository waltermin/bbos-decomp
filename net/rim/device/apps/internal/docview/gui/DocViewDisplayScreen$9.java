package net.rim.device.apps.internal.docview.gui;

class DocViewDisplayScreen$9 implements Runnable {
   private final DocViewDisplayScreen this$0;

   DocViewDisplayScreen$9(DocViewDisplayScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      try {
         this.this$0.getUiEngine().relayout();
      } finally {
         return;
      }
   }
}
