package net.rim.device.apps.internal.docview.gui;

class DocViewTextDisplayField$18 implements Runnable {
   private final DocViewTextDisplayField this$0;

   DocViewTextDisplayField$18(DocViewTextDisplayField _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.refreshCrtPosition(true);
   }
}
