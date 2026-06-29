package net.rim.device.apps.internal.docview.gui;

class DocViewDisplayField$1 implements Runnable {
   private final DocViewDisplayField this$0;

   DocViewDisplayField$1(DocViewDisplayField _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      if (this.this$0.isMoreAvailable()) {
         this.this$0.executeMore(null, true, true);
      }
   }
}
