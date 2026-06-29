package net.rim.device.apps.internal.docview.gui;

class BaseParsingThread$1 extends Thread {
   private final BaseParsingThread this$0;

   BaseParsingThread$1(BaseParsingThread _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.notifyWaitingThreads();
   }
}
