package net.rim.device.apps.internal.docview.gui;

class DocViewTextDisplayField$8 implements Runnable {
   private final DocViewTextDisplayField$PageBreakStatusField val$pgBrk;
   private final DocViewTextDisplayField this$0;

   DocViewTextDisplayField$8(DocViewTextDisplayField _1, DocViewTextDisplayField$PageBreakStatusField _2) {
      this.this$0 = _1;
      this.val$pgBrk = _2;
   }

   @Override
   public void run() {
      if (this.val$pgBrk._isDummy
         && !this.this$0.isMoreRequestSent()
         && this.val$pgBrk == this.this$0.getNextPageStatusField(this.this$0.getFieldWithFocusIndex())) {
         this.this$0.privateProcessPageDisplay(this.val$pgBrk, null, false);
         if (this.val$pgBrk._isDummy) {
            this.this$0.retrieveSlide(this.val$pgBrk._domID, true, false);
         }
      }

      this.val$pgBrk._inProcess = false;
   }
}
