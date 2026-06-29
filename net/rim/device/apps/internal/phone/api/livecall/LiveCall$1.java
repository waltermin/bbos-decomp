package net.rim.device.apps.internal.phone.api.livecall;

class LiveCall$1 implements Runnable {
   private final LiveCall this$0;

   LiveCall$1(LiveCall _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0._dtmfQueue.start();
   }
}
