package net.rim.device.apps.internal.explorer.file.render;

final class RenderScreen$1 implements Runnable {
   private final RenderScreen$ProgressDialog val$progressDialog;
   private final RenderScreen this$0;

   RenderScreen$1(RenderScreen _1, RenderScreen$ProgressDialog _2) {
      this.this$0 = _1;
      this.val$progressDialog = _2;
   }

   @Override
   public final void run() {
      this.val$progressDialog.show();
   }
}
