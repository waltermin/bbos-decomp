package net.rim.device.apps.internal.docview.gui;

class DocViewDisplayScreen$7 implements Runnable {
   private final DocViewDisplayScreen this$0;

   DocViewDisplayScreen$7(DocViewDisplayScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.checkTitleField();
      this.this$0._displayField.checkArbItems();
   }
}
