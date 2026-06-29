package net.rim.device.apps.internal.browser.download;

import net.rim.device.apps.internal.browser.resources.BrowserResources;

class DownloadPage$DownloadItem$2 implements Runnable {
   private final DownloadPage$DownloadItem this$1;

   DownloadPage$DownloadItem$2(DownloadPage$DownloadItem _1) {
      this.this$1 = _1;
   }

   @Override
   public void run() {
      if (this.this$1._gauge != null) {
         this.this$1.delete(this.this$1._gauge);
         this.this$1._gauge = null;
         this.this$1._speed.setText(BrowserResources.getString(885) + " (" + this.this$1.getTransferRate(this.this$1._dm.getTransferRate()) + ')');
      }
   }
}
