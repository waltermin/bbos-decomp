package net.rim.device.apps.internal.docview.gui;

class DocViewSlideshowField$4 extends Thread {
   private final DocViewSlideshowField val$thisObject;
   private final DocViewSlideshowField this$0;

   DocViewSlideshowField$4(DocViewSlideshowField _1, DocViewSlideshowField _2) {
      this.this$0 = _1;
      this.val$thisObject = _2;
   }

   @Override
   public void run() {
      this.this$0._docData.resumeParsing();
      this.this$0._docData.getParsingData().waitForData();
      if (this.this$0._notifyObject != null) {
         this.this$0._notifyObject.moreDataParsed();
      }

      this.this$0.calculateImageCount();
      synchronized (this.val$thisObject) {
         this.this$0._duringPausedAutoMore = false;
      }
   }
}
