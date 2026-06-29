package net.rim.device.internal.ui.component;

class TitledScrollingDialog$PopulateDialogThread extends Thread {
   private final TitledScrollingDialog this$0;

   TitledScrollingDialog$PopulateDialogThread(TitledScrollingDialog _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.setPriority(1);
      this.this$0.populateDialog();
   }
}
