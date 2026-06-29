package net.rim.device.apps.internal.qm.peer.common;

final class QmRenderScreen$4 extends Thread {
   private final Runnable val$runnable;
   private final QmRenderScreen this$0;

   QmRenderScreen$4(QmRenderScreen _1, Runnable _2) {
      this.this$0 = _1;
      this.val$runnable = _2;
   }

   @Override
   public final void run() {
      this.val$runnable.run();
   }
}
