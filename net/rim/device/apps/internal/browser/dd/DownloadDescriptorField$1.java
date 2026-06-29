package net.rim.device.apps.internal.browser.dd;

final class DownloadDescriptorField$1 implements Runnable {
   private final DownloadDescriptorField this$0;

   DownloadDescriptorField$1(DownloadDescriptorField _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._statusReportThread.start();
   }
}
