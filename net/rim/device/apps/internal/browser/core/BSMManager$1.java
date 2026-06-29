package net.rim.device.apps.internal.browser.core;

class BSMManager$1 extends Thread {
   private final BSMManager this$0;

   BSMManager$1(BSMManager _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.prepareConnectData();
   }
}
