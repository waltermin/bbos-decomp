package net.rim.device.apps.internal.qm.peer.common;

final class QmRenderScreen$2 extends Thread {
   private final QmRenderScreen this$0;

   QmRenderScreen$2(QmRenderScreen _1) {
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      try {
         QmRenderScreen.access$600(this.this$0).finishLoading();
      } catch (Throwable var3) {
         e.printStackTrace();
         return;
      }
   }
}
