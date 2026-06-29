package net.rim.device.apps.internal.phone.options;

class ChangePasswordVerb$1 implements Runnable {
   private final ChangePasswordVerb this$0;

   ChangePasswordVerb$1(ChangePasswordVerb _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.stopListening();
   }
}
