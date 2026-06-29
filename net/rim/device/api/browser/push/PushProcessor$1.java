package net.rim.device.api.browser.push;

class PushProcessor$1 implements Runnable {
   private final PushProcessor this$0;

   PushProcessor$1(PushProcessor _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      PushProcessor._instance.doPPGConnections();
   }
}
