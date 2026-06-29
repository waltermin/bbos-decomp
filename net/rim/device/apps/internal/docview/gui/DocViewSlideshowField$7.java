package net.rim.device.apps.internal.docview.gui;

class DocViewSlideshowField$7 implements Runnable {
   private final DocViewSlideshowField this$0;

   DocViewSlideshowField$7(DocViewSlideshowField _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.deleteAll();
      this.this$0.add(this.this$0._dummyFld);
   }
}
