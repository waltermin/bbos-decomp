package net.rim.device.apps.internal.docview.gui;

class DocViewTextDisplayField$14 implements Runnable {
   private final boolean val$bFullDocumentState;
   private final DocViewTextDisplayField this$0;

   DocViewTextDisplayField$14(DocViewTextDisplayField _1, boolean _2) {
      this.this$0 = _1;
      this.val$bFullDocumentState = _2;
   }

   @Override
   public void run() {
      this.this$0.refreshCrtPosition(this.val$bFullDocumentState);
   }
}
