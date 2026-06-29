package net.rim.device.apps.internal.explorer.file.menu;

final class MoveMenuItem$3 implements Runnable {
   private final MoveMenuItem this$0;

   MoveMenuItem$3(MoveMenuItem _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0._progressDialog != null) {
         this.this$0._progressDialog.dismiss();
         this.this$0._progressDialog = null;
      }
   }
}
