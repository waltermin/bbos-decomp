package net.rim.plazmic.internal.contentpreview.device.apps;

import java.util.TimerTask;

final class ContentPreviewPMEBrowserField$1 extends TimerTask {
   private final ContentPreviewPMEBrowserField this$0;

   ContentPreviewPMEBrowserField$1(ContentPreviewPMEBrowserField _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.updateTime(this.this$0.getMediaPlayer().getMediaTime());
   }
}
