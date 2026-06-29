package net.rim.device.apps.internal.docview.gui;

class DocViewTextDisplayField$DocViewPlayScreen$11 extends Thread {
   private final DocViewTextDisplayField$DocViewPlayScreen this$1;

   DocViewTextDisplayField$DocViewPlayScreen$11(DocViewTextDisplayField$DocViewPlayScreen _1) {
      this.this$1 = _1;
   }

   @Override
   public void run() {
      this.this$1.checkSlidesImpl();
   }
}
