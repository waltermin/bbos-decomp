package net.rim.wica.runtime.lifecycle.internal;

class AlertManager$1 implements Runnable {
   private final AlertManager this$0;

   AlertManager$1(AlertManager this$0) {
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      if (this.this$0._dialog != null) {
         this.this$0._dialog.setDialogClosedListener(null);
         this.this$0._dialog.close();
      }
   }
}
