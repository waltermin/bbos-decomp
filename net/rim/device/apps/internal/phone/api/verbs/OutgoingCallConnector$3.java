package net.rim.device.apps.internal.phone.api.verbs;

class OutgoingCallConnector$3 implements Runnable {
   private final String val$phoneNumber;
   private final int val$clirFlag;
   private final OutgoingCallConnector this$0;

   OutgoingCallConnector$3(OutgoingCallConnector _1, String _2, int _3) {
      this.this$0 = _1;
      this.val$phoneNumber = _2;
      this.val$clirFlag = _3;
   }

   @Override
   public void run() {
      this.this$0.doStartCall(this.val$phoneNumber, this.val$clirFlag);
   }
}
