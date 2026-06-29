package net.rim.device.internal.system;

class USBPasswordRedirectManager$MessageStatusDialog$1 implements Runnable {
   private final USBPasswordRedirectManager$MessageStatusDialog this$0;

   USBPasswordRedirectManager$MessageStatusDialog$1(USBPasswordRedirectManager$MessageStatusDialog _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.close(-1);
   }
}
