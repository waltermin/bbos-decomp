package net.rim.device.apps.internal.iota;

final class ProvisioningServiceAgent$1 implements Runnable {
   private final ProvisioningServiceAgent this$0;

   ProvisioningServiceAgent$1(ProvisioningServiceAgent _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._dialog.close();
   }
}
