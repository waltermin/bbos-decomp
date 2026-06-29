package net.rim.device.apps.internal.docview.gui;

class DocViewTextDisplayField$19 implements Runnable {
   private final DocViewTextDisplayField this$0;

   DocViewTextDisplayField$19(DocViewTextDisplayField _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.refreshCrtPosition(false);
   }
}
