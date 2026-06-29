package net.rim.device.apps.internal.docview.gui;

class DocViewTextDisplayField$11 implements Runnable {
   private final DocViewTextDisplayField$SkippedStatusField val$skippedField;
   private final DocViewTextDisplayField this$0;

   DocViewTextDisplayField$11(DocViewTextDisplayField _1, DocViewTextDisplayField$SkippedStatusField _2) {
      this.this$0 = _1;
      this.val$skippedField = _2;
   }

   @Override
   public void run() {
      this.val$skippedField.calculateDisplayText();
   }
}
