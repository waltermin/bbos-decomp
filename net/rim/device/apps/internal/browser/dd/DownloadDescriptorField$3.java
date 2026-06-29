package net.rim.device.apps.internal.browser.dd;

import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.device.apps.internal.browser.util.QuincyUtil;

final class DownloadDescriptorField$3 implements Runnable {
   private final Dialog val$nextStepDialog;
   private final DownloadDescriptorField this$0;

   DownloadDescriptorField$3(DownloadDescriptorField _1, Dialog _2) {
      this.this$0 = _1;
      this.val$nextStepDialog = _2;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      try {
         HttpHeaders requestHeaders = (HttpHeaders)(new Object());
         String nextURL = null;
         if (this.val$nextStepDialog.doModal() == 0 && this.this$0._savedFilename != null) {
            label34:
            try {
               nextURL = this.this$0._savedFilename;
               RenderingUtilities.setTranscodeHeader(requestHeaders, false);
            } catch (Throwable var8) {
               QuincyUtil.sendQuincy(t, false);
               break label34;
            }
         }

         this.this$0.goToNextURL(nextURL, requestHeaders);
      } catch (Throwable var9) {
         QuincyUtil.sendQuincy(t, false);
         this.this$0.goBack(false);
         return;
      }
   }
}
