package net.rim.device.apps.internal.browser.push;

class BasePushModel$1 implements Runnable {
   private final BasePushModel this$0;

   BasePushModel$1(BasePushModel _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      synchronized (this.this$0._activateObject) {
         this.this$0._activateObject.notify();
      }
   }
}
