package net.rim.device.apps.internal.phone.options;

class EditForwardingNumbersScreen$1 implements Runnable {
   private final EditForwardingNumbersScreen this$0;

   EditForwardingNumbersScreen$1(EditForwardingNumbersScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.refreshForwardingNumberList();
   }
}
