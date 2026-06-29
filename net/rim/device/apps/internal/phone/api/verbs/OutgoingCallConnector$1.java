package net.rim.device.apps.internal.phone.api.verbs;

class OutgoingCallConnector$1 implements Runnable {
   private final OutgoingCallConnector this$0;

   OutgoingCallConnector$1(OutgoingCallConnector _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.completeConnection();
   }
}
