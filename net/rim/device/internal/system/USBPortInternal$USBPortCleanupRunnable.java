package net.rim.device.internal.system;

final class USBPortInternal$USBPortCleanupRunnable implements Runnable {
   private final USBPortInternal this$0;

   USBPortInternal$USBPortCleanupRunnable(USBPortInternal _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      USBPortInternal.closeChannel(this.this$0._channel);
   }
}
