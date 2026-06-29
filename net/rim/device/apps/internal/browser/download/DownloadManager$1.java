package net.rim.device.apps.internal.browser.download;

import net.rim.device.api.browser.field.CancelRequestResource;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.apps.internal.browser.stack.FetchRequest;

class DownloadManager$1 extends Thread {
   private final DownloadManager this$0;

   DownloadManager$1(DownloadManager _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      if (!(this.this$0._currentResourceRequest instanceof FetchRequest)) {
         if (this.this$0._renderingApp != null && this.this$0._currentResourceRequest instanceof RequestedResource) {
            this.this$0._renderingApp.eventOccurred(new CancelRequestResource(this, (RequestedResource)this.this$0._currentResourceRequest));
         }
      } else {
         ((FetchRequest)this.this$0._currentResourceRequest).abort();
      }
   }
}
