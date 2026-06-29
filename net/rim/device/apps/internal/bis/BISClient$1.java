package net.rim.device.apps.internal.bis;

final class BISClient$1 implements Runnable {
   private final BISClient this$0;

   BISClient$1(BISClient _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.shutdown();
   }
}
