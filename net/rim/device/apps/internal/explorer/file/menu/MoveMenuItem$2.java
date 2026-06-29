package net.rim.device.apps.internal.explorer.file.menu;

final class MoveMenuItem$2 implements Runnable {
   private final int val$percentage;
   private final MoveMenuItem this$0;

   MoveMenuItem$2(MoveMenuItem _1, int _2) {
      this.this$0 = _1;
      this.val$percentage = _2;
   }

   @Override
   public final void run() {
      if (this.this$0._progressDialog != null) {
         this.this$0._progressDialog.setProgress(this.val$percentage);
      }
   }
}
