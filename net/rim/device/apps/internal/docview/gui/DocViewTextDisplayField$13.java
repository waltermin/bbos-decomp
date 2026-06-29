package net.rim.device.apps.internal.docview.gui;

class DocViewTextDisplayField$13 implements Runnable {
   private final DocViewTextDisplayField$PageBreakStatusField val$pgBrk;
   private final DocViewTextDisplayField this$0;

   DocViewTextDisplayField$13(DocViewTextDisplayField _1, DocViewTextDisplayField$PageBreakStatusField _2) {
      this.this$0 = _1;
      this.val$pgBrk = _2;
   }

   @Override
   public void run() {
      this.this$0.privateProcessPageDisplay(this.val$pgBrk, null, true);
   }
}
