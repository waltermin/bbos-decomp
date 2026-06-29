package net.rim.device.apps.internal.bis.api.ui;

final class RefreshableScreen$2 implements Runnable {
   private final RefreshableScreen this$0;

   RefreshableScreen$2(RefreshableScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.showStatusMessage();
   }
}
