package net.rim.device.apps.internal.iota;

final class ProvisioningServiceAgent$IOTAStatusRunnable implements Runnable {
   private String _msg;
   private final ProvisioningServiceAgent this$0;

   public ProvisioningServiceAgent$IOTAStatusRunnable(ProvisioningServiceAgent _1, String msg) {
      this.this$0 = _1;
      this._msg = msg;
   }

   @Override
   public final void run() {
      if (!this.this$0._dialog.isDisplayed()) {
         this.this$0._dialog.show();
      }

      this.this$0._dialog.setStatus(this._msg);
   }
}
