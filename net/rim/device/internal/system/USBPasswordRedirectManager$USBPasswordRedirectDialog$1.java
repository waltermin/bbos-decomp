package net.rim.device.internal.system;

class USBPasswordRedirectManager$USBPasswordRedirectDialog$1 implements Runnable {
   private final USBPasswordRedirectManager$USBPasswordRedirectDialog this$1;

   USBPasswordRedirectManager$USBPasswordRedirectDialog$1(USBPasswordRedirectManager$USBPasswordRedirectDialog _1) {
      this.this$1 = _1;
   }

   @Override
   public void run() {
      this.this$1.close(1);
   }
}
