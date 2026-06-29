package net.rim.device.apps.internal.bis.api.ui;

final class BasicScreen$1 implements Runnable {
   private final BasicScreen this$0;

   BasicScreen$1(BasicScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.resetFonts();
   }
}
