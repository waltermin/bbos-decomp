package net.rim.device.apps.internal.docview.gui;

class DocViewTextDisplayField$10 implements Runnable {
   private final DocViewTextDisplayField$TextControlInfo val$crtControlFinal;
   private final int val$textIndexFinal;
   private final DocViewTextDisplayField this$0;

   DocViewTextDisplayField$10(DocViewTextDisplayField _1, DocViewTextDisplayField$TextControlInfo _2, int _3) {
      this.this$0 = _1;
      this.val$crtControlFinal = _2;
      this.val$textIndexFinal = _3;
   }

   @Override
   public void run() {
      this.this$0.putCursorPosition(this.val$crtControlFinal, this.val$textIndexFinal, true, true);
   }
}
