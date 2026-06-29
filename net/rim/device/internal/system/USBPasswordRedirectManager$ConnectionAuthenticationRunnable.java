package net.rim.device.internal.system;

class USBPasswordRedirectManager$ConnectionAuthenticationRunnable implements Runnable {
   int _channel;
   private final USBPasswordRedirectManager this$0;

   USBPasswordRedirectManager$ConnectionAuthenticationRunnable(USBPasswordRedirectManager _1, int channel) {
      this.this$0 = _1;
      this._channel = channel;
   }

   @Override
   public void run() {
      this.this$0.connectionAuthenticationRequiredPrivate(this._channel);
   }
}
