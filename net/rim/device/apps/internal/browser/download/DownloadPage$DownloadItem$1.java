package net.rim.device.apps.internal.browser.download;

class DownloadPage$DownloadItem$1 implements Runnable {
   private final String val$errorMessage;
   private final DownloadPage$DownloadItem this$1;

   DownloadPage$DownloadItem$1(DownloadPage$DownloadItem _1, String _2) {
      this.this$1 = _1;
      this.val$errorMessage = _2;
   }

   @Override
   public void run() {
      if (this.this$1._gauge != null) {
         this.this$1.delete(this.this$1._gauge);
         this.this$1._gauge = null;
         this.this$1._speed.setText(this.val$errorMessage);
      }
   }
}
