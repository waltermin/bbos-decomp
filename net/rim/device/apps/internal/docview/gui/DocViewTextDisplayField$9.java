package net.rim.device.apps.internal.docview.gui;

class DocViewTextDisplayField$9 implements Runnable {
   private final DocViewTextDisplayField$TextControlInfo val$crtControl;
   private final int val$nFoundIndex;
   private final DocViewTextDisplayField this$0;

   DocViewTextDisplayField$9(DocViewTextDisplayField _1, DocViewTextDisplayField$TextControlInfo _2, int _3) {
      this.this$0 = _1;
      this.val$crtControl = _2;
      this.val$nFoundIndex = _3;
   }

   @Override
   public void run() {
      this.this$0.putCursorPosition(this.val$crtControl, this.val$nFoundIndex, true, true);
   }
}
