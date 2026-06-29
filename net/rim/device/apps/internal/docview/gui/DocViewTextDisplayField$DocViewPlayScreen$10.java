package net.rim.device.apps.internal.docview.gui;

class DocViewTextDisplayField$DocViewPlayScreen$10 implements Runnable {
   private final boolean val$forceDownload;
   private final DocViewTextDisplayField$DocViewPlayScreen this$1;

   DocViewTextDisplayField$DocViewPlayScreen$10(DocViewTextDisplayField$DocViewPlayScreen _1, boolean _2) {
      this.this$1 = _1;
      this.val$forceDownload = _2;
   }

   @Override
   public void run() {
      this.this$1.this$0.retrieveSlide(this.this$1._renderedDomIDs[this.this$1._currentIndex], true, this.val$forceDownload);
   }
}
