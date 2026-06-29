package net.rim.device.apps.internal.qm.peer.common;

final class QmRenderScreen$3 implements Runnable {
   private final QmRenderScreen this$0;

   QmRenderScreen$3(QmRenderScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0.isDisplayed()) {
         this.this$0.close();
      }
   }
}
