package net.rim.device.apps.internal.phone.options;

class CallForwardingOption$3 implements Runnable {
   private final CallForwardingOption this$0;

   CallForwardingOption$3(CallForwardingOption _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.open();
   }
}
