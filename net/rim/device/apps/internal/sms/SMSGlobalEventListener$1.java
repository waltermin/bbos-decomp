package net.rim.device.apps.internal.sms;

class SMSGlobalEventListener$1 implements Runnable {
   private final SMSGlobalEventListener this$0;

   SMSGlobalEventListener$1(SMSGlobalEventListener _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.updateEntry();
   }
}
