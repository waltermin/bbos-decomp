package net.rim.device.apps.internal.docview.gui;

class DocViewSheetDisplayField$1 implements Runnable {
   private final DocViewSheetDisplayField this$0;

   DocViewSheetDisplayField$1(DocViewSheetDisplayField _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      try {
         this.this$0.getCurrentDisplayField(false).displayTitleString();
      } finally {
         return;
      }
   }
}
