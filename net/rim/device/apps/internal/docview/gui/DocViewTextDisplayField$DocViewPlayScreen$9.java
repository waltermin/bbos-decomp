package net.rim.device.apps.internal.docview.gui;

class DocViewTextDisplayField$DocViewPlayScreen$9 implements Runnable {
   private final int val$currentIdx;
   private final DocViewTextDisplayField$DocViewPlayScreen this$1;

   DocViewTextDisplayField$DocViewPlayScreen$9(DocViewTextDisplayField$DocViewPlayScreen _1, int _2) {
      this.this$1 = _1;
      this.val$currentIdx = _2;
   }

   @Override
   public void run() {
      if (this.this$1._currentIndex == this.val$currentIdx && !this.this$1._gotCurrentSlide) {
         this.this$1.setDisplayField(false);
      }
   }
}
