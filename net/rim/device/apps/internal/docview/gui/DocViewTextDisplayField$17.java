package net.rim.device.apps.internal.docview.gui;

class DocViewTextDisplayField$17 implements Runnable {
   private final String val$text;
   private final DocViewTextDisplayField this$0;

   DocViewTextDisplayField$17(DocViewTextDisplayField _1, String _2) {
      this.this$0 = _1;
      this.val$text = _2;
   }

   @Override
   public void run() {
      this.this$0._moreStatusField.setText(this.val$text);
   }
}
