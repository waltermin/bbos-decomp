package net.rim.device.apps.internal.phone.api.verbs;

class DialVerb$2 implements Runnable {
   private final Object val$connectionParameters;
   private final DialVerb this$0;

   DialVerb$2(DialVerb _1, Object _2) {
      this.this$0 = _1;
      this.val$connectionParameters = _2;
   }

   @Override
   public void run() {
      OutgoingCallConnector.startCall(this.val$connectionParameters);
   }
}
